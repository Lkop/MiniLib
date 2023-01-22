package org.lkop.minilib.models;

public class ClassInfo {

    private String class_name;
    private String method_name;

    public ClassInfo(String class_name, String method_name) {
        this.class_name = class_name;
        this.method_name = method_name;
    }

    public String getClassName() {
        return class_name;
    }

    public String getMethodName() {
        return method_name;
    }
}
