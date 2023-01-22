package org.lkop.minilib;

import org.lkop.minilib.treecomponents.BaseVisitor;
import org.lkop.minilib.treecomponents.VisitableBaseTreeElement;
import org.lkop.minilib.utils.StringUtils;

public class ClassElement extends VisitableBaseTreeElement {

    private String class_longname;
    private String class_name;
    private String class_path;

    public ClassElement() {

    }

    public ClassElement(String class_longname) {
        resettingClassname(class_longname);
    }

    public void resettingClassname(String class_longname) {
        this.class_longname = class_longname;
        this.class_name = StringUtils.longToShortClassname(class_longname);
        this.class_path = StringUtils.classToPath(class_longname);
    }

    public String getClassLongName() {
        return class_longname;
    }

    public String getClassName() {
        return class_name;
    }

    public String getClassPath() {
        return class_path;
    }

    @Override
    public String getGraphvizName(){
        return getClassLongName() + "_" + getSerialId();
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitClassElement(this);
        }
        return null;
    }
}
