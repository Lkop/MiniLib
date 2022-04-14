import javassist.*;

public class ClassCreator {

    private ClassPool old_classpool, new_classpool;
    private CtClass new_class;

    public ClassCreator(){
        this.old_classpool = ClassPool.getDefault();
        this.new_classpool = new ClassPool();
        this.new_classpool.appendClassPath(new LoaderClassPath(this.old_classpool.getClassLoader()));
    }

    public ClassPool getNewClassPool() {
        return new_classpool;
    }

    public boolean copyExistingSuperclass(String class_longname, String method_name, CtClass[] params) {
        initializeCtClass(class_longname);
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            CtMethod old_method = old_class.getDeclaredMethod(method_name, params);
            new_class.addMethod(CtNewMethod.copy(old_method, new_class, null));
        }catch(CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingInterface(String interface_longname, String parent_longname) {
        try {
            CtClass old_class = old_classpool.getCtClass(interface_longname);
            new_classpool.makeClass(old_class.getClassFile2());

            new_class = new_classpool.getCtClass(interface_longname);
            CtClass parent_class = new_classpool.getCtClass(parent_longname);
            parent_class.addInterface(new_class);
        }catch(NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingMethod(String class_longname, String method_name, CtClass[] params) {
        initializeCtClass(class_longname);
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            CtMethod old_method = old_class.getDeclaredMethod(method_name, params);
            CtMethod new_method = CtNewMethod.copy(old_method, new_class, null);
            new_class.addMethod(new_method);
        }catch(CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean copyExistingConstructor(String class_longname, CtClass[] params) {
        initializeCtClass(class_longname);
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            CtConstructor ct_constructor = old_class.getDeclaredConstructor(params);
            new_class.addConstructor(CtNewConstructor.copy(ct_constructor, new_class, null));
        }catch(CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private CtClass initializeCtClass(String class_longname) {
        try {
            new_class = new_classpool.getCtClass(class_longname);
        } catch (NotFoundException e) {
            new_class = new_classpool.makeClass(class_longname);
            copyExistingFields(class_longname);
        }
        return new_class;
    }

    public boolean copyExistingFields(String class_longname) {
        try {
            CtClass old_class = old_classpool.getCtClass(class_longname);
            for(CtField ct_field : old_class.getDeclaredFields()) {
                new_class.addField(new CtField(ct_field, new_class));
            }
        } catch (CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }
        return true;
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
