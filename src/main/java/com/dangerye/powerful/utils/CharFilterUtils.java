package com.dangerye.powerful.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharFilterUtils {

    // "^[0-9a-zA-Z\u4e00-\u9fa5~!@#$%^\\*()\\-_+?:,\\.;，。、‘：“《》？~！@#￥%……（）•·\\s]*$"
    private static final String CHAR_FILTER_PATTERN_REGEX = "^[0-9a-zA-Z]*$";
    private static final Pattern CHAR_FILTER_PATTERN = Pattern.compile(CHAR_FILTER_PATTERN_REGEX);

    private static boolean isFilterChar(String str) {
        Matcher matcher = CHAR_FILTER_PATTERN.matcher(str);
        return !matcher.matches();
    }

    public static String filterChar(String str, String replaceWith) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (isFilterChar(String.valueOf(c))) {
                if (replaceWith != null) {
                    stringBuilder.append(replaceWith);
                }
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
