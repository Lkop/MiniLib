package org.lkop.minilib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private List<File> jar_list;
    private List<String> jar_list_string;

    public FileFinder() {
        jar_list = new ArrayList<>();
        jar_list_string = new ArrayList<>();
    }

    public File find(String file, String folder_path) {
        return new File("");
    }

    public void addJar(String file) {
        this.jar_list.add(new File(file));
        this.jar_list_string.add(file);
    }

    public List<File> findAll(String folder_path, String extension) {
        File folder = new File(folder_path);
        listOfFilesExtensionRecursive(folder, extension);

        return jar_list;
    }

    public void listOfFilesRecursive(File folder_file) {
        File[] all_files = folder_file.listFiles();

        for(File file : all_files) {
            if(file.isFile()) {
                jar_list.add(file);
                System.out.println("File path: " + file.getName());
            }else{
                listOfFilesRecursive(file);
            }
        }
    }

    public void listOfFilesExtensionRecursive(File folder_file, String extension) {
        File[] all_files = folder_file.listFiles();
        if (all_files == null) {
            return;
        }

        for(File file : all_files) {
            if(file.isFile()) {
                if(file.getName().endsWith(extension)) {
                    jar_list.add(file);
                    jar_list_string.add(file.getName());
                }
            }else{
                listOfFilesExtensionRecursive(file, extension);
            }
        }
    }
}
