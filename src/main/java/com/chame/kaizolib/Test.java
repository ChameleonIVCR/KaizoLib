package com.chame.kaizolib;

import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizolib.nibl.Nibl;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Nibl nibl = new Nibl();
        nibl.setNiblSuccessListener(new Nibl.NiblSuccessListener() {
            @Override
            public void onSuccess(List<Result> result) {
                search(result);
            }
        });
        nibl.search("Subsplease Jujutsu Kaisen");

    }

    private static void search(List<Result> result){
        IrcClient irc = new IrcClient(result.get(0).getCommand(), "Chame");
        try {
            DCC dcc = irc.execute();

            File download = new File("F:\\test\\"+dcc.getFilename());
            download.createNewFile();
            DCCDownloader dccDl = new DCCDownloader(dcc, download);
            dccDl.setDCCDownloadListener(new DCCDownloader.DCCDownloadListener() {
                @Override
                public void onDownloadReadyToPlay(int progress, File downloadFile) {

                }

                @Override
                public void onProgress(int progress, String speed) {
                    System.out.println(progress+"%" + "/" + speed);
                }

                @Override
                public void onFinished(File downloadFile) {

                }

                @Override
                public void onError(Exception error) {

                }
            });
            dccDl.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
