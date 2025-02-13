package org.lkop.minilib;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class MiniLibEngine {

    private String dependencies_folder;
    private String output_folder;

    public MiniLibEngine() {
        setDependenciesFolderOS();
        setOutputFolderOS();
        //generateCode(null, null);
    }

    public void setDependenciesFolder(String dependencies_folder) {
        this.dependencies_folder = dependencies_folder;
    }

    private void setOutputFolderOS() {
        setOutputFolder(".");
    }

    public void setOutputFolder(String output_folder) {
        this.output_folder = output_folder + "/MiniLib_Output";
        File directory = new File(this.output_folder);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    public void generateCode(Class<?> clazz, Method method) {
        FileFinder jf = new FileFinder();
        List<File> jar_list = jf.findAll(dependencies_folder, ".jar");
        if (jar_list.size() == 0) {
            System.out.println("MiniLib Error - Maven folder (" + dependencies_folder + ") is empty");
            return;
        }

        //!important
        //jar_list.add(new File("L:\\Secret_Projects\\MiniLib_workspace\\Client\\out\\artifacts\\Client_jar\\Client.jar"));

        ClassParser class_parser = new ClassParser();
        List<String> classes_list = class_parser.getClasses(jar_list);

        ClassInsider class_insider = new ClassInsider(jar_list);
        class_insider.assignStartingMethod(clazz.getName(), method.getName());
        class_insider.listCalledMethods(classes_list);



        ClassElement method_tree = class_insider.getRoot();
        ((StartingMethodElement)method_tree).setGeneralInfo(new GeneralInfo(class_insider.getOneTimeClasses()));
        System.out.println("Visiting -> Done");

        TreePrinterVisitor tree_printer = new TreePrinterVisitor(output_folder);
        //tree_printer.openWriteGraph();
        class_insider.getRoot().accept(tree_printer);
        //tree_printer.closeWriteGraph();
        System.out.println("Printing -> Done");

        ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(output_folder + "/minilib_generated");
        class_insider.getRoot().accept(cgv);
        //cgv.createClassFile();
        System.out.println("Generating -> Done");
    }

    private void setDependenciesFolderOS() {
        String os_name = System.getProperty("os.name");
        if (os_name.startsWith("Windows")) {
//            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:/Users/*/m2/*");
//            try {
//                Files.walk(Paths.get(".")).forEach((path) -> {
//                    path = path.toAbsolutePath().normalize();
//                    System.out.print("Path: " + path + " ");
//                    if (pathMatcher.matches(path)) {
//                        System.out.print("matched");
//                    }
//                    System.out.println();
//                });
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            dependencies_folder = "C:\\Users\\*\\.m2";
        } else if (os_name.startsWith("Linux")) {
            dependencies_folder = "~/.m2";
        } else if (os_name.startsWith("Mac")) {
            dependencies_folder = "~/.m2";
        }
    }
}
