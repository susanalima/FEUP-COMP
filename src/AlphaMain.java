
import java.util.LinkedList;

public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  private LinkedList<String> calledClassFunctions = new LinkedList<>();

  public static void main(String args[]) throws ParseException {
    if (args.length != 1) {
      System.out.println("Usage: Alpha <FileToParse>");
    }
    try {
      Alpha myCalc = new Alpha(new java.io.FileInputStream(args[0]));

      SimpleNode root = Alpha.Program();
      root.dump("");
      eval(root, "", SymbolTable.GLOBAL, State.BUILD);
      symbolTable.printSymbolTable();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String eval(SimpleNode node, String symbol, String funcname, State state) {

    String tmp = "";

    switch (node.getId()) {
    case AlphaTreeConstants.JJTPROGRAM:
    case AlphaTreeConstants.JJTARGS:
    case AlphaTreeConstants.JJTARG:
    case AlphaTreeConstants.JJTCLASSBODY:
      if(node.getId() == AlphaTreeConstants.JJTCLASSBODY)
        symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname, State.BUILD);
      }
      symbol += tmp;
      break;
    case AlphaTreeConstants.JJTPLUS:
    case AlphaTreeConstants.JJTMINUS:
    case AlphaTreeConstants.JJTPRODUCT:
    case AlphaTreeConstants.JJTDIVISION:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname, State.PROCESS);
      }
      symbol = tmp;
      break;
    case AlphaTreeConstants.JJTIDENTIFIER:
      if(state == State.BUILD) // if it is building state the symbol must be the name of the variable
        symbol = "#" + node.val;
      else if(state == State.PROCESS) { //if it is processing state the variable must be validated and and symbol is it's type
        symbol = symbolTable.getVarType(funcname, node.val); 
        if (symbol.equals("")) // if the variable was not declared aborts the program
          System.exit(0);
        symbol = "&" + symbol;
      }
      break;
    case AlphaTreeConstants.JJTINTEGER: // se estes tiverem um val pode se juntar tudo numa so condiçao
      symbol = "&int";
      break;
    case AlphaTreeConstants.JJTTRUE: // separar???
    case AlphaTreeConstants.JJTFALSE:
      symbol = "&boolean";
      break;  
    case AlphaTreeConstants.JJTINT:
      symbol = "#int";
      break;
    case AlphaTreeConstants.JJTSTRING:
      symbol = "#string";
      break;
    case AlphaTreeConstants.JJTARRAY:
      symbol = "array";
      break;
    case AlphaTreeConstants.JJTBOOLEAN:
      symbol = "#boolean";
      break;
    case AlphaTreeConstants.JJTVOID:
      symbol = "#void";
      break;
    case AlphaTreeConstants.JJTMAIN:
      symbol = "#main";
      break;
    case AlphaTreeConstants.JJTEXTENDS:
      symbolTable.setExtends();
      break;
    case AlphaTreeConstants.JJTEQUAL: // formato &type1&type2 etc...
      SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
      String varType = symbolTable.getVarType(funcname, identifier.val); // substituir pelo eval do primeiro filho (pode ser um array)
      if (varType.equals("")) // if the variable was not declared aborts the program
        System.exit(0);
      //symbol += "&" + varType + "=";
      for (int i = 1; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname, State.PROCESS);
      }
      symbol += tmp;
      //chamada a funçao de avaliaçao da expressao !! TODO
      if(!evaluateExpressionType(varType, symbol))
        System.exit(0);
      break;
    /*
     * case AlphaTreeConstants.JJTRETURN:
     * symbolTable.checkFunctionReturnType(funcname, eval((SimpleNode)
     * node.jjtGetChild(0), symbol, funcname)); break;
     */
    case AlphaTreeConstants.JJTVAR_DECLARATION:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER && i != 0) { // this means the type is done and the identifier is next
          symbol += tmp;
          String value = "local";
          if (funcname.equals(SymbolTable.GLOBAL))
            value = "global";
          symbolTable.addSymbol(funcname, child_node.val, new Var(symbol.split("#")[1], child_node.val, value));
        }
        tmp += eval(child_node, symbol, funcname, State.BUILD);
      }
      break;
    case AlphaTreeConstants.JJTMAINDECLARATION:
    case AlphaTreeConstants.JJTMETHOD_DECLARATION:
      symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        if (child_node.getId() == AlphaTreeConstants.JJTBODY) { // this means the arguments are over and can create the symboltable entry
          symbol += tmp;
          funcname = symbolTable.addFunction(symbol);
        }
        tmp += eval(child_node, symbol, funcname, State.BUILD);
      }
      break;
    /*
     * case AlphaTreeConstants.JJTDOT: if(node.jjtGetChild(0).getId() ==
     * AlphaTreeConstants.JJTTHIS && node.jjtGetChild(1).getId() ==
     * AlphaTreeConstants.JJTFUNC) { for (int i = 0; i <
     * node.jjtGetChild(1).jjtGetNumChildren(); i++) { SimpleNode child_node =
     * (SimpleNode) node.jjtGetChild(1).jjtGetChild(i); eval(child_node, symbol,
     * funcname); } } break;
     */
    default:
      symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        eval(child_node, symbol, funcname, state);
      }
      break;
    }
    return symbol;
  }


  public static boolean evaluateExpressionType(String expectedType, String expression) {
    String[] tokens = expression.split("&");
    for(int i = 1; i < tokens.length; i++) {
        if(!expectedType.equals(tokens[i]))
          return false;
    }
    return true;
  }

}

/* TESTING */
/*
 * System.out.println(symbolTable.methodExists("#GLOBAL_SCOPE"));
 * System.out.println(symbolTable.isVarGlobal("a4"));
 * System.out.println(symbolTable.methodExists(
 * "#int#ComputeFac#int#num#int#a#int#b#intarray#c"));
 * System.out.println(symbolTable.isVarLocal(
 * "#int#ComputeFac#int#num#int#a#int#b#intarray#c", "num_aux"));
 * System.out.println(symbolTable.isVarLocal(
 * "#int#ComputeFac#int#num#int#a#int#b#intarray#c", "num"));
 * System.out.println(symbolTable.isVarLocal(
 * "#int#ComputeFac#int#num#int#a#int#b#intarray#c", "a1"));
 * System.out.println(symbolTable.isVarLocal(
 * "#int#ComputeFac#int#num#int#a#int#b#intarray#c", "c"));
 * /*System.out.println(symbolTable.methodExists("#int#Compute#int#num"));
 * System.out.println(symbolTable.isVarLocal("#int#Compute#int#num",
 * "num_aux"));
 * System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "num"));
 * System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "a1"));
 * System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "num"));
 */
/* TESTING */