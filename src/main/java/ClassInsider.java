import javassist.expr.*;
import models.ClassInfo;
import javassist.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ClassInsider {

    private ClassPool class_pool;
    private String starting_class;
    private String starting_method;
    private CtClass[] params;
    private ClassElement root = null;
    private ClassElement current_parent = null;
    private Stack<ClassElement> parents_stack = new Stack<>();
    private String previous_class = null;

    private List<String> keep_only_classes;
    private List<String> one_time_methods;

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

    public List listCalledMethods(List<String> keep_only) throws NotFoundException {
        this.keep_only_classes = keep_only;
        this.one_time_methods = new ArrayList<>();

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
        try {
            CtClass ct_class = class_pool.get(class_name);

            CtBehavior behavior = null;
            switch(type) {
                case METHOD_CALL:
                    behavior = ct_class.getDeclaredMethod(starting_method, params);
                    break;
                case CONSTRUCTOR_CALL:
                    behavior = ct_class.getDeclaredConstructor(params);
                    break;
            }

            behavior.instrument(
                new ExprEditor() {
                    @Override
                    public void edit(NewExpr e) {
                        try {
                            CtConstructor constructor = e.getConstructor();
                            String inline_info = constructor.getSignature();
                            System.out.println(inline_info);
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
        }catch (NotFoundException | CannotCompileException e) {
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
            String inline_info = class_name + "+" + method_name + "+" + signature;
            if(!one_time_methods.contains(inline_info)) {
                one_time_methods.add(inline_info);
                System.out.println(inline_info);

                MethodElement new_node = null;
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
            }else{
                return;
            }

            listCalledMethodsRecursive(class_name, method_name, params, call_type,  list);

            if(keep_only_classes != null && keep_only_classes.contains(class_name)) {
                parents_stack.pop();
            }
        }
    }
}
