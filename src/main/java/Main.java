public class Main {

    public static void main(String[] args) {
        ClassCreator cc = new ClassCreator("Generated");

        StringBuilder my_method = new StringBuilder();
        my_method.append("public void ")
                .append("test_method")
                .append("() {")
                .append("System.out.println();")
                .append("}");

        cc.addMethod(my_method);
        cc.createClassFile();

        ClassInsider ci = new ClassInsider("C:\\Users\\Loukas\\Desktop\\MiniLibWorkspace\\Client\\out\\artifacts\\Client_jar\\Client.jar");
        ci.assignStartingMethod("Main", "main");
        ci.listCalledMethods();

        MethodElement method_tree = ci.getRoot();
        System.out.println("Done");
    }
}
