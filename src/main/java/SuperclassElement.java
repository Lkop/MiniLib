import javassist.CtClass;
import treecomponents.BaseVisitor;

public class SuperclassElement extends ClassElement {


    private String superclass_longname;

    public SuperclassElement(String class_longname, String superclass_longname) {
        super(class_longname);
        this.superclass_longname = superclass_longname;
    }

    public String getSuperclassName() {
        return this.superclass_longname;
    }

    public String getGraphvizName(){
        return getClassLongName() + " extends " + superclass_longname + " (" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitSuperclassElement(this);
        }
        return null;
    }

}
