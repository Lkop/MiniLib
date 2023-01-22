package org.lkop.minilib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassParser {

    private List<String> class_list;

    public ClassParser() {
        class_list = new ArrayList<>();
    }

    public List getClasses(String jar_path) {
        this.getClassesPrivate(jar_path);
        return class_list;
    }

    public List getClasses(List<File> jar_list) {
        for (File jar_file : jar_list) {
            this.getClassesPrivate(jar_file.getAbsolutePath());
        }
        return class_list;
    }

    private void getClassesPrivate(String jar_path) {
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jar_path));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String class_fullname = entry.getName().replace('/', '.'); // including ".class"
                    class_list.add(class_fullname.substring(0, class_fullname.length() - ".class".length()));
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
