
import java.util.LinkedList;
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  public static void main(String args[]) throws ParseException {
    if (args.length != 1) {
      System.out.println("Usage: Alpha <FileToParse>");
    }
    try {
      Alpha myCalc = new Alpha(new java.io.FileInputStream(args[0]));

      SimpleNode root = Alpha.Program();
      root.dump("");
      symbolTable.eval_build(root, "", SymbolTable.GLOBAL, State.BUILD); 
      symbolTable.printSymbolTable();
      symbolTable.eval_process(root, "", SymbolTable.GLOBAL, State.PROCESS); 

      System.out.println("---Jasmin---\n");
      JasminBuilder jBuilder = new JasminBuilder(symbolTable);
      String jasmin = jBuilder.printJasmin(root);
      jasmin = jasmin.concat("\n\nArithmetic \n\n" + jBuilder.arithmeticJasmin(root));
      System.out.println(jasmin);

      System.out.println("---JasminTEST---\n");
      JasminTest jTest = new JasminTest(symbolTable);
       jTest.jasmin_process(root, "", State.BUILD);
    

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
 

}
