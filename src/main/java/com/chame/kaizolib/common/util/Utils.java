package com.chame.kaizolib.common.util;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private final static String[] qualityList = new String[]{"480p", "540p", "720p", "1080p", "360p", "576p", "1920x1080",
                                                             "1280x720", "xvid", "dvd"};
    private final static String[] fileTypes = new String[]{"mp4", "mkv", "avi", "mov", "ts"};

    private static Map<String, String> qualityMap = new HashMap<String , String>() {
        {
            put("360p", "360p");
            put("480p", "480p");
            put("848x480", "480p");
            put("540p", "540p");
            put("720p", "720p");
            put("1280x720", "720p");
            put("bd720", "720p");
            put("1080p", "1080p");
            put("1920x1080", "1080p");
            put("2160p", "2160p");
            put("3840x2160", "2160p");
            put("dvd", "DVD");
            put("xvid", "DVD");
            put("dvix", "DVD");
        }};

    public static String qualityFromFilename(String filename) {
        filename = filename.toLowerCase();

        for (Map.Entry<String,String> entry : qualityMap.entrySet()) {
            if (filename.contains(entry.getKey())){
                return entry.getValue();
            }
        }

//        for (String quality : qualityList){
//            if (filename.contains(quality)){
//                return quality;
//            }
//        }
        return "";
    }

    public static String extensionFromFilename(String filename) {
        filename = filename.toLowerCase();

        if (filename.length() < 3) {
            return "Unknown";
        }

        String extension = filename.substring(filename.length() - 3);

        for (String fileType : fileTypes) {
            if (extension.contains(fileType)){
                return fileType;
            }
        }
        return "Non-Video";
    }

    public static String cleanFilename(String filename) {
        //Remove file extension
        if (filename.length() > 3) {
            filename = filename.substring(0, filename.length() - 3);
        }

        return filename.replaceAll("\\[.*?\\]","")
                .replace("_", " ")
                .replace(".", " ")
                .trim();
    }
}
