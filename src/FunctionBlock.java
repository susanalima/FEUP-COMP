import java.util.HashMap;

public class FunctionBlock {
    HashMap<String, Symbol> contents;  //Key is varName
    String returnType; 

    public FunctionBlock() {
        this.contents = new HashMap<>();
        this.returnType = "void";
    }

    public FunctionBlock(String returnType) {
        this.contents = new HashMap<>();
        this.returnType = returnType;
    }

    void addSymbol(Symbol content) {
        contents.put(content.name, content);
    }

    boolean varExists(String varName) {
        return contents.containsKey(varName);
    }

    /*Type getVarType(String varName) {
        return contents.get(varName).type;
    }*/

    String getVarType(String varName) {
        return contents.get(varName).type;
    }

    String getReturnType() {
        return this.returnType;
    }

    void addSymbol(String varName, Symbol newSymbol) {
        this.contents.put(varName,newSymbol);
    }

  
    public void printFunctionBlock() {
        System.out.println(this.returnType);
        contents.forEach((key, value) -> System.out.println(key + " : " + value));
    }
  
}