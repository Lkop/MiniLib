package org.lkop.minilib.treecomponents;

import java.util.ArrayList;
import java.util.List;

public class BaseTreeElement {

    //Auto increment
    private static int count = 0;

    private int serial_id;
    private List<BaseTreeElement> parents = null;
    private List<BaseTreeElement> children = null;

    public BaseTreeElement(){
        serial_id = count++;
    }

    public int getSerialId() {
        return serial_id;
    }

    public void addChild(BaseTreeElement child) {
        if (child.parents == null){
            child.parents = new ArrayList<>();
        }
        child.parents.add(this);

        if (children == null){
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public BaseTreeElement getFirstParent() {
        return parents.get(0);
    }

    public List<BaseTreeElement> getChildren() {
        if(children == null) {
            return new ArrayList<>();
        }
        return children;
    }

    public String getGraphvizName() {
        return null;
    }
}
