package org.lkop.minilib.annotations;

import java.lang.reflect.Method;
import org.lkop.minilib.MiniLibEngine;

public class AnnotationParser {

    MiniLibEngine minilib_engine = new MiniLibEngine();


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
