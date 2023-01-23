package org.lkop.minilib.annotations;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class MiniLibAnnotation implements BeforeAllCallback, BeforeEachCallback {

    private AnnotationParser annotation_parser;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        annotation_parser = new AnnotationParser();
        Optional<Class<?>> test_class = extensionContext.getTestClass();
        if (test_class.isPresent()) {
            annotation_parser.parseFolderAnnotations(test_class.get());
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        Optional<Class<?>> test_class = extensionContext.getTestClass();
        Optional<Method> test_method = extensionContext.getTestMethod();
        if (test_class.isPresent() && test_method.isPresent()) {
            annotation_parser.parseMethodAnnotation(test_class.get(), test_method.get());
        }
    }
}