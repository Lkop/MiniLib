package treecomponents;

public interface Visitable {
     <T> T accept(BaseVisitor<? extends T> visitor);
}