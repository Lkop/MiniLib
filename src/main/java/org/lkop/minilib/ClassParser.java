package org.lkop.minilib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassParser {

    private List<String> clazz_list;
//    private List<String> clazz_list_target;

    public ClassParser() {
        clazz_list = new ArrayList<>();
//        clazz_list_target = new ArrayList<>();
    }

    public List<String> getClazzes() {
        return clazz_list;
    }

//    public List<String> getClazzesTarget() {
//        return clazz_list_target;
//    }


    public void parseTargetJar(String target_jar_absolute_path) {
        this.parseSingleJar(target_jar_absolute_path);
    }

    /**
     * Scans a list of JAR files and extracts all {@code .class} files.
     *
     * @param jar_list List of JAR's
     */
    public void parseDependenciesJars(List<File> jar_list) {
        for (File jar_file : jar_list) {
            this.parseSingleJar(jar_file.getAbsolutePath());
        }
    }

    private void parseSingleJar(String jar_absolute_path) {
        List<String> tmp_list = this.extractClazzesFromJar(jar_absolute_path);
        clazz_list = Stream.concat(clazz_list.stream(), tmp_list.stream()).toList();
    }

    public List<String> extractClazzesFromJar(String jar_path) {
        List<String> list = new ArrayList<>();
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jar_path));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String class_fullname = entry.getName().replace('/', '.'); // including ".class"
                    list.add(class_fullname.substring(0, class_fullname.length() - ".class".length()));
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
