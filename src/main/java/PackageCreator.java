import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import org.apache.commons.io.FileUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PackageCreator {

    private String root_path = "output\\Generated";
    private int counter;
    private ClassCreator class_creator;
    private ClassPool new_classpool;

    public PackageCreator() {
        deleteRootFolder();
        this.counter = 0;
        this.class_creator = new ClassCreator();
        this.new_classpool = class_creator.getNewClassPool();
    }

    public void addToPackage(MethodElement node) {
        createPackageFolder(node.getClassPath());
        switch (node.getType()){
            case METHOD_CALL:
                class_creator.copyExistingMethod(node.getClassLongName(), node.getMethodName(), node.getParams());
                break;
            case CONSTRUCTOR_CALL:
                class_creator.copyExistingConstructor(node.getClassLongName(), node.getParams());
                break;
        }
        saveClassInPackage(node.getClassPath(), node.getClassLongName(), node.getClassName());
    }

    private void createPackageFolder(String folder_path) {
        File folder = new File(root_path + "\\" + folder_path);
        folder.mkdirs();
    }

    private void saveClassInPackage(String class_path, String class_longname, String class_name) {
        try {
            ClassFile cf = new_classpool.get(class_longname).getClassFile();
            File file = new File(root_path + "\\" + class_path + "\\" + class_name + ".class");
            cf.write(new DataOutputStream(new FileOutputStream(file)));
        }catch (NotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRootFolder() {
        try {
            FileUtils.deleteDirectory(new File(root_path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
