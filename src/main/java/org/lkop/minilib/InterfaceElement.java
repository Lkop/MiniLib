package org.lkop.minilib;

import org.lkop.minilib.treecomponents.BaseVisitor;

public class InterfaceElement extends ClassElement {

    private ClassElement parent_class;

    public InterfaceElement(String interface_longname, String parent_longname) {
        super(interface_longname);
        this.parent_class = new ClassElement(parent_longname);
    }

    public ClassElement getParentClass() {
        return parent_class;
    }

    public String getInterfaceLongName() {
        return getClassLongName();
    }

    public String getGraphvizName(){
        return parent_class.getClassLongName() + " implements " + getClassLongName() + " (" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitInterfaceElement(this);
        }
        return null;
    }
}
