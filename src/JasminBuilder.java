public class JasminBuilder {
  String fN;
  String invokingFN;

  public JasminBuilder() {
    fN = "";
    invokingFN = "";
  }

  public String printJasmin(SimpleNode root, SymbolTable sT) {
    String acc = "";

    buildFunctionNameName(root);
    buildFunctionNameParameters(root, sT);

    if (root.toString().equals("DOT")) {
      // acc += "invokenonvirtual ";
    } else if ((root.toString().equals("IDENTIFIER") || root.toString().equals("THIS")
        || root.toString().equals("INTEGER") || root.toString().equals("FALSE") || root.toString().equals("TRUE"))
        && root.parent != null) {
      String parent = root.parent.toString();
      if (parent.equals("FUNC")) {
        acc += root.val;
      } else if (parent.equals("DOT")) {
        if (root.toString().equals("IDENTIFIER")) {
          acc += "invokestatic " + root.val + "/";
        } else if (root.toString().equals("THIS")) {
          String thisClass = getThisClass(root);
          acc += "invokenonvirtual " + thisClass + "/";
        }

      } else if (parent.equals("FUNC_ARG")) {
        if (fN.equals("")) {
          System.out.println("FUNC NAME NOT DEFINED");
        }

        if (root.toString().equals("INTEGER")) {
          acc += "int;";
        } else if (root.toString().equals("FALSE") || root.toString().equals("TRUE")) {
          acc += "boolean;";
        } else {
          acc += sT.getVarType(fN, root.val) + ";";
        }
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
      acc += "\n\n";
    } else if (root.toString().equals("FUNC_ARGS")) {
      acc += ")";
      if (sT.methodExists(invokingFN)) {
        String returnType = sT.getFunctionReturnType(invokingFN);

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
      } else {
        acc += "externalUndefined";
      }
    }

    return acc;

  }

  private String insertSubString(String existingString, String insertString, String after) {
    System.out.println("EX " + existingString);
    int index = existingString.lastIndexOf(after);
    String newString = existingString.substring(0, index + 1) + insertString + existingString.substring(index + 1);
    System.out.println(index);
    System.out.println(newString);
    return newString;
  }

  private void buildFunctionNameName(SimpleNode root) {

    if (root.parent != null && root.parent.toString().equals("METHOD_DECLARATION")
        && root.toString().equals("IDENTIFIER")) {
      fN = root.val;
    }
    if (root.toString().equals("Args")) {
      if (root.parent.toString().equals("MainDeclaration")) {
        fN = "main";
      }
    }

    if (root.parent != null && root.parent.toString().equals("FUNC") && root.toString().equals("IDENTIFIER")) {
      invokingFN = root.val;
    }
  }

  private void buildFunctionNameParameters(SimpleNode root, SymbolTable sT) {
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
      if (rpp != null && rpp.toString().equals("FUNC_ARGS")) {

        switch (root.toString()) {
        case "INTEGER":
          invokingFN += "&int";
          break;

        case "FALSE":
        case "TRUE":
          invokingFN += "&boolean";
          break;

        case "ARRAY":
          invokingFN += "$array";
          break;

        case "IDENTIFIER":
          invokingFN += "&" + sT.getVarType(fN, root.val);
          break;
        default:
          break;
        }
      }
    }
  }

  private String getThisClass(Node node) {
    while (node.jjtGetParent() != null) {
      node = node.jjtGetParent();
    }

    int numChild = node.jjtGetNumChildren();
    for (int i = 0; i < numChild; i++) {
      Node n = node.jjtGetChild(i);

      if (n.toString().equals("IDENTIFIER")) {
        SimpleNode sN = (SimpleNode) n;
        return sN.val;
      }
    }

    return "ERROR";
  }
}