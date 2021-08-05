package com.chame.kaizolib.irc;

import com.chame.kaizolib.irc.model.DCC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class DCCDownloader {
    private static final Logger logger = LogManager.getLogger(DCCDownloader.class);
    private final DCC dcc;
    private final File downloadFile;
    private boolean stop = false;
    private int progress = 0;
    private int speedKBps = 0;

    public DCCDownloader(DCC dcc, File downloadFile) {
        this.dcc = dcc;
        this.downloadFile = downloadFile;
    }

    public void stop() {
        this.stop = true;
    }

    public int getProgress() {
        return progress;
    }

    public String getSpeed() {
        if (speedKBps > 1024) {
            return (speedKBps / 1024) + " Mbps";
        } else {
            return speedKBps + " Kbps";
        }
    }

    public void start() throws UnknownHostException {
        try(Socket socket = new Socket(dcc.getIp(), dcc.getPort())){
            DataInputStream inputStream
                    = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream fileOutput
                    = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(downloadFile)));
            //8192 buffer size
            byte[] buffer = new byte[8192];

            int repetitions = 0;
            long downloadedLength = 0;
            long fileLength = dcc.getSizeBits();
            long downloadStartTime = System.currentTimeMillis();

            while (true) {
                int read = inputStream.read(buffer);
                downloadedLength += read;

                if (read == -1 || downloadedLength >= fileLength || stop) {
                    break;
                }

                //Update progress every 300 repetitions to avoid calculating the progress so often.
                if (repetitions >= 300) {
                    long downloadElapsedTime = (System.currentTimeMillis() - downloadStartTime) / 1000;

                    progress = (int) (downloadedLength * 100 / fileLength);
                    speedKBps = (int) ((downloadedLength / downloadElapsedTime) / 1024);

                    logger.debug("File Received " + progress + "% at " + speedKBps);
                    repetitions = 0;
                }

                fileOutput.write(buffer, 0, read);
                repetitions += 1;
            }

            progress = 100;
            logger.debug("Receive completed, file saved as " + downloadFile.getPath() + "\n");
            inputStream.close();
            fileOutput.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
