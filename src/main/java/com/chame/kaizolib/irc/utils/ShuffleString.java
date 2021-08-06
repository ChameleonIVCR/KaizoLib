package com.chame.kaizolib.irc.utils;

import java.util.Random;

public class ShuffleString {
    public static String shuffle (String s) {
        StringBuffer result = new StringBuffer(s);
        int n = result.length();
        Random rand = new Random();
        while (n>1) {
            int randomPoint = rand.nextInt(n);
            char randomChar = result.charAt(randomPoint);
            result.setCharAt(n-1,randomChar);
            n--;
        }
        return result.toString();
    }
}
