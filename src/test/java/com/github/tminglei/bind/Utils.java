package com.github.tminglei.bind;

import java.util.function.Function;

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


    ///////////////////////////////////////////////////////////////////

    public static Function<Framework.Extensible, Framework.Extensible>
                    toExt(Function<Ext, Ext> setting) {
        return (c -> {
            Ext ext = c != null ? (Ext) c : new Ext();
            return setting.apply(ext);
        });
    }

    public static class Ext implements Framework.Extensible {
        private String in = "";
        private String desc = "";

        public Ext in(String where) {
            this.in = where;
            return this;
        }
        public Ext desc(String desc) {
            this.desc = desc;
            return this;
        }

        @Override
        public Framework.Extensible clone() {
            Ext clone = new Ext();
            clone.in = this.in;
            clone.desc = this.desc;
            return clone;
        }

        ///
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Ext) {
                Ext other = (Ext) obj;
                return in.equals(other.in)
                    && desc.equals(other.desc);
            } else return false;
        }
    }
}
