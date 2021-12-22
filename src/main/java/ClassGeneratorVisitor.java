public class ClassGeneratorVisitor extends MethodElementVisitor<Integer> {

    private PackageCreator pc;

    public ClassGeneratorVisitor(String filename) {
        pc = new PackageCreator();
    }

    @Override
    public Integer visitStartingMethodElement(StartingMethodElement node) {
        super.visitMethodElement(node);
        return 0;
    }

    @Override
    public Integer visitMethodElement(MethodElement node) {
        System.out.println("Copying: " + node.getClassLongName() + " -> " + node.getMethodName() + "()");
        pc.addToPackage(node);
        super.visitMethodElement(node);
        return 0;
    }

    @Override
    public Integer visitConstructorElement(ConstructorElement node) {
        System.out.println("Copying constructor: " + node.getClassLongName() + " -> " + node.getMethodName() + "()");
        pc.addToPackage(node);
        super.visitConstructorElement(node);
        return 0;
    }
}
