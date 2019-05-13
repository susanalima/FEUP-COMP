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

    String getVarType(String varName) {
        return contents.get(varName).type;
    }


    String getReturnType() {
        return this.returnType;
    }

    Boolean checkReturnType(String returnType) {
        return this.returnType.equals(returnType);
    }

    void setSymbolType(String varName, String varType) {
        this.contents.get(varName).type = varType;
    }

    boolean addSymbol(String varName, Symbol newSymbol) {
        if(this.contents.get(varName) != null)
            return false;
        this.contents.put(varName,newSymbol);
        return true;
    }

    int getVarSize(String varName) {
        return contents.get(varName).size;
    }

    
    void setVarSize(String varName, int size) {
        contents.get(varName).setSize(size);
    }


    public void printFunctionBlock() {
        System.out.println("function return type : " + this.returnType);
        contents.forEach((key, value) -> System.out.println( key + " : " +  value));
    }
  
}