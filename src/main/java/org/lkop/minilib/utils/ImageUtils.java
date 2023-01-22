package org.lkop.minilib.utils;

import java.io.IOException;

public final class ImageUtils {

    private ImageUtils() {

    }

    public static void createGIF(String filename) throws IOException, InterruptedException {
        //ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start dot -Tjpg output/"+filename+".dot -o output/"+filename+".jpg");
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start C:\\Users\\Loukas\\Desktop\\Graphviz\\bin\\dot.exe -Tjpg output/"+filename+".dot -o output/"+filename+".jpg");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        System.out.println("Exit Code: "+process.waitFor());
    }

}
