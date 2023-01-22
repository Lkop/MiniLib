package org.lkop.minilib.utils;

public class StringUtils {

    public static String classToPath(String class_name) {
        if (class_name != null && class_name.length() > 0 ) {
            int end_index = class_name.lastIndexOf(".");
            if (end_index != -1) {
                String remove_end = class_name.substring(0, end_index);
                return remove_end.replace(".", "\\");
            }
        }
        return class_name;
    }

    public static String longToShortClassname(String long_name) {
        if (long_name != null && long_name.length() > 0 ) {
            int end_index = long_name.lastIndexOf(".");
            if (end_index != -1) {
                return long_name.substring(end_index+1);
            }
        }
        return long_name;
    }
}
