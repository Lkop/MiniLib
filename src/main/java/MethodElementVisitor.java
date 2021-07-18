import treecomponents.BaseVisitor;

public abstract class MethodElementVisitor<T> extends BaseVisitor<T> {

    public T visitMethodElement(MethodElement node) {
        return super.visitChildren(node);
    }

}
