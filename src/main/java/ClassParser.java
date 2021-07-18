import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassParser {

    private String jar_path;

    public ClassParser(String jar_path) {
        this.jar_path = jar_path;
    }

    public List getClasses() {
        List<String> class_list = new ArrayList<>();
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
        return class_list;
    }
}
