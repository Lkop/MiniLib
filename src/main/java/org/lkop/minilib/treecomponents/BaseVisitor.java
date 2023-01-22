package org.lkop.minilib.treecomponents;

public abstract class BaseVisitor<T> implements TreeVisitor {

    private BaseTreeElement parent = null;

    @Override
    public T visit(VisitableBaseTreeElement node) {
        return (T) node.accept(this);
    }

    public T visitChildren(BaseTreeElement node) {
        BaseTreeElement oldParent = parent;
        parent = node;
        T netResult = null;

        if (node.getChildren() != null) {
            for (BaseTreeElement child : node.getChildren()) {
                    VisitableBaseTreeElement element = (VisitableBaseTreeElement) child;
                    netResult = (T) element.accept(this);
            }
            parent = oldParent;
        }
        return netResult;
    }

//    public virtual T AggregateResult(T oldResult, T value) {
//        return value;
//    }
}

