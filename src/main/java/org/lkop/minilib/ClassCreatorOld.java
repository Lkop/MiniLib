package org.lkop.minilib;

import javassist.*;
import javassist.bytecode.ClassFile;
import java.io.*;

public class ClassCreatorOld {

    private String classname;
    private ClassPool class_pool;
    private CtClass new_class;

    public ClassCreatorOld(String classname){
        this.classname = classname;
        this.class_pool = ClassPool.getDefault();
        this.new_class = class_pool.makeClass(classname);
    }

    public void addMethod(StringBuilder method) {
        addMethodToClass(method.toString());
    }

    public void addMethod(String method) {
        addMethodToClass(method);
    }

    public boolean copyExistingMethod(String class_name, String method_name) {
        try {
            CtClass selected_class = class_pool.getCtClass(class_name);
            new_class.addMethod(CtNewMethod.copy(selected_class.getDeclaredMethod(method_name), new_class, null));
            ////
            CtClass selected_class2 = class_pool.getCtClass("com.google.gson.Gson");
            CtConstructor[] ct_con = selected_class2.getDeclaredConstructors();
            new_class.addConstructor(CtNewConstructor.copy(ct_con[1], new_class, null));
            ////
        }catch(CannotCompileException | NotFoundException e) {
            return false;
        }
        return true;
    }

    public void createClassFile() {
        try {
            ClassFile cf = class_pool.get(classname).getClassFile();
            File file = new File("output/" + classname + ".class");
            cf.write(new DataOutputStream(new FileOutputStream(file)));
        }catch (NotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean addMethodToClass(String method){
        try {
            new_class.addMethod(CtMethod.make(method, new_class));
        }catch(CannotCompileException e) {
            return false;
        }
        return true;
    }
}
