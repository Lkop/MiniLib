package org.lkop.minilib;

import org.lkop.minilib.treecomponents.BaseVisitor;

public class SuperclassElement extends ClassElement {

    private ClassElement parent_class;

    public SuperclassElement(String superclass_longname, String parent_longname) {
        super(superclass_longname);
        this.parent_class = new ClassElement(parent_longname);
    }

    public ClassElement getParentClass() {
        return parent_class;
    }

    public String getSuperclassLongName() {
        return getClassLongName();
    }

    public String getGraphvizName(){
        return parent_class.getClassLongName() + " extends " + getClassLongName() + " (" + getSerialId() + ")";
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitSuperclassElement(this);
        }
        return null;
    }

}
