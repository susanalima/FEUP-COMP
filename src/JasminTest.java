
public class JasminTest {

  SymbolTable symbolTable;

  JasminTest(SymbolTable sT) {
    this.symbolTable = sT;
  }

  public String jasmin_process(SimpleNode node, String symbol, State state) {

    SimpleNode child_node;
    String tmp = "", funcname; //TODO buscar o nome da funçao atual
    switch (node.getId()) {
    case AlphaTreeConstants.JJTIDENTIFIER:
      symbol +=  node.val;
      if(state == State.PROCESS)
        System.out.println( "iload " + symbolTable.getCounter("main&string$array", node.val) + ";");
      break;
    case AlphaTreeConstants.JJTLENGTH:
    case AlphaTreeConstants.JJTINTEGER:
      symbol += "int";
      break;
    case AlphaTreeConstants.JJTTRUE:
    case AlphaTreeConstants.JJTFALSE:
      symbol +=  "boolean";
      System.out.println("aload " + node.toString().toLowerCase() + ";");
      break;
    case AlphaTreeConstants.JJTARRAY:
      break;
    case AlphaTreeConstants.JJTINDEX:
      break;
    case AlphaTreeConstants.JJTNEWFUNC:
      break;
      case AlphaTreeConstants.JJTFUNC_ARG:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += "&" + jasmin_process(child_node, symbol, State.PROCESS);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTFUNC_ARGS:
      case AlphaTreeConstants.JJTFUNC:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += jasmin_process(child_node, symbol, State.BUILD);
      }
      symbol += tmp;
      break;
    case AlphaTreeConstants.JJTDOT:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp +=  jasmin_process(child_node, symbol, state) ;
      }
      symbol += tmp;
      System.out.println("invokevirtual " + symbolTable.getClassName() + "/" + process_func_call("main&string$array",symbol));
      symbol = symbolTable.eval_process(node, "", "main&string$array", State.PROCESS).split("&")[1];
      break;
    default:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        jasmin_process(child_node, symbol, state);
      }
      break;
    }
    return symbol;
}


private String process_func_call(String funcname, String expression) {
  String[] tokens = expression.split("&");
  String processed = tokens[0] + "(";
  expression = tokens[0];
  for (int i = 1; i < tokens.length; i++){
    if(!tokens[i].equals("boolean") && !tokens[i].equals("int") && !tokens[i].equals("int$array")){ //TODO
      tokens[i] = symbolTable.getVarType(funcname, tokens[i]);
    }
    processed += tokens[i] + ";";
    expression += "&" + tokens[i];
  }
  processed += ")";
  if (symbolTable.methodExists(expression)) {
    String returnType = symbolTable.getFunctionReturnType(expression);
    switch (returnType) {
    case "boolean":
    processed += "Z";
      break;
    case "void":
    processed += "V";
      break;
    case "int":
    processed += "I";
      break;
    case "string":
    processed += "S";
      break;
    case "int$array":
    processed += "[I";
      break;
    default:
    processed += "L" + returnType;
    }
  } else {
    processed += "externalUndefined";
  }
  return processed;
}

}
