package org.lkop.minilib;

import org.lkop.minilib.treecomponents.BaseVisitor;

public class StartingMethodElement extends MethodElement{

    private GeneralInfo general_info;

    public StartingMethodElement() {
        super();
    }

    public GeneralInfo getGeneralInfo() {
        return general_info;
    }

    public void setGeneralInfo(GeneralInfo general_info) {
        this.general_info = general_info;
    }

    public String getGraphvizName(){
        return "start_"+getSerialId();
    }

    @Override
    public <T> T accept(BaseVisitor<? extends T> visitor) {
        MethodElementVisitor v = (MethodElementVisitor)visitor;
        if (v != null) {
            return (T) v.visitStartingMethodElement(this);
        }
        return null;
    }
}
