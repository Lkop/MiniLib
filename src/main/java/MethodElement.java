import treecomponents.BaseTreeElement;
import treecomponents.BaseVisitor;
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

    public MethodElement getFirstParent() {
        return (MethodElement) super.getFirstParent();
    }

    public String getGraphvizName(){
        return method_name+"_"+getSerialId();
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitMethodElement(this);
        }
        return null;
    }
}
