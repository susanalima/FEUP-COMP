
public class JasminTest {

  SymbolTable symbolTable;
  String code;

  JasminTest(SymbolTable sT) {
    this.symbolTable = sT;
    this.code = "";
  }

  public String jasmin_process(SimpleNode node, String symbol, String funcname, State state) {

    switch (node.getId()) {

    case AlphaTreeConstants.JJTMAINDECLARATION:
    case AlphaTreeConstants.JJTMETHOD_DECLARATION:
      funcname = getFuncname(node, "", funcname, State.PROCESS);
      break;
    case AlphaTreeConstants.JJTIDENTIFIER:
      symbol = jasmin_process_nodeFuncDeclaration(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTINT:
      symbol += "int";
      break;
    case AlphaTreeConstants.JJTINTEGER:
      symbol += "int";
      // if (state == State.PROCESS) { //TODO TEST IF NEEDED, DONT THINK SO
      code += "ldc " + node.val + "\n";
      // }
      break;
    case AlphaTreeConstants.JJTINDEX:
      break;
    case AlphaTreeConstants.JJTTRUE:
    case AlphaTreeConstants.JJTFALSE:
      symbol += "boolean";
      if (state == State.PROCESS)
        code += "aload " + node.toString().toLowerCase() + "\n";
      break;
    case AlphaTreeConstants.JJTPLUS:
    case AlphaTreeConstants.JJTMINUS:
    case AlphaTreeConstants.JJTPRODUCT:
    case AlphaTreeConstants.JJTDIVISION:
      symbol = jasmin_process_nodeOperator(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTNEWFUNC:
      symbol = jasmin_process_nodeNewFunc(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTFUNC_ARG:
      symbol = jasmin_process_nodeFuncArg(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTARGS:
    case AlphaTreeConstants.JJTARG:
    case AlphaTreeConstants.JJTFUNC_ARGS:
    case AlphaTreeConstants.JJTFUNC:
      symbol = jasmin_process_nodeFunc(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTDOT:
      symbol = jasmin_process_nodeDot(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTEQUAL:
      symbol = jasmin_process_nodeEqual(node, symbol, funcname, state); // TYPE
      break;
    default:
      jasmin_process_nodeDefault(node, symbol, funcname, state);
      break;
    }
    return symbol;

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

  private String process_func_call(String funcname, String expression, boolean checkMethod) {
    String[] tokens = expression.split(SymbolTable.AND_SEPARATOR);
    String processed = tokens[0] + "(";
    // expression = tokens[0];
    /*
     * for (int i = 1; i < tokens.length; i++) { if (!tokens[i].equals("boolean") &&
     * !tokens[i].equals("int") && !tokens[i].equals("int$array")) { // TODO
     * tokens[i] = symbolTable.getVarType(funcname, tokens[i]); } expression += "&"
     * + tokens[i]; }
     */

    if (checkMethod)
      expression = symbolTable.methodExistsWithUndefinedValues(expression);

    tokens = expression.split(SymbolTable.AND_SEPARATOR);
    for (int i = 1; i < tokens.length; i++) {
      processed += paramType(tokens[i]) + ";";
    }
    processed += ")";

    if (!expression.equals("") && checkMethod) {
      String returnType = symbolTable.getFunctionReturnType(expression);
      processed += paramType(returnType);
    }

    return processed;
  }

  public String getFuncname(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node;
    String tmp = "";
    State currState = State.BUILD;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      if (child_node.getId() == AlphaTreeConstants.JJTBODY) {

        if (state == State.PROCESS) {
          currState = state;
        }
        symbol += tmp;
        funcname = symbolTable.processFunction(symbol, false);
      }

      if (currState == State.BUILD)
        tmp += symbolTable.eval_build(child_node, symbol, funcname, currState);
      else if (currState == State.PROCESS)
        tmp += jasmin_process(child_node, "", funcname, currState);
    }
    return funcname;
  }

  private String getOperatorInstruction(SimpleNode node) {
    String instruction = "";

    switch (node.getId()) {
    case AlphaTreeConstants.JJTPLUS:
      instruction = "iadd \n";
      break;
    case AlphaTreeConstants.JJTMINUS:
      instruction = "isub \n";
      break;
    case AlphaTreeConstants.JJTPRODUCT:
      instruction = "imul \n";
      break;
    case AlphaTreeConstants.JJTDIVISION:
      instruction = "idiv \n";
      break;
    }

    return instruction;
  }

  private String jasmin_process_nodeFuncDeclaration(SimpleNode node, String symbol, String funcname, State state) {
    if (state == State.PROCESS) {
      symbol += symbolTable.getVarType(funcname, node.val);
      code += "iload_" + symbolTable.getCounter(funcname, node.val) + "\n";
    } else {
      symbol += node.val;
    }
    return symbol;
  }

  private String jasmin_process_nodeOperator(SimpleNode node, String symbol, String funcname, State state) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += SymbolTable.AND_SEPARATOR + jasmin_process(child_node, symbol, funcname, State.PROCESS);
    }
    symbol = symbolTable.returnExpressionType(tmp);
    code += getOperatorInstruction(node);
    return symbol;
  }

  private String jasmin_process_nodeNewFunc(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node;
    child_node = (SimpleNode) node.jjtGetChild(1);
    if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
      child_node = (SimpleNode) child_node.jjtGetChild(0);
      code += "invokenonstatic " + child_node.val + "/" + child_node.val + "()L" + child_node.val + ";\n";
      symbol = "";
    }
    return symbol;
  }

  private String jasmin_process_nodeFuncArg(SimpleNode node, String symbol, String funcname, State state) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += SymbolTable.AND_SEPARATOR + jasmin_process(child_node, symbol, funcname, State.PROCESS);
    }
    symbol += tmp;
    return symbol;
  }

  private String jasmin_process_nodeFunc(SimpleNode node, String symbol, String funcname, State state) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += jasmin_process(child_node, symbol, funcname, State.BUILD);
    }
    symbol += tmp;
    return symbol;
  }

  private String jasmin_process_nodeDot(SimpleNode node, String symbol, String funcname, State state) {
    String tmp = "";
    SimpleNode child_node;
    symbol = "";
    child_node = (SimpleNode) node.jjtGetChild(1); // left child
    String header = "";
    boolean checkMethod = true;
    if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
      child_node = (SimpleNode) node.jjtGetChild(0); // right child
      if (child_node.getId() == AlphaTreeConstants.JJTTHIS) {
        header = "invokevirtual " + symbolTable.getClassName();
      } else if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER) {
        if (symbolTable.varExists(funcname, child_node.val) && !symbolTable.extends_) {
          if (!symbolTable.getClassName().equals(symbolTable.getVarType(funcname, child_node.val)))
            checkMethod = false;
          header = "invokevirtual " + symbolTable.getVarType(funcname, child_node.val);
        } else {
          header = "invokestatic " + child_node.val;
          checkMethod = false;
        }
      } else if (child_node.getId() == AlphaTreeConstants.JJTNEWFUNC) {
        child_node = (SimpleNode) child_node.jjtGetChild(1);
        if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
          child_node = (SimpleNode) child_node.jjtGetChild(0);
          if (child_node.val.equals(symbolTable.getClassName())) {
            header = "invokevirtual " + symbolTable.getClassName();
          }
        }
      } else {
        checkMethod = false;
      }
      for (int i = 1; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += jasmin_process(child_node, symbol, funcname, state);
      }
      symbol += tmp;
      code += header + "/" + process_func_call(funcname, symbol, checkMethod) + "\n";
      symbol = symbolTable.eval_process(node, "", funcname, State.PROCESS).split(SymbolTable.AND_SEPARATOR)[1];
    } else if (child_node.getId() == AlphaTreeConstants.JJTLENGTH) {
      symbol = "int";
    }
    return symbol;
  }

  private String jasmin_process_nodeEqual(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      jasmin_process(child_node, symbol, funcname, State.BUILD);
    }
    child_node = (SimpleNode) node.children[1]; // right child
    if (child_node.getId() == AlphaTreeConstants.JJTMINOR) // TODO WHAT TO DO WHEN IS A BOOLEAN ASSIGMENT WITH MINOR???
      return symbol;
    if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER) // CASE IT IS AN IDENTIFIER LIKE  a = s
      jasmin_process(child_node, symbol, funcname, State.PROCESS);
    child_node = (SimpleNode) node.children[0]; // left child -> identifier
    if (child_node.getId() == AlphaTreeConstants.JJTINDEX) // in case it is an array assignment
      child_node = (SimpleNode) child_node.jjtGetChild(0);
    code += "istore_" + symbolTable.getCounter(funcname, child_node.val) + "\n"; // TODO CHANGE ACCORDING WITH THE

    return symbol;
  }

  private void jasmin_process_nodeDefault(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      jasmin_process(child_node, symbol, funcname, State.BUILD);
    }
  }

}
