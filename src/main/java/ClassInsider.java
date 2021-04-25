import models.ClassInfo;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.List;


public class ClassInsider {

    private ClassPool class_pool;

    public ClassInsider(String jar_path) {
        class_pool = ClassPool.getDefault();

        try {
            class_pool.insertClassPath(jar_path);
        }catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public List listCalledMethods(String class_name, String starting_method) {

        List<ClassInfo> m_list = new ArrayList<>();

        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass ct_class = cp.get(class_name);
            CtMethod method = ct_class.getDeclaredMethod(starting_method);
            method.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall method) throws CannotCompileException {
                            ClassInfo ci = new ClassInfo(method.getClassName(), method.getMethodName());
                            m_list.add(ci);
                            //System.out.println(method.getClassName() + "." + method.getMethodName() + "--" + method.getLineNumber() + " " + method.getSignature());
                        }
                    });
        }catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
        return m_list;
    }
}
