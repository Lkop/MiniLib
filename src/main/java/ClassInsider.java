import models.ClassInfo;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class ClassInsider {

    private ClassPool class_pool;
    private String starting_class;
    private String starting_method;
    private MethodElement root;
    private Stack<MethodElement> parents_stack = new Stack<>();
    private int limit=0;


    public ClassInsider(String jar_path) {
        class_pool = ClassPool.getDefault();

        try {
            class_pool.insertClassPath(jar_path);
        }catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public MethodElement getRoot() {
        return root;
    }

    public void assignStartingMethod(String class_name, String starting_method) {
        this.starting_class = class_name;
        this.starting_method = starting_method;
    }

    public List listCalledMethods() {
        List<ClassInfo> m_list = new ArrayList<>();
        if (root == null) {
            root = new MethodElement("","");
        }
        parents_stack.push(root);
        this.listCalledMethodsRecursive(this.starting_class, this.starting_method, m_list);
        return m_list;
    }

    private void listCalledMethodsRecursive(String class_name, String starting_method, List list) {
        MethodElement current_parent = parents_stack.peek();
        try {
            CtClass ct_class = class_pool.get(class_name);
            CtMethod method = ct_class.getDeclaredMethod(starting_method);
            method.instrument(
                new ExprEditor() {
                    public void edit(MethodCall method) {
                        System.out.println(method.getClassName() + "  " + method.getMethodName());

                        MethodElement new_node = new MethodElement(method.getClassName(), method.getMethodName());
                        current_parent.addChild(new_node);
                        parents_stack.push(new_node);

                        if (limit < 10) {
                            limit++;
                            listCalledMethodsRecursive(method.getClassName(), method.getMethodName(), list);
                        }
                        parents_stack.pop();
                    }
                });
        }catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }
}