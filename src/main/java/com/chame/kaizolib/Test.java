package com.chame.kaizolib;

import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizolib.irc.exception.NoQuickRetryException;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Test {
    public static void main(String[] args) {
        Nibl nibl = new Nibl();
        List<Result> results = nibl.search("Full Metal Alchemist Brotherhood");
        IrcClient irc = new IrcClient(results.get(1).getCommand());
        try {
            DCC dcc = irc.execute();

            File download = new File("F:\\test\\"+dcc.getFilename());
            download.createNewFile();
            DCCDownloader dccDl = new DCCDownloader(dcc, download);
            dccDl.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoQuickRetryException e) {
            e.printStackTrace();
        }
    }
}
