import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        ClassCreator cc = new ClassCreator("Generated");

        StringBuilder my_method = new StringBuilder();
        my_method.append("public void ")
                .append("test_method")
                .append("() {")
                .append("System.out.println();")
                .append("}");

        cc.addMethod(my_method);
        cc.createClassFile();


        ClassParser cp = new ClassParser("C:\\Users\\Loukas\\Desktop\\MiniLibWorkspace\\Client\\out\\artifacts\\Client_jar\\Client.jar");

        ClassInsider ci = new ClassInsider("C:\\Users\\Loukas\\Desktop\\MiniLibWorkspace\\Client\\out\\artifacts\\Client_jar\\Client.jar");
        ci.assignStartingMethod("Main", "main");
        ci.listCalledMethods(cp.getClasses());

        MethodElement method_tree = ci.getRoot();
        System.out.println("Visiting Done");

        TreePrinterVisitor tree_printer = new TreePrinterVisitor("ast");
        tree_printer.openWriteGraph();
        ci.getRoot().accept(tree_printer);
        tree_printer.closeWriteGraph();
        System.out.println("Printing Done");
    }
}
