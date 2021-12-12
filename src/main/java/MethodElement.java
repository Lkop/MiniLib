import javassist.CtClass;
import treecomponents.BaseVisitor;
import treecomponents.VisitableBaseTreeElement;
import utils.StringUtils;

public class MethodElement extends VisitableBaseTreeElement {

    private String class_longname;
    private String class_name;
    private String method_name;
    private CtClass[] params;
    private String method_signature;
    private String unique_id;
    private String class_path;
    private Enum.Methods type;

    public MethodElement(String class_longname, String method_name, CtClass[] params, String method_signature, Enum.Methods type) {
        this.class_longname = class_longname;
        this.class_name = StringUtils.longToShortClassname(class_longname);
        this.method_name = method_name;
        this.params = params;
        this.method_signature = method_signature;
        this.unique_id = class_longname + "+" + method_name + "+" + method_signature;
        this.class_path = StringUtils.classToPath(class_longname);
        this.type = type;
    }

    public String getClassName() {
        return class_name;
    }


    public String getClassLongName() {
        return class_longname;
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

    public String getClassPath() {
        return class_path;
    }

    public Enum.Methods getType() {
        return type;
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
