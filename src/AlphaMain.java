
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  //TODO LIST:
  //1. RESOLVER CENA DO MENOS
  //2. RESOLVER CENA DO RETURN (nao esta a verificar se as variaveis retornadas sao globais so ver se for local)
  //3. RESOLVER CENA DOS INDEXES DAS GLOBAIS (aparentemente esta resolvido)
  //4. FAZER ALTO REFACTORING NESTE TRETA
  //5. JUNTAR VAR COM SYMBOL PORQUE SAO A MESMA MERDA
  //6. CHANGE istore TO BE ACCORDING WITH THE TYPE
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
      jTest.jasmin_process(root, "", SymbolTable.GLOBAL, State.BUILD, "int");
      System.out.println("\n\n---JasminTEST---\n");
      System.out.println(jTest.code);


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
 

}
