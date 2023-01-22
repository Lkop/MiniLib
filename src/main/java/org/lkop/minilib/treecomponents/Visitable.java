package org.lkop.minilib.treecomponents;

public interface Visitable {
     <T> T accept(BaseVisitor<? extends T> visitor);
}