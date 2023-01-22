package org.lkop.minilib;
public class ClassGeneratorVisitor extends MethodElementVisitor<Integer> {

    private PackageCreator pc;

    public ClassGeneratorVisitor(String root_path) {
        pc = new PackageCreator(root_path);
    }

    @Override
    public Integer visitStartingMethodElement(StartingMethodElement node) {
        System.out.println("Parsing GeneralInfo");
        pc.parseGeneralInfo(node);
        super.visitMethodElement(node);
        return 0;
    }

    @Override
    public Integer visitEmptyClassElement(EmptyClassElement node) {
        System.out.println("Copying Empty Class: " + node.getClassLongName());
        pc.addEmptyFieldsClass(node);
        super.visitEmptyClassElement(node);
        return 0;
    }

    @Override
    public Integer visitSuperclassElement(SuperclassElement node) {
        System.out.println("Copying Superclass: " + node.getSuperclassLongName());
        pc.addSuperclass(node);
        super.visitSuperclassElement(node);
        return 0;
    }

    @Override
    public Integer visitInterfaceElement(InterfaceElement node) {
        System.out.println("Copying Interface: " + node.getInterfaceLongName());
        pc.addInterface(node);
        super.visitInterfaceElement(node);
        return 0;
    }

    @Override
    public Integer visitMethodElement(MethodElement node) {
        System.out.println("Copying Method: " + node.getClassLongName() + " -> " + node.getMethodName() + "()");
        pc.addMethod(node);
        super.visitMethodElement(node);
        return 0;
    }

    @Override
    public Integer visitConstructorElement(ConstructorElement node) {
        System.out.println("Copying Constructor: " + node.getClassLongName() + " -> " + node.getMethodName() + "()");
        pc.addMethod(node);
        super.visitConstructorElement(node);
        return 0;
    }
}
