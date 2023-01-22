import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import org.apache.commons.io.FileUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PackageCreator {

    private String root_path;
    private ClassCreator class_creator;
    private ClassPool new_classpool;

    public PackageCreator(String root_path) {
        this.root_path = root_path;
        deleteRootFolder();
        this.class_creator = new ClassCreator();
        this.new_classpool = class_creator.getNewClassPool();
    }

    public void parseGeneralInfo(StartingMethodElement node) {
        class_creator.parseGeneralInfo(node.getGeneralInfo());
    }

    public void addEmptyFieldsClass(EmptyClassElement node) {
        createPackageFolder(node);
        class_creator.addEmptyFieldClass(node.getClassLongName());
        saveClassesInPackage(node);
    }

    public void addSuperclass(SuperclassElement node) {
        createPackageFolder(node, node.getParentClass());
        class_creator.copyExistingSuperclass(node.getSuperclassLongName(), node.getParentClass().getClassLongName());
        saveClassesInPackage(node, node.getParentClass());
    }

    public void addInterface(InterfaceElement node) {
        createPackageFolder(node, node.getParentClass());
        class_creator.copyExistingInterface(node.getInterfaceLongName(), node.getParentClass().getClassLongName());
        saveClassesInPackage(node, node.getParentClass());
    }

    public void addMethod(MethodElement node) {
        createPackageFolder(node);
        switch (node.getType()){
            case METHOD_CALL:
                class_creator.copyExistingMethod(node.getClassLongName(), node.getMethodName(), node.getMethodParams());
                break;
            case CONSTRUCTOR_CALL:
                class_creator.copyExistingConstructor(node.getClassLongName(), node.getMethodParams());
                break;
        }
        saveClassesInPackage(node);
    }

    private void createPackageFolder(ClassElement... class_nodes) {
        for (ClassElement class_node : class_nodes) {
            new File(root_path + "\\" + class_node.getClassPath()).mkdirs();
        }
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
