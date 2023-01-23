package org.lkop.minilib;

import org.lkop.minilib.constants.Constants;
import org.lkop.minilib.treecomponents.BaseTreeElement;
import org.lkop.minilib.utils.ImageUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class TreePrinterVisitor extends MethodElementVisitor<Integer> {

    private String file_path;
    private PrintWriter writer;
    private int serial_counter = 0;
    private Stack<Integer> pos = new Stack<>();

    public TreePrinterVisitor(String output_folder) {
        file_path = output_folder + "/" + Constants.OUTPUT_FILENAME + ".dot";
        try {
            writer = new PrintWriter(file_path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractSubgraphs(MethodElement node) {
        writer.println("\tsubgraph cluster" + serial_counter++ + "{");
        writer.println("\t\tnode [style=filled,color=white];");
        writer.println("\t\tstyle=filled;");
        writer.println("\t\tcolor=lightgrey;");

        writer.print("\t\t");
        for (BaseTreeElement elem : node.getChildren()) {
            int i = pos.peek();
            pos.pop();
            pos.push(++i);
            writer.print(((MethodElement)elem).getMethodName()+"_"+elem.getSerialId()+"; ");
        }

        //writer.println("\n\t\tlabel=" + context_names[context] + ";");
        writer.println("\t}");
    }
//
//    public void openWriteGraph(){
//        writer.println("digraph G {");
//    }
//
//    public void closeWriteGraph(){
//        writer.println("}");
//        writer.close();
//
//        try {
//            ImageUtils.createGIF(filename);
//        }catch (IOException | InterruptedException e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public Integer visitStartingMethodElement(StartingMethodElement node) {
        System.out.println("StartingMethodVisitableElement -> " + node.getMethodName());
        writer.println("digraph G {");
        super.visitStartingMethodElement(node);
        writer.println("}");
        writer.close();

        try {
            ImageUtils.createGIF(file_path);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Integer visitClassElement(ClassElement node) {
        System.out.println("ClassVisitableElement -> " + node.getClassLongName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        super.visitClassElement(node);
        return 0;
    }

    @Override
    public Integer visitEmptyClassElement(EmptyClassElement node) {
        System.out.println("EmptyClassVisitableElement -> " + node.getClassLongName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        super.visitClassElement(node);
        return 0;
    }

    @Override
    public Integer visitSuperclassElement(SuperclassElement node) {
        System.out.println("SuperclassVisitableElement -> " + node.getSuperclassLongName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        writer.println("\"" + node.getGraphvizName() + "\"" + " [style=filled, fillcolor=\"#ff3232\"];");
        super.visitSuperclassElement(node);
        return 0;
    }

    @Override
    public Integer visitInterfaceElement(InterfaceElement node) {
        System.out.println("InterfaceVisitableElement -> " + node.getInterfaceLongName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        writer.println("\"" + node.getGraphvizName() + "\"" + " [style=filled, fillcolor=\"yellow\"];");
        super.visitInterfaceElement(node);
        return 0;
    }

    @Override
    public Integer visitMethodElement(MethodElement node) {
        System.out.println("MethodVisitableElement -> " + node.getMethodName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        writer.println("\"" + node.getGraphvizName() + "\"" + " [style=filled, fillcolor=\"#cfe2f3\"];");
        super.visitMethodElement(node);
        return 0;
    }

    @Override
    public Integer visitConstructorElement(ConstructorElement node) {
        System.out.println("ConstructorVisitableElement -> " + node.getMethodName());
        writer.println("\"" + node.getFirstParent().getGraphvizName() + "\"->\"" + node.getGraphvizName() + "\";");
        writer.println("\"" + node.getGraphvizName() + "\"" + " [style=filled, fillcolor=\"#6fa8dc\"];");
        super.visitConstructorElement(node);
        return 0;
    }
}
