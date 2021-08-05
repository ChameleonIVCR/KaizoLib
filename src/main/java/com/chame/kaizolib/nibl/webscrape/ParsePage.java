package com.chame.kaizolib.nibl.webscrape;

import com.chame.kaizolib.common.model.Result;

import com.chame.kaizolib.common.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ParsePage {

    public static List<Result> parse(String response) {
        Document page = Jsoup.parse(response);
        Elements items = page.select("tbody > tr");
        List<Result> results = new ArrayList<Result>();

        for (Element item : items) {
            String bot = item.child(0).children().text();
            String pack = item.child(1).text();
            String size = item.child(2).text();
            String filename = item.child(3).text();
            filename = filename.replace("_"," ");
            results.add(
                    new Result(bot,
                            pack,
                            size,
                            Utils.qualityFromFilename(filename),
                            Utils.extensionFromFilename(filename),
                            Utils.cleanFilename(filename))
            );
        }

        return results.isEmpty() ? null : results;
    }
}
