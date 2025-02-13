package org.lkop.minilib.annotations;

import java.lang.reflect.Method;
import org.lkop.minilib.MiniLibEngine;

public class AnnotationParser {

    MiniLibEngine minilib_engine = new MiniLibEngine();

    public AnnotationParser() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//
//
////        File jar_file = new File(jar_path);
////        if(!jar_file.isFile()) {
////            return;
////        }
////
////        ClassPool class_pool = ClassPool.getDefault();
////        try {
////            class_pool.insertClassPath(jar_path);
////        } catch (NotFoundException e) {
////            throw new RuntimeException(e);
////        }
//
//
//        try {
//            Class<?> test_class = Class.forName("com.bla.TestActivity");
//            Method[] methods = test_class.getDeclaredMethods();
//
//            for (int i = 0; i < methods.length; i++) {
//                System.out.println(methods[i].toString());
//            }
//        } catch (Throwable e) {
//            System.err.println(e);
//        }
     }

    public void parseFolderAnnotations(Class<?> clazz) {
        if (clazz.isAnnotationPresent(MiniLibDependenciesFolder.class)) {
            MiniLibDependenciesFolder folder = clazz.getAnnotation(MiniLibDependenciesFolder.class);
            minilib_engine.setDependenciesFolder(folder.value());
        }

        if (clazz.isAnnotationPresent(MiniLibOutputFolder.class)) {
            MiniLibOutputFolder folder = clazz.getAnnotation(MiniLibOutputFolder.class);
            minilib_engine.setOutputFolder(folder.value());
        }
    }

    public void parseMethodAnnotation(Class<?> clazz, Method method) {
        if (method.isAnnotationPresent(MiniLib.class)) {
            minilib_engine.generateCode(clazz, method);
        }
    }
}
