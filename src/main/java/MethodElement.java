import treecomponents.VisitableBaseTreeElement;

public class MethodElement extends VisitableBaseTreeElement {

    private String class_name;
    private String method_name;

    public MethodElement(String class_name, String method_name) {
        this.class_name = class_name;
        this.method_name = method_name;
    }

    public String getClassName() {
        return class_name;
    }

    public String getMethodName() {
        return method_name;
    }
}
