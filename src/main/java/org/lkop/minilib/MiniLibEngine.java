package org.lkop.minilib;

import java.io.File;
import java.util.List;

public class MiniLibEngine {

    private String target_jar;
    private String dependencies_folder;
    private String output_folder;
    private FileFinder file_finder;
    private ClassParser class_parser;

    public MiniLibEngine() {
        setDependenciesFolderOS();
        setOutputFolderOS();

        this.file_finder = new FileFinder();
        this.class_parser = new ClassParser();

        //generateCode(null, null);
    }

    public void setTargetJar(String target_jar) {
        this.target_jar = target_jar;
        file_finder.addJar(target_jar);
    }

    public void setDependenciesFolder(String dependencies_folder) {
        this.dependencies_folder = dependencies_folder;
    }

    private void setOutputFolderOS() {
        setOutputFolder(".");
    }

    public void setOutputFolder(String output_folder) {
        this.output_folder = output_folder + "/minilib-output";
        File directory = new File(this.output_folder);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }


//    public void generateCode(Class<?> clazz, Method method) {
//        generateCodeCore(clazz.getName(), method.getName());
//    }
//
//    public void generateCode(Class<?> clazz, Method method) {
//        try {
//            Class<?> clazz = Class.forName(clazz_str);
//            Method method = clazz.getDeclaredMethod(method_str);
//            this.generateCode(clazz, method);
//        } catch (ClassNotFoundException | NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }

    public void generateCodeCore(String clazz_str, String method_str) {




        //TODO FIX
//        dependencies_folder = "C:\\Users\\Loukas\\.m2";
        dependencies_folder = "C:\\Users\\Loukas\\Desktop\\jars\\pmpr";

        List<File> jar_list = file_finder.findAll(dependencies_folder, ".jar");
        if (jar_list.size() == 0) {
            System.out.println("MiniLib Error - Maven folder (" + dependencies_folder + ") is empty");
            return;
        }




        //!important
        //jar_list.add(new File("L:\\Secret_Projects\\MiniLib_workspace\\Client\\out\\artifacts\\Client_jar\\Client.jar"));

        //Populate clazzes list
        //class_parser.parseTargetJar("C:\\Users\\Loukas\\Desktop\\tutorial-NOMS24-main\\target\\ReputationSystem-1.0.jar");
        class_parser.parseDependenciesJars(jar_list);

        ClassInsider class_insider = new ClassInsider(jar_list);

        List<String> a = class_parser.extractClazzesFromJar(target_jar);

        class_insider.getAllMethods(target_jar, a);

//        class_insider.getAllMethods(target_jar, class_parser.getClazzes());


        class_insider.setStartingMethod(clazz_str, method_str);
        class_insider.listCalledMethods(class_parser.getClazzes());



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
