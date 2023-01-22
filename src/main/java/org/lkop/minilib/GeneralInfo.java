package org.lkop.minilib;

import java.util.List;

public class GeneralInfo {

    private List<String> one_time_classes;

    public GeneralInfo(List<String> one_time_classes) {
        this.one_time_classes = one_time_classes;
    }

    public List<String> getOneTimeClasses() {
        return one_time_classes;
    }
}
