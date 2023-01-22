package org.lkop.minilib.treecomponents;

public interface TreeVisitor {
    <T> T visit(VisitableBaseTreeElement node);
}
