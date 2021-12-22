import treecomponents.BaseVisitor;

public class StartingMethodElement extends MethodElement{

    public StartingMethodElement() {
        super();
    }

    public String getGraphvizName(){
        return "start_"+getSerialId();
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitStartingMethodElement(this);
        }
        return null;
    }
}
