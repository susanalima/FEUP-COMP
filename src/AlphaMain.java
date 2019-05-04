
public class AlphaMain {

  private static SymbolTable symbolTable = new SymbolTable();

  //TODO LIST:
  //1. RESOLVER CENA DO MENOS
  //4. FAZER ALTO REFACTORING NESTE TRETA
  //6. CHANGE istore TO BE ACCORDING WITH THE TYPE
  //8. Cena do aload true e false  -> Na JVM o valor "true" é representado pelo inteiro 1 e o "false" pelo 0.
  //9. Check cena do string&array -> se é [S
  //12. new x[1].funçao -> codigo para isso
  //12. new C().funçao -> codigo para isso
  //13. o getfield e putfield precisam do aload_0 antes???
  //15. CODIGO PARA variavel.length -> arraylength?
  //17. Nao me cheira que isto esteja bem -> ios/pp(LC;;)I


  /* DEPOIS TIRO ISTO DAQUI, AI DE ALGUEM QUE APAGUE
      public void Test(int[] array) {
        System.out.println(new Symbol().toString());

         public void Test(int[]);
    Code:
       0: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
       3: new           #20                 // class Symbol
       6: dup
       7: invokespecial #21                 // Method "<init>":()V
      10: invokevirtual #22                 // Method toString:()Ljava/lang/String;
      13: invokevirtual #23                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      16: return
    }

     public void Test(int[] array) {
        System.out.println(new Symbol().getCounter(1));
    }

    public void Test(int[]);
    Code:
       0: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;       3: new           #20                 // class Symbol
       6: dup
       7: invokespecial #21                 // Method "<init>":()V
      10: iconst_1
      11: invokevirtual #22                 // Method getCounter:(I)I
      14: invokevirtual #23                 // Method java/io/PrintStream.println:(I)V
      17: return}


    public void Test(int[] array) {
        int x = new Symbol().getCounter(1);
    }

     public void Test(int[]);
    Code:
       0: new           #19                 // class Symbol
       3: dup
       4: invokespecial #20                 // Method "<init>":()V
       7: iconst_1
       8: invokevirtual #21                 // Method getCounter:(I)I
      11: istore_2
      12: return
   */

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
