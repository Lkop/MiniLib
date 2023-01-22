package org.lkop.minilib;

import org.lkop.minilib.treecomponents.BaseVisitor;

//TODO Merge with ClassElement
public class EmptyClassElement extends ClassElement {

    public EmptyClassElement(String class_longname) {
        super(class_longname);
    }

    public String getGraphvizName(){
        return "EMPTY CLASS " + getClassLongName() + "(" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitEmptyClassElement(this);
        }
        return null;
    }
}
