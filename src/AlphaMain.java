
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  public static void main(String args[]) throws ParseException {
    if (args.length != 1) {
      System.out.println("Usage: Alpha <FileToParse>");
    }
    try {
      Alpha myCalc = new Alpha(new java.io.FileInputStream(args[0]));

      SimpleNode root = myCalc.Program();
      root.dump("");
      System.out.println(eval(root, "", "#GLOBAL_SCOPE"));

      /* TESTING */


      System.out.println(symbolTable.methodExists("#GLOBAL_SCOPE"));
      System.out.println(symbolTable.isVarGlobal("a4"));


      System.out.println(symbolTable.methodExists("#int#ComputeFac#int#num#int#a#int#b#intarray#c"));
      System.out.println(symbolTable.isVarLocal("#int#ComputeFac#int#num#int#a#int#b#intarray#c", "num_aux"));
      System.out.println(symbolTable.isVarLocal("#int#ComputeFac#int#num#int#a#int#b#intarray#c", "num"));
      System.out.println(symbolTable.isVarLocal("#int#ComputeFac#int#num#int#a#int#b#intarray#c", "a1"));

      System.out.println(symbolTable.methodExists("#int#Compute#int#num"));
      System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "num_aux"));
      System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "num"));
      System.out.println(symbolTable.isVarLocal("#int#Compute#int#num", "a1"));
      /* TESTING */


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String eval(SimpleNode node, String symbol, String funcname) {

    if (node.val != null && !symbol.equals("")) {
      //System.out.println(node.getId());
      System.out.println(symbol);
    }
      

    String tmp = "";

    switch(node.getId()) {
      case AlphaTreeConstants.JJTPROGRAM :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTCLASS  :
      symbol = "";
      break;
      case AlphaTreeConstants.JJTIDENTIFIER :
      symbol = "#" + node.val;
      break;
      case AlphaTreeConstants.JJTINT :
      symbol = "#int";
      break;
      case AlphaTreeConstants.JJTARRAY :
      symbol = "array";
      break;
      case AlphaTreeConstants.JJTBOOLEAN:
      symbol = "#boolean";
      break;
      case AlphaTreeConstants.JJTBODY :
      symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTVAR_DECLARATION :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        if(child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER){ //this means the type is done and the identifier is next
          symbol += tmp;
          symbolTable.addSymbol(funcname, child_node.val, new Var(symbol, child_node.val, "hello"));
          System.out.println("funcname: " + funcname);
          System.out.println("val: " + child_node.val);
          System.out.println("type/symbol: " + symbol);
        }
        tmp += eval(child_node, symbol, funcname);
      }
      break;
      case AlphaTreeConstants.JJTMAINDECLARATION :
      break;
      case AlphaTreeConstants.JJTARGS :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTARG :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol, funcname);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTMETHOD_DECLARATION :
      symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        if(child_node.getId() == AlphaTreeConstants.JJTBODY){ //this means the argument are over and can create the symboltable entry
          symbol += tmp;
          symbolTable.addSymbol(symbol, "", null);
          funcname = symbol;
        }
        tmp += eval(child_node, symbol, funcname);
      }
      break;
      default:
      symbol = " ";
      break;
    }
    return symbol;
  }
}
