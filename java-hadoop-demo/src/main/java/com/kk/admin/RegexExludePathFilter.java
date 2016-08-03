package com.kk.admin;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * 该接口只需实现其中的一个方法即可，即accpet方法，方法返回true时表示被过滤掉
 */
public class RegexExludePathFilter implements PathFilter {

    private final String regex;

    public RegexExludePathFilter(String regex) {
        this.regex = regex;
    }

    public boolean accept(Path path) {
        return !path.getName().matches(regex);
    }
}