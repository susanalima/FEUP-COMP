import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.*;

public class jmm {

  private static SymbolTable symbolTable = new SymbolTable();

  public static void main(String args[]) throws ParseException {
    if (args.length > 2 || args.length < 1) {
      System.out.println("Usage: jmm [-o] <FileToParse>");
      return;
    }
    try {

      Optimization optimization = Optimization.NONE;
      String file = args[0];
      if(args[0].equals("-o")){
        optimization = Optimization.O;
        file = args[1];
      }


      Alpha myCalc = new Alpha(new java.io.FileInputStream(file));

      SimpleNode root = Alpha.Program();
      System.out.println("\n\n---AST---\n\n");
      root.dump("");

      symbolTable.buildAndAnalise(root);

    
      JasminTest jTest = new JasminTest(symbolTable, optimization);
      jTest.process(root, "", SymbolTable.GLOBAL, State.BUILD, "int");
      System.out.println("\n\n---JasminTEST---\n");
     // System.out.println(jTest.finalCode);

      PrintWriter out = null;
      try {
        out = new PrintWriter("../out/output.j");
        out.println(jTest.finalCode);
      } finally {
        if (out != null)
          out.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
