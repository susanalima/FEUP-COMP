
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
      System.out.println(eval(root, ""));
      System.out.println(symbolTable.methodExists("#int#ComputeFac#int#num#int#a#int#b#intarray#c"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String eval(SimpleNode node, String symbol) {

    if (node.val != null) {
      System.out.println(node.getId());
      System.out.println(symbol);
    }
      

    String tmp = "";

    switch(node.getId()) {
      case AlphaTreeConstants.JJTPROGRAM :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol);
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
      break;
      case AlphaTreeConstants.JJTBODY :
      symbol = "";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTVAR_DECLARATION :
      break;
      case AlphaTreeConstants.JJTMAINDECLARATION :
      break;
      case AlphaTreeConstants.JJTARGS :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol);
      }
      symbol += tmp;
      break;
      case AlphaTreeConstants.JJTARG :
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(i);
        tmp += eval(child_node, symbol);
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
        }
        tmp += eval(child_node, symbol);
      }
      break;
      default:
      symbol = " ";
      break;
    }
    return symbol;
  }
}
