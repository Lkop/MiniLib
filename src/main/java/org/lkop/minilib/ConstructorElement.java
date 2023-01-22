package org.lkop.minilib;

import javassist.CtClass;
import org.lkop.minilib.treecomponents.BaseVisitor;

public class ConstructorElement extends MethodElement{

    public ConstructorElement(String class_longname, String method_name, CtClass[] params, String method_signature, Enum.ExprCall type) {
        super(class_longname, method_name, params, method_signature, type);
    }

    public String getGraphvizName(){
        return getClassLongName() + " <-- CONSTRUCTOR(...) (" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitConstructorElement(this);
        }
        return null;
    }
}
