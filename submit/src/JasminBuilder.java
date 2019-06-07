
public class JasminBuilder {
  String fN;
  String invokingFN;
  SymbolTable sT;
  String actualFunction = "#GLOBAL_SCOPE";
  String fullInstructions = "";

  public JasminBuilder(SymbolTable sT_) {
    fN = "";
    invokingFN = "";
    sT = sT_;

  }

  private String paramType(String returnType) {
    switch (returnType) {
    case "boolean":
      return "Z";

    case "void":
      return "V";

    case "int":
      return "I";

    case "string":
      return "S";

    case "int$array":
      return "[I";

    default:
      return "L" + returnType + ";";
    }
  }

  public String printJasmin(SimpleNode root) {
    String acc = "";

    buildFunctionNameName(root);
    buildFunctionNameParameters(root, sT);

    if (root.toString().equals("DOT")) {
      if (isFunc(root)) {
        acc += this.loadParameters(root, false);
      }

    } else if ((root.toString().equals("IDENTIFIER") || root.toString().equals("THIS")
        || root.toString().equals("INTEGER") || root.toString().equals("FALSE") || root.toString().equals("TRUE"))
        && root.parent != null) {
      String parent = root.parent.toString();
      if (parent.equals("FUNC")) {
        if (((SimpleNode) root.parent).parent.toString().equals("newFunc")) {
          acc += "invokenonstatic " + root.val + "/" + root.val + "()L" + root.val + "\n";
        } else {
          acc += root.val;
        }
      } else if (parent.equals("DOT")) {
        if (root.toString().equals("IDENTIFIER")) {
          if (sT.varExists(fN, root.val) && isFunc((SimpleNode) root.parent)) {
            acc += "invokevirtual " + sT.getVarType(fN, root.val) + "/";
          } else if (isFunc((SimpleNode) root.parent)) {
            acc += "invokestatic " + root.val + "/";
          }
        } else if (root.toString().equals("THIS")) {
          String thisClass = getThisClass(root);
          acc += "invokevirtual " + thisClass + "/";
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
          acc += paramType(sT.getVarType(fN, root.val)) + ";";
        }
      }
    } else if (root.toString().equals("FUNC_ARGS")) {
      acc += "(";
    }
    if (root.toString().equals("DOT")) {
      // acc += "\n\n";
    }

    if (root.children != null) {
      for (Node child : root.children) {
        SimpleNode sN = (SimpleNode) child;
        acc += printJasmin(sN);
      }
    }
    if (root.toString().equals("FUNC_ARGS")) {
      acc += ")";
      System.out.println(invokingFN);
      if (sT.methodExists(invokingFN)) {
        String returnType = sT.getFunctionReturnType(invokingFN);

        acc += paramType(returnType);
      } else {
        acc += "externalUndefined";
      }
      acc += "\n\n";

    }

    return acc;

  }

  private String loadParameters(SimpleNode node, boolean processed) {
    String acc = "";

    if (!processed) {
      fN = sT.processFunction(fN);
    }

    if (node.parent != null && node.parent.toString().equals("FUNC_ARG")) {
      if (fN.equals("")) {
        System.out.println("FUNC NAME NOT DEFINED");
      }

      if (node.toString().equals("INTEGER")) {
        acc += "ldc " + node.val + ";\n";
      } else if (node.toString().equals("FALSE") || node.toString().equals("TRUE")) {
        acc += "aload " + node.toString().toLowerCase() + ";\n";
      } else if (node.toString().equals("DOT")) {

      } else {
        System.out.println(node.val + "\t" + node.toString());
        acc += "iload " + sT.getCounter(fN, node.val) + ";\n";
      }
    }

    if (node.children != null) {
      for (Node child : node.children) {
        SimpleNode sN = (SimpleNode) child;
        acc += loadParameters(sN, true);
      }
    }

    return acc;
  }

