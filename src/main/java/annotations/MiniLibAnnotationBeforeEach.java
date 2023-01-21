package annotations;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.lang.reflect.Method;
import java.util.Optional;

public class MiniLibAnnotationBeforeEach implements BeforeEachCallback {

    private AnnotationParser ap = new AnnotationParser();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        Optional<Class<?>> test_class = extensionContext.getTestClass();
        Optional<Method> test_method = extensionContext.getTestMethod();

        if (test_class.isPresent() && test_method.isPresent()) {
            ap.runAnnotationParsing(test_class.get(), test_method.get());
        }
    }
}