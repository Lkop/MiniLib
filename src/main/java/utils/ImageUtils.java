package utils;

import java.io.IOException;

public final class ImageUtils {

    private ImageUtils() {

    }

    public static void createGIF(String filename) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start dot -Tgif output/"+filename+".dot -o output/"+filename+".gif");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        System.out.println("Exit Code: "+process.waitFor());
    }

}
