import javassist.CtClass;
import treecomponents.BaseVisitor;
import treecomponents.VisitableBaseTreeElement;
import utils.StringUtils;

public class MethodElement extends ClassElement {

    private String method_name;
    private CtClass[] params;
    private String method_signature;
    private String unique_id;
    private Enum.ExprCall type;

    public MethodElement() {
        super();
    }

    public MethodElement(String class_longname, String method_name, CtClass[] params, String method_signature, Enum.ExprCall type) {
        super(class_longname);
        this.method_name = method_name;
        this.params = params;
        this.method_signature = method_signature;
        this.unique_id = class_longname + "+" + method_name + "+" + method_signature;
        this.type = type;
    }

    public String getMethodName() {
        return method_name;
    }

    public CtClass[] getParams() {
        return params;
    }

    public String getUniqueId() {
        return unique_id;
    }

    public Enum.ExprCall getType() {
        return type;
    }

    public String getGraphvizName(){
        return getClassLongName() + "_" + method_name + "_" + getSerialId();
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
