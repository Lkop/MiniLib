import javassist.CtClass;
import treecomponents.BaseVisitor;

public class InterfaceElement extends ClassElement {

    private String interface_longname;

    public InterfaceElement(String class_longname, String interface_longname) {
        super(class_longname);
        this.interface_longname = interface_longname;
    }

    public String getInterfaceName() {
        return interface_longname;
    }

    public String getGraphvizName(){
        return getClassLongName() + " implements " + interface_longname + " (" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitInterfaceElement(this);
        }
        return null;
    }
}
