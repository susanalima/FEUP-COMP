
import java.util.LinkedList;
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  private LinkedList<String> calledClassFunctions = new LinkedList<>(); //TODO AGORA USA ANALISE SEQUENCIAL

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
      
      JasminBuilder jBuilder = new JasminBuilder(symbolTable);
      String jasmin = jBuilder.printJasmin(root);
      System.out.println(jasmin);

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
      if (node.getId() == AlphaTreeConstants.JJTCLASSBODY)
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
    case AlphaTreeConstants.JJTMINOR:
    case AlphaTreeConstants.JJTAND:
    case AlphaTreeConstants.JJTFUNC_ARGS:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname, State.PROCESS);
      }
      symbol = tmp;
      if(node.getId() == AlphaTreeConstants.JJTMINOR)
        symbol = "&boolean";
      break;
    case AlphaTreeConstants.JJTIDENTIFIER:
      // System.out.println(state + " " + node.val);
      if (state == State.BUILD) // if it is building state the symbol must be the name of the variable
        symbol = "#" + node.val;
      else if (state == State.PROCESS) { // if it is processing state the variable must be validated and and symbol is
                                         // it's type
        symbol = symbolTable.getVarType(funcname, node.val);
        if (symbol.equals("")) {// if the variable was not declared aborts the program
          System.out.println("Variable not declared: " + node.val);
          System.exit(0); 
        }
        symbol = "&" + symbol;
      }
      break;
    case AlphaTreeConstants.JJTEXTENDS:
      symbolTable.setExtends();
      break;
    case AlphaTreeConstants.JJTLENGTH:
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
      symbol = "$array";
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

    case AlphaTreeConstants.JJTIF:
    case AlphaTreeConstants.JJTWHILE:
    symbol = "";
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
      symbol = eval(child_node, symbol, funcname, state);
    }
      break;
    case AlphaTreeConstants.JJTCONDITION:
    symbol = "";
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
      symbol = eval(child_node, symbol, funcname, State.PROCESS);
    }
    tmp = returnExpressionType(symbol);
    if(!tmp.equals("boolean") && !tmp.equals("undefined")) {
      System.out.println("Invalid Condition!");
      System.exit(0);
    }
    break;
    case AlphaTreeConstants.JJTINDEX:
      symbol = eval((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS); // validates the identifier
      String index = eval((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.PROCESS); // validates the index

      if (!symbol.contains("$array") || !evaluateExpressionInt(index)) // case the variable was declared but is not an array or the index is not and int
      {
        System.out.println("Variable not an array or index not an integer");
        System.exit(0);
      }                                                         
       

      symbol = symbol.split("\\$")[0];
      break;
    case AlphaTreeConstants.JJTEQUAL: // formato &type1&type2 etc...
      SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
      String varType = eval(identifier, symbol, funcname, State.PROCESS);
      for (int i = 1; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname, State.PROCESS);
      }
      symbol += tmp;
      if (!evaluateExpressionType(varType, symbol)) {
        System.out.println("Invalid type");
        System.exit(0);
      }
        
      break;

    case AlphaTreeConstants.JJTRETURN:
      if(!symbolTable.checkFunctionReturnType(funcname,
          eval((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS).split("&")[1])) {
            System.out.println("Invalid return type");
            System.exit(1);
          }
      break;

    case AlphaTreeConstants.JJTVAR_DECLARATION:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER && i != 0) { // this means the type is done and the
                                                                                // identifier is next
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
        if (child_node.getId() == AlphaTreeConstants.JJTBODY) { // this means the arguments are over and can create the
                                                                // symboltable entry
          symbol += tmp;
          funcname = symbolTable.addFunction(symbol);
        }
        tmp += eval(child_node, symbol, funcname, State.BUILD);
      }
      break;
    case AlphaTreeConstants.JJTFUNC_ARG:
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        symbol = eval(child_node, symbol, funcname, State.PROCESS);
        if(symbol.equals(""))
          continue;
        symbol = "&" + returnExpressionType(symbol); 
        if(symbol.equals("&")) {
          System.out.println("Invalid type 1");
          System.exit(0);
        }
         
      }
      break;

    case AlphaTreeConstants.JJTFUNC:
      symbol = ((SimpleNode) node.jjtGetChild(0)).val;    
      for (int i = 1; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        symbol += eval(child_node, symbol, funcname, State.PROCESS);
      }
      break;

    case AlphaTreeConstants.JJTDOT:
      if (node.jjtGetChild(0).getId() == AlphaTreeConstants.JJTTHIS) { // if first child is THIS eval function
        symbol = eval((SimpleNode) node.jjtGetChild(1), symbol, funcname, state);
        if (!symbolTable.methodExists(symbol)) { // se a funçao com aqueles argumentos nao existir
          System.out.println("Invalid function");
          System.exit(0);
        }
        else 
          symbol = "&" + symbolTable.getFunctionReturnType(symbol);
      } else {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
          SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
          symbol = eval(child_node, symbol, funcname, state);
        }
      }
      break;

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


  public static String returnExpressionType(String expression) {
    if(!expression.substring(0,1).equals("&")) //if it is a outside function 
      return "undefined";
    String expectedType = expression.split("&")[1];
    if(evaluateExpressionType("&" + expectedType, expression))
      return expectedType;
    else
      return "";
  }


  public static boolean evaluateExpressionType(String expectedType, String expression) {
    String[] tokens = expression.split("&");
    String processed_expectedType = expectedType.split("&")[1];
    if (processed_expectedType.equals("undefined"))
      return true;
    for (int i = 1; i < tokens.length; i++) {
      if (!processed_expectedType.equals(tokens[i]))
        return false;
    }
    return true;
  }

  public static boolean evaluateExpressionInt(String expression) {
    return evaluateExpressionType("&int", expression);
  }

 

}
