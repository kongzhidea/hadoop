package com.kk.util;

public class ArgsUtil {
    // 有时 class当第一个参数传进来，把class参数过滤掉，  简单认为如果第一个参数 以com.开头则认为是class
    public static String[] filterClass(String[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        String first = args[0];
        if (first != null && first.startsWith("com.")) {
            String[] ret = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                ret[i - 1] = args[i];
            }
            return ret;
        }
        return args;
    }
}