  private boolean isFunc(SimpleNode node) {
    boolean ret = false;

    if (node.toString().equals("FUNC")) {
      return true;
    }

    if (node.children != null) {
      for (Node n : node.children) {
        SimpleNode sN = (SimpleNode) n;
        ret = ret || isFunc(sN);
      }
    }

    return ret;
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

        case "BOOLEAN":
          fN += "&boolean";
          break;

        case "IDENTIFIER":
          fN += "&" + root.val;
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

  public String getMethodKey(SimpleNode root) {
    String key = "";
    String paramType;
    for (Node child : root.children) {
      SimpleNode simpleChild = (SimpleNode) child;
      if (simpleChild.toString().equals("IDENTIFIER")) {
        key = simpleChild.val;
      } else if (simpleChild.toString().equals("MAIN")) {
        key = "main";
      }

      if (simpleChild.toString().equals("Args")) {
        if (simpleChild.children != null) {
          for (Node grandchild : simpleChild.children) {
            SimpleNode simpleGrandChild = (SimpleNode) grandchild;
            paramType = simpleGrandChild.children[0].toString();
            paramType = paramType.toLowerCase();
            if (paramType.equals("identifier")) // in case is an identifier
              paramType = ((SimpleNode) simpleGrandChild.children[0]).val;
            key = key.concat("&" + paramType);
            if (simpleGrandChild.children[1].toString().equals("ARRAY"))
              key = key.concat("$array");
          }
        }
        return key;
      }
    }
    return key;
  }

  // TODO: Stack usage is only needed in arithmetic expressions x = (...) ->
  // after/inside an EQUAL ; everything else is just concat
  // DOUBT: Since variables need to be initialized to be used in a expression,
  // when needed, the jasmin code should put in the stack the variable or the
  // value of the variable?
  // DOUBT: Indexes of local variables? 0 - this ; 1 - next variable?parameter?
  public String arithmeticJasmin(SimpleNode root) {
    String instruction, ident, value;
    instruction = "";
    int counter;

    if (root.toString().equals("METHOD_DECLARATION") || root.toString().equals("MainDeclaration"))
      actualFunction = getMethodKey(root);

    if (root.parent != null && !root.parent.toString().equals("METHOD_DECLARATION")
        && !root.parent.toString().equals("MainDeclaration") && !root.parent.toString().equals("Arg")
        && !root.parent.toString().equals("FUNC_ARG")) {
      switch (root.toString()) {
      case "PLUS":
        instruction = "iadd";
        break;
      case "MINUS":
        instruction = "isub";
        break;
      case "PRODUCT":
        instruction = "imul";
        break;
      case "DIVISION":
        instruction = "idiv";
        break;
      case "EQUAL":
        SimpleNode leftSide = (SimpleNode) root.children[0];
        if (leftSide.toString().equals("INDEX")) // in case it is an array assignment
          leftSide = (SimpleNode) leftSide.jjtGetChild(0);
        ident = leftSide.val;
        if (sT.varExists(actualFunction, ident)) {
          Symbol symb;

          if (sT.isVarGlobal(ident))
            symb = sT.symbolTable.get("#GLOBAL_SCOPE").contents.get(ident);

          else
            symb = sT.symbolTable.get(actualFunction).contents.get(ident);

          counter = symb.counter;
          instruction = "istore_" + counter;
        }
        break;
      case "IDENTIFIER":
        ident = root.val;
        try {
          if (sT.varExists(actualFunction, ident) && !root.parent.toString().equals("VAR_DECLARATION")
              && !root.parent.toString().equals("FUNC") && !root.parent.toString().equals("Program")
              && !root.parent.toString().equals("EQUAL") && !root.parent.toString().equals("RETURN")) {
            Symbol symb;
            if (sT.isVarGlobal(ident))
              symb = sT.symbolTable.get("#GLOBAL_SCOPE").contents.get(ident);
            else
              symb = sT.symbolTable.get(actualFunction).contents.get(ident);
            System.out.println(symb.toString());
            counter = symb.counter;
            instruction = "iload_" + counter;
          }
        } catch (NullPointerException e) {
          System.out.println("The information being accessed is not defined.");
          return "-2";
        }
        break;
      case "INTEGER":
        instruction = "ldc " + root.val;
        break;
      default:
        break;
      }
    }
    if (root.children != null) {
      for (Node child : root.children) {
        SimpleNode sN = (SimpleNode) child;
        arithmeticJasmin(sN);
      }
    }
    if (instruction != "")
      fullInstructions = fullInstructions.concat(instruction + "\n");

    return fullInstructions;
  }
}