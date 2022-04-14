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
    public void addInterface(InterfaceElement node) {
        createPackageFolder(node.getClassPath());
        class_creator.copyExistingInterface(node.getInterfaceLongName(), node.getParentClass().getClassLongName());
        saveClassesInPackage(node, node.getParentClass());
    }

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
        new File(root_path + "\\" + folder_path).mkdirs();
    }

    private void saveClassesInPackage(ClassElement... class_nodes) {
        for (ClassElement class_node : class_nodes) {
            try {
                ClassFile cf = new_classpool.get(class_node.getClassLongName()).getClassFile();
                File file = new File(root_path + "\\" + class_node.getClassPath() + "\\" + class_node.getClassName() + ".class");
                cf.write(new DataOutputStream(new FileOutputStream(file)));
            }catch (NotFoundException | IOException e) {
                e.printStackTrace();
            }
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
