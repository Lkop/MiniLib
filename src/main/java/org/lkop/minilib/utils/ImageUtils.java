package org.lkop.minilib.utils;

import org.lkop.minilib.constants.Constants;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {

    private ImageUtils() {

    }

    public static void createGIF(String input_file_path) throws IOException, InterruptedException {
    /*    File input_file = new File(input_file_path);
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start dot -Tjpg " + input_file_path + " -o " + input_file.getParent() + "/" + Constants.OUTPUT_FILENAME + ".jpg"); // Needs fixing for linux and mac
        pb.redirectErrorStream(true);
        Process process = pb.start();
        System.out.println("Exit Code: " + process.waitFor());*/
    }

}
