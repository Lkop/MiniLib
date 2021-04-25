import models.ClassInfo;

import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) throws IOException {
        ClassCreator cc = new ClassCreator("Generated");

        StringBuilder my_method = new StringBuilder();
        my_method.append("public void ")
                .append("test_method")
                .append("() {")
                .append("System.out.println();")
                .append("}");

        cc.addMethod(my_method);

        cc.createClassFile();

        
        ClassInsider ci = new ClassInsider("examples/client_jar/Client.jar");

        for(ClassInfo model : (List<ClassInfo>)ci.listCalledMethods("Main", "main")) {
            System.out.println(model.getClassName() + " " + model.getMethodName());
        }
    }
}
