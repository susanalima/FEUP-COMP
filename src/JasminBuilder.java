import java.util.Stack;


public class JasminBuilder {
  static String fN;
  static Stack instructionsStack = new Stack();

  public static String printJasmin(SimpleNode root, SymbolTable sT) {
    String acc = "";

    /*
     * if (root.children == null) { switch (root.toString()) { case "PUBLIC": acc +=
     * ".method public "; break; }
     * 
     * }
     */

    if (root.parent != null && root.parent.toString().equals("METHOD_DECLARATION") && root.toString().equals("IDENTIFIER")) {
      fN = root.val;
    }
    if (root.toString().equals("Args")) {
      if (root.parent.toString().equals("MainDeclaration")) {
        fN = "main";
      }
    }
    if (root.parent != null) {

      Node rpp = root.parent.jjtGetParent();
      if (rpp != null && rpp.toString().equals("Args")) {

        switch (root.toString()) {
        case "INT":
          fN += "&int";
          break;

        case "STRING":
          fN += "&string";
          break;

        case "ARRAY":
          fN += "$array";
          break;

        default:
          break;
        }
      }
    }

    if (root.toString().equals("DOT")) {
      acc += "invokenonvirtual ";
    } else if (root.toString().equals("IDENTIFIER") && root.parent != null) {
      String parent = root.parent.toString();
      if (parent.equals("FUNC")) {
        acc += root.val;
      } else if (parent.equals("DOT")) {
        acc += root.val + "/";
      } else if (parent.equals("FUNC_ARG")) {
        if (fN.equals("")) {
          System.out.println("FUNC NAME NOT DEFINED");
        }

        acc += sT.getVarType(fN, root.val) + ";";
      }
    } else if (root.toString().equals("FUNC_ARGS")) {
      acc += "(";
    }

    if (root.children != null) {
      for (Node child : root.children) {
        SimpleNode sN = (SimpleNode) child;
        acc += printJasmin(sN, sT);
      }
    }
    if (root.toString().equals("DOT")) {
      acc += "\n";
    } else if (root.toString().equals("FUNC_ARGS")) {
      acc += ")";
      String returnType = sT.getFunctionReturnType(fN);

      switch (returnType) {
        case "boolean":
        acc += "B";
        break;

        case "void":
        acc += "V";
        break;

        case "int":
        acc += "I";
        break;

        case "string":
        acc += "S";
        break;
      }
    }

    return acc;

  }


  public static String arithmeticJasmin(SimpleNode root, SymbolTable sT) {
      String instruction;
      switch(root.toString()){
        case "PLUS":
          instruction = "iadd";
          instructionsStack.push(instruction);
        break;
        case "MINUS":
          instruction = "isub";
          instructionsStack.push(instruction);
        break;
        case "PRODUCT":
          instruction = "imul";
          instructionsStack.push(instruction);
        break;
        case "DIVISION":
          instruction = "idiv";
          instructionsStack.push(instruction);
        break;
        case "EQUAL":
          instruction = "istore_"; //TODO: Qual o valor do istore?
          instructionsStack.push(instruction);
        break;
        default:
        break;
      }
      if (root.children != null) {
        for (Node child : root.children) {
          SimpleNode sN = (SimpleNode) child;
          arithmeticJasmin(sN, sT);
        }
    }
  }
}