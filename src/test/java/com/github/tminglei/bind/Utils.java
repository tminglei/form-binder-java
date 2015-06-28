package com.github.tminglei.bind;

public class Utils {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLACK = "\u001B[30m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_PURPLE = "\u001B[35m";
    static final String ANSI_CYAN = "\u001B[36m";
    static final String ANSI_WHITE = "\u001B[37m";

    public static String red(String message) {
        return ANSI_RED + message + ANSI_RESET;
    }
    public static String green(String message) {
        return ANSI_GREEN + message + ANSI_RESET;
    }
    public static String blue(String message) {
        return ANSI_BLUE + message + ANSI_RESET;
    }
    public static String cyan(String message) {
        return ANSI_CYAN + message + ANSI_RESET;
    }

}
