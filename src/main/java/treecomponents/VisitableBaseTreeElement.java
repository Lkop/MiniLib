package treecomponents;

public class VisitableBaseTreeElement extends BaseTreeElement implements Visitable{

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        return null;
    }
}
