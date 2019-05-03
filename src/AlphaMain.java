
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  //TODO LIST:
  //1. RESOLVER CENA DO MENOS
  //4. FAZER ALTO REFACTORING NESTE TRETA
  //6. CHANGE istore TO BE ACCORDING WITH THE TYPE
  //8. Cena do aload true e false  -> Na JVM o valor "true" é representado pelo inteiro 1 e o "false" pelo 0.
  //9. Check cena do string&array -> se é [S
  //11. x = new C();  -> x tem de ser do tipo C ne??
  //12. new x[1].funçao -> codigo para isso
  //13. o getfield e putfield precisam do aload_0 antes???
  //14. nao esta a fazer analise semantica do NEWFUNC -> isto passa boolean a; x = new int[a]; -> nao devia passar

  public static void main(String args[]) throws ParseException {
    if (args.length != 1) {
      System.out.println("Usage: Alpha <FileToParse>");
    }
    try {
      Alpha myCalc = new Alpha(new java.io.FileInputStream(args[0]));

      SimpleNode root = Alpha.Program();
      System.out.println("\n\n---AST---\n\n");
      root.dump("");

      symbolTable.buildAndAnalise(root);

      /*System.out.println("---Jasmin---\n");
      JasminBuilder jBuilder = new JasminBuilder(symbolTable);
      String jasmin = jBuilder.printJasmin(root);
      jasmin = jasmin.concat("\n\nArithmetic \n\n" + jBuilder.arithmeticJasmin(root));
      System.out.println(jasmin);*/

  
      JasminTest jTest = new JasminTest(symbolTable);
      jTest.process(root, "", SymbolTable.GLOBAL, State.BUILD, "int");
      System.out.println("\n\n---JasminTEST---\n");
      System.out.println(jTest.code);


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
 

}
