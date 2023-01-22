package org.lkop.minilib;

import javassist.*;
import java.util.List;

public class ClassCreator {

    private ClassPool old_classpool, new_classpool;
    private CtClass new_class;
    private List<String> one_time_classes;

    public ClassCreator(){
        this.old_classpool = ClassPool.getDefault();
        this.new_classpool = new ClassPool();
        this.new_classpool.appendClassPath(new LoaderClassPath(this.old_classpool.getClassLoader()));
    }

    public ClassPool getNewClassPool() {
        return new_classpool;
    }

    public void parseGeneralInfo(GeneralInfo general_info) {
        one_time_classes = general_info.getOneTimeClasses();
    }

    public boolean addEmptyFieldClass(String class_longname) {
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            new_class = initializeCtClass(class_longname);

            //!important - Fields added in previous step (initializeCtClass)
//            for(CtField ct_field : old_class.getDeclaredFields()) {
//                new_class.addField(new CtField(ct_field, new_class));
//            }
        }catch(NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingSuperclass(String superclass_longname,  String parent_longname) {
        try {
            CtClass old_class = old_classpool.getCtClass(superclass_longname);
            new_class = initializeCtClass(superclass_longname);
            //new_class = new_classpool.makeClass(superclass_longname);
            //new_class.setModifiers(old_class.getModifiers());

//            CtClass parent_class = new_classpool.getCtClass(parent_longname);
            CtClass parent_class = initializeCtClass(parent_longname);
            parent_class.setSuperclass(new_class);

        }catch(NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingInterface(String interface_longname, String parent_longname) {
        try {
//            CtClass old_class = old_classpool.getCtClass(interface_longname);
//            new_class = new_classpool.makeClass(old_class.getClassFile2());
//            new_class.setModifiers(old_class.getModifiers());
//
//            //new_class = new_classpool.getCtClass(interface_longname);
//            CtClass parent_class = new_classpool.getCtClass(parent_longname);
//            parent_class.addInterface(new_class);

            CtClass old_class = old_classpool.getCtClass(interface_longname);
            new_class = new_classpool.makeClass(old_class.getClassFile2());
            CtClass parent_class = initializeCtClass(parent_longname);
            //parent_class.addInterface(new_class);

        }catch(NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingMethod(String class_longname, String method_name, CtClass[] params) {
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            CtMethod old_method = old_class.getDeclaredMethod(method_name, params);
            new_class = initializeCtClass(class_longname);
            new_class.addMethod(CtNewMethod.copy(old_method, new_class, null));
        } catch(CannotCompileException | NotFoundException e) {
            //e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingConstructor(String class_longname, CtClass[] params) {
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);


            CtConstructor ct_constructor = old_class.getDeclaredConstructor(params);
            new_class = initializeCtClass(class_longname);
            new_class.addConstructor(CtNewConstructor.copy(ct_constructor, new_class, null));
        }catch(CannotCompileException | NotFoundException e) {

        }
        return true;
    }

    private CtClass initializeCtClass(String class_longname) {
        CtClass new_class = null;
        try {
            new_class = new_classpool.getCtClass(class_longname);
        } catch (NotFoundException e) {
            try {
                CtClass old_class = old_classpool.getCtClass(class_longname);

                //Check for internal/anonymous classes ($1, $2, $...)
                //Cannot create an anonymous class from scratch
                if(old_class.getDeclaringClass() != null){
                    return new_classpool.makeClass(old_class.getClassFile2());
                }

                //Create empty Class
                new_class = new_classpool.makeClass(class_longname);

                //Copy Modifiers (final, abstract, static etc)
                new_class.setModifiers(old_class.getModifiers());

                //Copy Fields
                for(CtField ct_field : old_class.getDeclaredFields()) {
                    String field_type = ct_field.getType().getName();





//                    if(field_type.startsWith("java.") || one_time_classes.contains(ct_field.getType().getName())) {
//                        new_class.addField(new CtField(ct_field, new_class));
//                    }
                    new_class.addField(new CtField(ct_field, new_class));
                    initializeCtClass(field_type);
//                    try {
//                        ClassFile cf = new_classpool.get(class_node.getClassLongName()).getClassFile();
//                        File file = new File(root_path + "\\" + class_node.getClassPath() + "\\" + class_node.getClassName() + ".class");
//                        cf.write(new DataOutputStream(new FileOutputStream(file)));
//                    }catch (NotFoundException | IOException e) {
//                        e.printStackTrace();
//                    }
                }
            } catch (NotFoundException | CannotCompileException ex) {
                ex.printStackTrace();
            }
        }
        return new_class;
    }

//    private void createClassFile(String class_name) {
//        try {
//            ClassFile cf = new_classpool.get(class_name).getClassFile();
//            File file = new File("output/" + classname + ".class");
//            cf.write(new DataOutputStream(new FileOutputStream(file)));
//        }catch (NotFoundException | IOException e) {
//            e.printStackTrace();
//        }
//    }

}
