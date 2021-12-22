import treecomponents.BaseVisitor;

public abstract class MethodElementVisitor<T> extends BaseVisitor<T> {

    public T visitStartingMethodElement(StartingMethodElement node) {
        return super.visitChildren(node);
    }

    public T visitMethodElement(MethodElement node) {
        return super.visitChildren(node);
    }

    public T visitConstructorElement(ConstructorElement node) {
        return super.visitChildren(node);
    }

}
