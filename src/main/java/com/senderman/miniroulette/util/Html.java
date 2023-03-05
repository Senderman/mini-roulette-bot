package com.senderman.miniroulette.util;

public class Html {

    public static String htmlSafe(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

}
