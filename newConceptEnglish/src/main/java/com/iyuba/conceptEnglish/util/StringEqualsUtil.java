package com.iyuba.conceptEnglish.util;

//模糊比较两个字符串是否相等,忽略符号，忽略大小写，忽略全角与半角
public class StringEqualsUtil {
    public static boolean isEquals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        if (str1.equalsIgnoreCase(str2)) {
            return true;
        } else {
            String s1 = str1.replaceAll("[\\p{Punct}\\p{Space}]+", "");
            String s2 = str2.replaceAll("[\\p{Punct}\\p{Space}]+", "");
            if (s1.equalsIgnoreCase(s2)) {
                return true;
            } else {
                char[] chars1 = s1.toCharArray();
                char[] chars2 = s2.toCharArray();
                for (int i = 0; i < chars1.length; i++) {
                    if (chars1[i] == '\u3000') {
                        chars1[i] = ' ';
                    } else if (chars1[i] > '\uFF00' && chars1[i] < '\uFF5F') {
                        chars1[i] = (char) (chars1[i] - 65248);
                    }
                }
                for (int i = 0; i < chars2.length; i++) {
                    if (chars2[i] == '\u3000') {
                        chars2[i] = ' ';
                    } else if (chars2[i] > '\uFF00' && chars2[i] < '\uFF5F') {
                        chars2[i] = (char) (chars2[i] - 65248);
                    }
                }
                s1 = new String(chars1);
                s2 = new String(chars2);
                return s1.equalsIgnoreCase(s2);
            }
        }
    }
}
