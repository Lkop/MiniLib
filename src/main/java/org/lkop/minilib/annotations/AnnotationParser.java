package org.lkop.minilib.annotations;

import java.lang.reflect.Method;
import org.lkop.minilib.MiniLibEngine;

public class AnnotationParser {

    MiniLibEngine minilib_engine = new MiniLibEngine();

    public void runAnnotationParsing(Class<?> clazz, Method method) {
        if (!method.isAnnotationPresent(MiniLib.class)) {
            minilib_engine.generateCode(clazz, method);
        }
    }
}
