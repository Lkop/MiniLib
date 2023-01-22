import constants.Constants;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;
import javassist.expr.*;
import models.ClassInfo;
import javassist.*;
import treecomponents.BaseTreeElement;

import java.io.File;
import java.util.*;

public class ClassInsider {

    private ClassPool class_pool;
    private String starting_class;
    private String starting_method;
    private CtClass[] params;
    private ClassElement root = null;
    private ClassElement current_parent = null;
    private Stack<ClassElement> parents_stack = new Stack<>();
    private String previous_class = null;
    private MethodElement new_node;                 //Accessing previous element to change parent superclass

    private List<String> keep_only_classes;
    private List<String> one_time_classes;
    private List<String> one_time_methods;
    private List<String> one_time_field_classes;
    private List<String> one_time_extras;

    public ClassInsider(String jar_path) {
        class_pool = ClassPool.getDefault();

        try {
            class_pool.appendClassPath(jar_path);
            //class_pool.insertClassPath(jar_path);
        }catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public ClassInsider(List<File> jar_list) {
        class_pool = ClassPool.getDefault();
        try {
            for (File jar_file : jar_list) {
                class_pool.insertClassPath(jar_file.getAbsolutePath());
            }
        }catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public ClassElement getRoot() {
        return root;
    }

    public List<String> getOneTimeClasses() {
        return one_time_classes;
    }

    public void assignStartingMethod(String class_name, String starting_method) {
        this.starting_class = class_name;
        this.starting_method = starting_method;

        try {
            this.params = class_pool.getCtClass(starting_class).getDeclaredMethod(starting_method).getParameterTypes();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public List listCalledMethods() {
        List<ClassInfo> m_list = new ArrayList<>();
//        parents_stack.push(root);
        //this.listCalledMethodsRecursive(this.starting_class, this.starting_method, m_list);
        this.listCalledMethodsRecursive(this.starting_class, this.starting_method, this.params, Enum.ExprCall.METHOD_CALL, m_list);
        return m_list;
    }

    public List listCalledMethods(List<String> keep_only) {
        this.keep_only_classes = keep_only;
        this.one_time_classes = new ArrayList<>();
        this.one_time_methods = new ArrayList<>();
        this.one_time_extras = new ArrayList<>();

        List<ClassInfo> m_list = new ArrayList<>();

        root = new StartingMethodElement();
        parents_stack.push(root);

        this.listCalledMethodsRecursive(this.starting_class, this.starting_method, this.params, Enum.ExprCall.METHOD_CALL, m_list);
        //this.listCalledMethodsRecursive(this.starting_class, this.starting_method, null, 2);
        return m_list;
    }

//    private void listCalledMethodsRecursive(String class_name, String starting_method, CtClass[] params, int type) {
//        try {
//            CtClass ct_class = class_pool.get(class_name);
//            CtBehavior method;
//            if(type == 1) {
//                method = ct_class.getDeclaredConstructor(params);
//            }else{
//                method = ct_class.getDeclaredMethod(starting_method);
//            }
//            method.instrument(
//                    new ExprEditor() {
//                        @Override
//                        public void edit(NewExpr e) throws CannotCompileException {
//                            //super.edit(e);
//                            try {
//                                System.out.println(e.getConstructor().getName());
//                                listCalledMethodsRecursive(e.getConstructor().getLongName().replace("()", ""), e.getConstructor().getName(), e.getConstructor().getParameterTypes(),1);
//                            } catch (NotFoundException ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void edit(NewArray a) throws CannotCompileException {
//                            //super.edit(a);
//                            System.out.println(a.getCreatedDimensions());
//                        }
//
//                        @Override
//                        public void edit(ConstructorCall c) throws CannotCompileException {
//                           // super.edit(c);
//                            System.out.println(c.getMethodName());
//                            //listCalledMethodsRecursive(c.get, e.getConstructor().getName());
//                        }
//
//                        @Override
//                        public void edit(MethodCall m) throws CannotCompileException {
//                            //super.edit(m);
//                            System.out.println(m.getMethodName());
//                            try {
//                                listCalledMethodsRecursive(m.getClassName(), m.getMethodName(), m.getMethod().getParameterTypes(), 2);
//                            } catch (NotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//        }catch (NotFoundException | CannotCompileException e) {
//            e.printStackTrace();
//        }
//    }

    private void listCalledMethodsRecursive(String class_name, String starting_method, CtClass[] params, Enum.ExprCall type, List list) {
        if (class_name != null && class_name.equals("com.google.gson.internal.bind.TypeAdapters")) {
            int a = 1;
        }

        try {
            CtClass ct_class = class_pool.get(class_name);
            //CtClass ct_class = class_pool.get("com.google.gson.Gson$FutureTypeAdapter");
            //ct_class.nest
            CtField[] f1 = ct_class.getFields();
            CtField[] f2 = ct_class.getDeclaredFields();

            //checkForFieldClasses(ct_class);

//            if (f2.length > 0) {
//                SignatureAttribute sa = (SignatureAttribute)f2[14].getFieldInfo2().getAttribute("Signature");
//                try {
//                    SignatureAttribute.ObjectType ot = SignatureAttribute.toFieldSignature(sa.getSignature());
//                    SignatureAttribute.TypeArgument[] kk = ((SignatureAttribute.ClassType)ot).getTypeArguments();
//
//                    //SignatureAttribute.ClassType ctt = (SignatureAttribute.ClassType) SignatureAttribute.toFieldSignature(sa.getSignature());
//
//                    String p1 = ((SignatureAttribute.ClassType)kk[0].getType()).getName();
//
//                    SignatureAttribute.ObjectType ot2 = kk[0].getType();
//                    SignatureAttribute.TypeArgument[] kk2 = ((SignatureAttribute.ClassType)ot2).getTypeArguments();
//                    String p3 = ((SignatureAttribute.ClassType)kk2[0].getType()).getName();
//                    String p4 = ((SignatureAttribute.ClassType)kk2[1].getType()).getName();
//                    //String p2 = ((SignatureAttribute.ClassType)kk[1].getType()).getName();
//
//                    //TODO check for nested classes (as function)
////                    if nestedclasses
//
//                    System.out.println(kk);
//                } catch (BadBytecode e) {
//                    e.printStackTrace();
//                }
//            }
            CtClass[] cls = ct_class.getNestedClasses();
            CtClass clsd = ct_class.getDeclaringClass();
            Collection<String> aa = ct_class.getRefClasses();
//            ct_class.get
            CtBehavior behavior = null;
            switch(type) {
                case METHOD_CALL:
                    CtBehavior[] bh = ct_class.getDeclaredBehaviors();
                    behavior = ct_class.getDeclaredMethod(starting_method, params);
                    break;
                case CONSTRUCTOR_CALL:
                    behavior = ct_class.getDeclaredConstructor(params);
                    break;
            }

            behavior.instrument(
                new ExprEditor() {

                    @Override
                    public void edit(Instanceof i) throws CannotCompileException {
                        System.out.println("Instance of -> " + i.getFileName());
                        super.edit(i);
                    }

                    @Override
                    public void edit(Cast c) throws CannotCompileException {
                        System.out.println("Cast of -> " + c.getFileName());
                        super.edit(c);
                    }

                    @Override
                    public void edit(Handler h) throws CannotCompileException {
                        System.out.println("Handler of -> " + h.getFileName());
                        super.edit(h);
                    }

                    @Override
                    public void edit(FieldAccess f) throws CannotCompileException {
                        System.out.println("FieldAccess of -> " + f.getClassName());
                        String ss = f.getSignature();
                        String s2 = f.getFieldName();
                        int s3 = f.getLineNumber();
                        if (s3 == 268) {
                            int aa = 1;
                        }
                        CtClass[] s5;
                        try {
                            s5 = f.where().getParameterTypes();
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        checkForReferenceClasses(f.getClassName());
                        super.edit(f);
                    }

                    @Override
                    public void edit(NewExpr e) {
                        try {
                            CtConstructor constructor = e.getConstructor();
                            String inline_info = constructor.getSignature();
                            //System.out.println(inline_info);
                            if (!constructor.isEmpty()) {
                                addToTree(constructor.getLongName().replaceAll("\\([^)]*\\)", ""), null, constructor.getSignature(), constructor.getParameterTypes(), Enum.ExprCall.CONSTRUCTOR_CALL, list);
                                listCalledMethodsRecursive(constructor.getLongName().replaceAll("\\([^)]*\\)", ""), constructor.getName(), constructor.getParameterTypes(), Enum.ExprCall.CONSTRUCTOR_CALL, list);
                            }
                        } catch (NotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void edit(NewArray a) throws CannotCompileException {
                        super.edit(a);
                    }

                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        String inline_info = m.getClassName() + "+" + m.getMethodName() + "+" + m.getSignature();
                        //System.out.println(inline_info);
                        try {
                            int ln = m.getLineNumber();
                            CtClass[] ctm = m.getMethod().getParameterTypes();
                            addToTree(m.getClassName(), m.getMethodName(), m.getSignature(), m.getMethod().getParameterTypes(), Enum.ExprCall.METHOD_CALL, list);
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void edit(ConstructorCall c) {
                        String inline_info = c.getClassName() + "+" + c.getMethodName() + "+" + c.getSignature();
                        //System.out.println(inline_info);
                        try {
                            addToTree(c.getClassName(), c.getMethodName(), c.getSignature(), c.getConstructor().getParameterTypes(), Enum.ExprCall.CONSTRUCTOR_CALL, list);
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                });
        }catch (NotFoundException e) {
            //Try searching method on upper level (anonymous classes only)
            try {
                one_time_methods.remove(one_time_methods.size() - 1);
                String superclass_name = class_pool.get(class_name).getSuperclass().getName();
                new_node.resettingClassname(superclass_name);

                String inline_info = superclass_name + "+" + new_node.getMethodName() + "+" + new_node.getMethodSignature();
                if (!one_time_methods.contains(inline_info)) {
                    listCalledMethodsRecursive(superclass_name, starting_method, params, type, list);
                    one_time_methods.add(inline_info);
                }else{
                    List<BaseTreeElement> children = new_node.getFirstParent().getChildren();
                    children.remove(children.size() - 1);
                }
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private void addToTree(String class_name, String method_name, String signature, CtClass[] params, Enum.ExprCall call_type, List list) throws NotFoundException {
        if (root != null) {
            if(parents_stack.size() > 0) {
                current_parent = parents_stack.peek();
            }else{
                current_parent = root;
            }
        }

        if(keep_only_classes != null && keep_only_classes.contains(class_name)) {
            if(!one_time_classes.contains(class_name)) {
                one_time_classes.add(class_name);
            }

            String inline_info = class_name + "+" + method_name + "+" + signature;
            if(!one_time_methods.contains(inline_info)) {
                one_time_methods.add(inline_info);
                System.out.println(inline_info);

                new_node = null;
                if (call_type == Enum.ExprCall.METHOD_CALL) {
                    new_node = new MethodElement(class_name, method_name, params, signature, call_type);
                }else if(call_type == Enum.ExprCall.CONSTRUCTOR_CALL) {
                    new_node = new ConstructorElement(class_name, method_name, params, signature, call_type);
                }
                if (root == null) {
                    root = new_node;
                    current_parent = new_node; // = parents_stack.peek();
                } else {
                    current_parent.addChild(new_node);
                }
                parents_stack.push(new_node);

                checkForExtras(class_name);
            }else{
                return;
            }

            listCalledMethodsRecursive(class_name, method_name, params, call_type,  list);

            if(keep_only_classes != null && keep_only_classes.contains(class_name)) {
                parents_stack.pop();
            }
        }
    }

    private void checkForExtras(String class_name) {
        try {
            CtClass ct_class = class_pool.get(class_name);

            CtClass superclass = ct_class.getSuperclass();
            String inline_id = class_name + "+" + superclass.getName();
            if(!superclass.getName().equals("java.lang.Object") && !one_time_extras.contains(inline_id)){
                SuperclassElement superclass_node = new SuperclassElement(superclass.getName(), class_name);
                parents_stack.peek().addChild(superclass_node);
                one_time_extras.add(inline_id);
            }

            CtClass[] interfaces = ct_class.getInterfaces();
            for (int i=0; i < interfaces.length; i++) {
                inline_id = class_name + "+" + interfaces[i].getName();
                if(!one_time_extras.contains(inline_id)) {
                    InterfaceElement interfaces_node = new InterfaceElement(interfaces[i].getName(), class_name);
                    parents_stack.peek().addChild(interfaces_node);
                    one_time_extras.add(inline_id);
                }
            }
            checkForFieldClasses();

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkForReferenceClasses(String classname) {
        if(!classname.startsWith("java.") && !one_time_classes.contains(classname)) {
            parents_stack.peek().addChild(new EmptyClassElement(classname));
            one_time_classes.add(classname);
        }
    }

    private void checkForFieldClasses() {
//        for(CtField ct_field : ct_class.getDeclaredFields()) {
//            String field_type = ct_field.getType().getName();
//            if(!field_type.startsWith("java.") && !one_time_classes.contains(field_type) && !field_type.equals("boolean") && !field_type.equals("int")) {
//                parents_stack.peek().addChild(new EmptyClassElement(field_type));
//                one_time_classes.add(field_type);
//            }
//        }
    }

    private void checkForFieldClasses(CtClass ct_class) {
        CtField[] fields = ct_class.getDeclaredFields();
        if (fields.length < 1) {
            return;
        }
        for (CtField field : fields) {
            CtClass singletype_field_class;
            try {
                singletype_field_class = field.getType();
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }

            String nested_classname = singletype_field_class.getName();
            if (nested_classname.contains("[]")) {
                nested_classname = nested_classname.replace("[]", "");
            }
            if (!nested_classname.startsWith("java.") && !Arrays.asList(Constants.PRIMITIVES).contains(nested_classname) && !one_time_classes.contains(nested_classname)) {
                //Add custom field class only (EmptyClassElement)
                parents_stack.peek().addChild(new EmptyClassElement(nested_classname));
                one_time_classes.add(nested_classname);
                System.out.println(nested_classname);

                checkForFieldClasses(singletype_field_class);
            }


            SignatureAttribute signature_attribute = (SignatureAttribute)field.getFieldInfo2().getAttribute("Signature");
            if (signature_attribute == null) {
                continue;
            }

            try {
                SignatureAttribute.ObjectType object_type = SignatureAttribute.toFieldSignature(signature_attribute.getSignature());
                checkForFieldSignatureArgumentsRecursive(object_type);
            } catch (BadBytecode | NotFoundException e) {
                throw new RuntimeException(e);
            }



//            try {
//                SignatureAttribute.ObjectType object_type = SignatureAttribute.toFieldSignature(signature_attribute.getSignature());
//                SignatureAttribute.TypeArgument[] type_arguments = ((SignatureAttribute.ClassType)object_type).getTypeArguments();
//
//                for (SignatureAttribute.TypeArgument ta : type_arguments) {
//                    if(((SignatureAttribute.ClassType)ta.getType()) != null) {
//                        checkForFieldClassesRecursive(class_pool.get(((SignatureAttribute.ClassType)ta.getType()).getName()));
//                    }
//                }
////                //SignatureAttribute.ClassType ctt = (SignatureAttribute.ClassType) SignatureAttribute.toFieldSignature(sa.getSignature());
////
////                String p1 = ((SignatureAttribute.ClassType)kk[0].getType()).getName();
////
////                SignatureAttribute.ObjectType ot2 = kk[0].getType();
////                SignatureAttribute.TypeArgument[] kk2 = ((SignatureAttribute.ClassType) ot2).getTypeArguments();
////                String p3 = ((SignatureAttribute.ClassType) kk2[0].getType()).getName();
////                String p4 = ((SignatureAttribute.ClassType) kk2[1].getType()).getName();
////                //String p2 = ((SignatureAttribute.ClassType)kk[1].getType()).getName();
//
//                //System.out.println(kk);
//            } catch (BadBytecode | NotFoundException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void checkForFieldSignatureArgumentsRecursive(SignatureAttribute.ObjectType object_type) throws NotFoundException {
        SignatureAttribute.TypeArgument[] type_arguments = ((SignatureAttribute.ClassType)object_type).getTypeArguments();
        if (type_arguments == null) {
            return;
        }
        for (SignatureAttribute.TypeArgument ta : type_arguments) {
            if (ta.getType() != null) {
                if (ta.getType() instanceof SignatureAttribute.TypeVariable) {
                    continue;
                }

                CtBehavior mm;

                String nested_classname = ((SignatureAttribute.ClassType)ta.getType()).getName();
                if (!nested_classname.startsWith("java.") && !one_time_classes.contains(nested_classname) ) {
                    //Check for nested class
                    SignatureAttribute.ClassType nested_inside_class = ((SignatureAttribute.ClassType)ta.getType()).getDeclaringClass();
                    if (nested_inside_class != null) {
                        nested_classname = nested_inside_class.getName() + "$" + nested_classname;
                    }

                    //Add custom field class only (EmptyClassElement)
                    parents_stack.peek().addChild(new EmptyClassElement(nested_classname));
                    one_time_classes.add(nested_classname);
                    System.out.println(nested_classname);
                }
                checkForFieldSignatureArgumentsRecursive(ta.getType());
            }
        }
    }

}
