package com.chame.kaizolib.irc;

import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizolib.irc.exception.NoQuickRetryException;
import com.chame.kaizolib.irc.utils.ShuffleString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class IrcClient {
    private static final Logger logger = LogManager.getLogger(IrcClient.class);
    private final String version = " :KaizoLib 1.0 [" + System.getProperty("os.arch") + "]";
    private final String packCommand;
    private final String bot;
    private final String nickname;
    private IrcOnSuccessListener ircClientListener;
    private IrcOnFailureListener ircFailureListener;
    private PrintWriter out;
    private Scanner in;

    public IrcClient(String packCommand, String nickname) {
        this.nickname = nickname;
        this.packCommand = packCommand;
        this.bot = packCommand.split(" ")[0];
    }

    public void setIrcOnSuccessListener(IrcClient.IrcOnSuccessListener listener){
        ircClientListener = listener;
    }

    public void setIrcOnFailureListener(IrcClient.IrcOnFailureListener fListener) {
        ircFailureListener = fListener;
    }

    public DCC execute() {
        try (Socket socket = new Socket("irc.rizon.net", 6667)) {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());

            write("NICK", nickname);
            write("USER", nickname + " 0 * :" + nickname + "-Kaizo!");

            boolean retry = false;
            boolean retryRan = false;

            long startTime = System.currentTimeMillis();
            while (in.hasNext() || (System.currentTimeMillis()-startTime)<15000) {
                String serverMessage = in.nextLine();
                System.out.println(serverMessage);

                //Automated responses to comply with IRC standard
                if (mandatoryResponses(serverMessage)) {
                    continue;
                } else if (serverMessage.split(" ")[1].contains("PRIVMSG")
                        && serverMessage.contains("\u0001DCC")) {
                    quit();
                    close();
                    final DCC fetchedDCC = parseResponse(serverMessage);
                    if (ircClientListener != null ) ircClientListener.onSuccess(fetchedDCC);
                    return fetchedDCC;
                } else if (serverMessage.contains("You already requested that pack")
                        || serverMessage.contains("You have a DCC pending")){
                    retry = true;
                    logger.warn("You have requested this resource before. You'll have to wait from 150 to " +
                            "+300 seconds before retrying.");
                }

                if (retryRan) {
                    logger.warn("Retry has been attempted, but the bot doesn't support quick-retry, you'll " +
                            "have to wait.");
                    quit();
                    throw new NoQuickRetryException();
                } else if (retry) {
                    write("PRIVMSG",
                            serverMessage.split("!")[0]
                                    .substring(1) + " :XDCC CANCEL"
                    );
                    write("PRIVMSG", packCommand);
                    retryRan = true;
                }
                logger.debug(serverMessage);
            }
        logger.warn("The IRC connection didn't respond in time, or didn't respond at all to the request.");
        throw new TimeoutException("The IRC bot didn't respond in time, or didn't respond at all.");

        } catch (NoQuickRetryException e) {
            if (ircClientListener != null ) ircFailureListener.onFailure(FailureCode.NoQuickRetry);
        } catch (TimeoutException e){
            if (ircClientListener != null ) ircFailureListener.onFailure(FailureCode.TimeOut);
        } catch (UnknownHostException e) {
            if (ircClientListener != null ) ircFailureListener.onFailure(FailureCode.UnknownHost);
        } catch (IOException e) {
            if (ircClientListener != null ) ircFailureListener.onFailure(FailureCode.IoException);
            logger.fatal("Check network or filesystem permissions.");
        } finally {
            close();
        }
        return null;
    }

    private void write(String command, String message) {
        String fullMessage = command + " " + message;
        out.print(fullMessage + "\r\n");
        out.flush();
    }

    private void quit() {
        out.print("QUIT" + "\r\n");
        out.flush();
    }

    private void close() {
        out.close();
        in.close();
    }

    private boolean mandatoryResponses(String receivedMessage) {
        if (receivedMessage.startsWith("PING")) {
            write("PONG", receivedMessage.split(" ")[1]);
            return true;
        } else if (receivedMessage.contains("\u0001VERSION\u0001")) {
            write("PRIVMSG",
                    receivedMessage
                            .split("!")[0]
                            .substring(1) + this.version);
            return true;
        } else if (receivedMessage.contains("End of /MOTD command")) {
            write("JOIN", "#nibl");
            return true;
        } else if (receivedMessage.contains("End of /NAMES list")) {
            write("PRIVMSG", packCommand);
            return true;
        } else if (receivedMessage.contains("Nickname is already in use")
                && receivedMessage.split(" ")[0].contains(":irc.rizon.io")){

            String shuffledNickname = ShuffleString.shuffle(nickname);

            write("NICK", shuffledNickname);
            write("USER", shuffledNickname + " 0 * :" + shuffledNickname + "-Kaizo!");
            return true;
        }
        return false;
    }

    private DCC parseResponse(String message) {
        String[] msg = message.split("\u0001")[1].split(" ");

        String filename = String.join(
                " ", Arrays.copyOfRange(msg, 2, msg.length-3))
                .replace("\"", "");

        long ip = Long.parseLong(msg[msg.length-3]);

        String ipAddress = String.format("%d.%d.%d.%d",
                (ip >> 24 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 8 & 0xff),
                (ip & 0xff));


        return new DCC(filename,
                ipAddress,
                Integer.parseInt(msg[msg.length-2]),
                Long.parseLong(msg[msg.length-1])
        );
    }

    public enum FailureCode{
        TimeOut,
        UnknownHost,
        NoQuickRetry,
        IoException
    }

    public interface IrcOnSuccessListener{
        void onSuccess(DCC dcc);
    }

    public interface IrcOnFailureListener{
        void onFailure(FailureCode f);
    }
}
