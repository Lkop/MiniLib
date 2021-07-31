public class ClassGeneratorVisitor extends MethodElementVisitor<Integer> {

    private ClassCreator cc;

    public ClassGeneratorVisitor(String filename) {
        cc = new ClassCreator(filename);
    }

    @Override
    public Integer visitMethodElement(MethodElement node) {
        System.out.println("Copying: " + node.getClassName() + " -> " + node.getMethodName() + "()");

        cc.copyExistingMethod(node.getClassName(), node.getMethodName());

        super.visitMethodElement(node);

        return 0;
    }

    public void createClassFile() {
        cc.createClassFile();
    }
}
