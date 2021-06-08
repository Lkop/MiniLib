package treecomponents;


public interface TreeVisitor {
    <T> T visit(VisitableBaseTreeElement node);
}
