
public class JasminTest {

  SymbolTable symbolTable;
  String code;

  JasminTest(SymbolTable sT) {
    this.symbolTable = sT;
    this.code = "";
  }

  public String jasmin_process(SimpleNode node, String symbol, String funcname, State state) {

    SimpleNode child_node;
    String tmp = "" ;

    switch (node.getId()) {

    case AlphaTreeConstants.JJTMAINDECLARATION:
    case AlphaTreeConstants.JJTMETHOD_DECLARATION:
      funcname = getFuncname(node, "", funcname, State.PROCESS);
       break;
    case AlphaTreeConstants.JJTIDENTIFIER:
      symbol +=  node.val;
      if(state == State.PROCESS) {
        code += "iload " + symbolTable.getCounter(funcname, node.val) + "\n";
      }
      break;
    case AlphaTreeConstants.JJTINT:
      symbol += "int";
      break;
    case AlphaTreeConstants.JJTINTEGER:
      symbol += "int";
      if(state == State.PROCESS) {
        code +=  "ldc " + node.val + "\n";
      }
      break;
    case AlphaTreeConstants.JJTTRUE:
    case AlphaTreeConstants.JJTFALSE:
      symbol +=  "boolean";
      if(state == State.PROCESS)
        code += "aload " + node.toString().toLowerCase() + "\n";
      break;
    case AlphaTreeConstants.JJTINDEX:
      break;
    case AlphaTreeConstants.JJTNEWFUNC:
    child_node =  (SimpleNode) node.jjtGetChild(1);
    if(child_node.getId() == AlphaTreeConstants.JJTFUNC) {
        child_node = (SimpleNode) child_node.jjtGetChild(0);
        code +="invokenonstatic " + child_node.val + "/" + child_node.val + "()L" + child_node.val + "\n";
      symbol = "";
      }
      break;
    case AlphaTreeConstants.JJTFUNC_ARG:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += "&" + jasmin_process(child_node, symbol, funcname, State.PROCESS);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTARGS:
      case AlphaTreeConstants.JJTARG:
      case AlphaTreeConstants.JJTFUNC_ARGS:
      case AlphaTreeConstants.JJTFUNC:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += jasmin_process(child_node, symbol, funcname, State.BUILD);
      }
      symbol += tmp;
      break;
    case AlphaTreeConstants.JJTDOT: 
      symbol = "";
      child_node = (SimpleNode) node.jjtGetChild(1); //left child
      String header = "";
      boolean checkMethod = true;
      if(child_node.getId() == AlphaTreeConstants.JJTFUNC) {
        child_node = (SimpleNode) node.jjtGetChild(0); //right child
        if(child_node.getId() ==  AlphaTreeConstants.JJTTHIS) {
          header = "invokevirtual " + symbolTable.getClassName() ;
        } else if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER) {
          if(symbolTable.varExists(funcname, child_node.val) && !symbolTable.extends_) {
            if(!symbolTable.getClassName().equals(symbolTable.getVarType(funcname, child_node.val)))
              checkMethod = false;
            header = "invokevirtual " + symbolTable.getVarType(funcname, child_node.val);
          } else {
            header = "invokestatic " + child_node.val;
            checkMethod = false;
          }
        } else if(child_node.getId() == AlphaTreeConstants.JJTNEWFUNC) {
            child_node = (SimpleNode) child_node.jjtGetChild(1);
            if(child_node.getId() == AlphaTreeConstants.JJTFUNC) {
              child_node = (SimpleNode) child_node.jjtGetChild(0);
              if (child_node.val.equals(symbolTable.getClassName())) {
                header = "invokevirtual " + symbolTable.getClassName() ;
             }
           }
        }
        else {
          checkMethod = false;
      }
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
          child_node = (SimpleNode) node.jjtGetChild(i);
          tmp +=  jasmin_process(child_node, symbol,funcname, state) ;
        }
        symbol += tmp;
        code += header + "/" + process_func_call(funcname,symbol, checkMethod) + "\n";
        symbol = symbolTable.eval_process(node, "", funcname, State.PROCESS).split("&")[1];
      } else if(child_node.getId() == AlphaTreeConstants.JJTLENGTH) {
        symbol = "int";
      }
      break;
    default:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        child_node = (SimpleNode) node.jjtGetChild(i);
        jasmin_process(child_node, symbol, funcname, State.BUILD);
      }
      break;
    }
    return symbol;
}


private String process_func_call(String funcname, String expression, boolean checkMethod) {
  String[] tokens = expression.split("&");
  String processed = tokens[0] + "(";
  expression = tokens[0];
  for (int i = 1; i < tokens.length; i++){
    if(!tokens[i].equals("boolean") && !tokens[i].equals("int") && !tokens[i].equals("int$array")){ //TODO
      tokens[i] = symbolTable.getVarType(funcname, tokens[i]);
    }
    expression += "&" + tokens[i];
  }

  if(checkMethod)
    expression = symbolTable.methodExistsWithUndefinedValues(expression);

  tokens = expression.split("&");
  for (int i = 1; i < tokens.length; i++){
    processed += tokens[i] + ";";
  }
  processed += ")";


  

  if (!expression.equals("") && checkMethod) {  
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


public String getFuncname(SimpleNode node, String symbol, String funcname, State state) {        
  SimpleNode child_node;
  String tmp = "";
  State currState = State.BUILD;
  for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      if (child_node.getId() == AlphaTreeConstants.JJTBODY) { 
          
          if(state == State.PROCESS) {
              currState = state;
          }
          symbol += tmp;
          funcname = symbolTable.processFunction(symbol, false);
      }

      if(currState == State.BUILD)
          tmp += symbolTable.eval_build(child_node, symbol, funcname, currState);
      else if(currState == State.PROCESS)
          tmp += jasmin_process(child_node, "", funcname, currState);
  }
  return funcname;
}

}
