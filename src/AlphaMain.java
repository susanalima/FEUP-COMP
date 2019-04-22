
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
      symbolTable.eval(root, "", SymbolTable.GLOBAL, State.BUILD); 
      symbolTable.printSymbolTable();
      
      System.out.println("---Jasmin---\n");
      JasminBuilder jBuilder = new JasminBuilder(symbolTable);
      String jasmin = jBuilder.printJasmin(root);
      jasmin = jasmin.concat("\n\nArithmetic \n\n" + jBuilder.arithmeticJasmin(root));
      System.out.println(jasmin);
    

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
 

}
