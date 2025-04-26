package com.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: Generate AST [output directory]");
            System.exit(64);
        }

        String dir = args[0];

        defineAst(dir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right"
        ));

    }

    private static void defineAst(String dir, String baseName, List<String> types) throws IOException {

       


        //create path for new file
        String outputFile = dir + '/' + baseName + ".java";

        //create a writer for the new file
        PrintWriter writer = new PrintWriter(outputFile, "UTF-8");

        //we will just put all the packages in com.jlox so can hardcode for now
        writer.println("package com.jlox;");

        //write to file basic structure
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + '{');


        //create visitor interface
        defineVisitor(writer, baseName, types);


         //example of list of types:

        // "Binary   : Expr left, Token operator, Expr right",
        // "Grouping : Expr expression",
        // "Literal  : Object value",
        // "Unary    : Token operator, Expr right"


        for (String type : types) {
            //isolate the class
            String className = type.split(":")[0].trim();

            //isolate the fields
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);

        }

        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");

        writer.println('}');
        writer.close();

    }


    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("  static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
        String name = field.split(" ")[1];
        writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        // Fields.
        writer.println();
        for (String field : fields) {
        writer.println("    final " + field + ";");
        }

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
            className + baseName + "(this);");
        writer.println("    }");
        writer.println("    }");

    }


    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        
        writer.println("  interface Visitor<R> {");
    
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" +
                typeName + " " + baseName.toLowerCase() + ");");
        }
    
        writer.println("  }");
    }
}
