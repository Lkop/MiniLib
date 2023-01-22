import treecomponents.BaseVisitor;

public abstract class MethodElementVisitor<T> extends BaseVisitor<T> {

    public T visitStartingMethodElement(StartingMethodElement node) {
        return super.visitChildren(node);
    }

    public T visitClassElement(ClassElement node) {
        return super.visitChildren(node);
    }

    public T visitEmptyClassElement(EmptyClassElement node) {
        return super.visitChildren(node);
    }

    public T visitSuperclassElement(SuperclassElement node) {
        return super.visitChildren(node);
    }

    public T visitInterfaceElement(InterfaceElement node) {
        return super.visitChildren(node);
    }

    public T visitMethodElement(MethodElement node) {
        return super.visitChildren(node);
    }

    public T visitConstructorElement(ConstructorElement node) {
        return super.visitChildren(node);
    }

}
