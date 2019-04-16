
import java.util.HashMap;

public class SymbolTable {
    static final String GLOBAL = "#GLOBAL_SCOPE";
    HashMap<String, FunctionBlock> symbolTable; // First key is #fn#Param1Type#Param2Type
    boolean extends_;

    SymbolTable() {
        this.symbolTable = new HashMap<>();
        this.extends_ = false;
    }


    public void setExtends() {
        this.extends_ = true;
    }

    boolean varExists(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return true;
        } else {
            return isVarGlobal(varName);
        }
    }

    boolean isVarLocal(String funcName, String varName) {
        FunctionBlock fBlock = this.symbolTable.get(funcName);
        if (fBlock != null) {
            return fBlock.varExists(varName);
        } else {
            return false;
        }
    }

    boolean isVarGlobal(String varName) {
        return isVarLocal(SymbolTable.GLOBAL, varName);
    }

    /*
     * Type getVarType(String funcName, String varName) { if (isVarGlobal(varName))
     * funcName = this.GLOBAL;
     * 
     * FunctionBlock fBlock = this.symbolTable.get(funcName); return
     * fBlock.getVarType(varName); }
     */

    String getVarType(String funcName, String varName) {
        if (isVarGlobal(varName))
            funcName = SymbolTable.GLOBAL;

        FunctionBlock fBlock = this.symbolTable.get(funcName);
        return fBlock.getVarType(varName);
    }

    boolean methodExists(String funcName) {
        if (this.symbolTable.get(funcName) != null)
            return true;
        else
            return false;
    }

    void addParams(String funcName, String processed_funcName) {
        String[] tokens = funcName.split("#");
        for (int i = 3; i < tokens.length -1; i += 2) 
            this.symbolTable.get(processed_funcName).addSymbol(tokens[i + 1], new Var(tokens[i], tokens[i + 1], "param"));
    }

    String addSymbol(String funcName) {
        String processed_funcName = funcName;
        String returnType = "void";
        if(!funcName.equals(this.GLOBAL)){
            String[] tokens = funcName.split("#");
            processed_funcName = "#" + tokens[2];
            returnType = tokens[1] ;
            for(int i = 3; i < tokens.length; i+=2) {
                processed_funcName += "#" + tokens[i];
            }
        }
        if (this.symbolTable.get(processed_funcName) == null) {
            FunctionBlock fBlock = new FunctionBlock(returnType);
            this.symbolTable.put(processed_funcName, fBlock);
            addParams(funcName, processed_funcName);
        } 
        return processed_funcName;
    }

    void addSymbol(String funcName, String varName, Symbol newSymbol) {
        String processed_funcName = addSymbol(funcName); // else {
        this.symbolTable.get(processed_funcName).addSymbol(varName, newSymbol); // nao sei se isto e valido
        // }
    }

    public void printSymbolTable() {
        System.out.println("extends: " + this.extends_);
        symbolTable.forEach((key, value) -> {
            System.out.println(key + " : ");
            value.printFunctionBlock();
        });
    }
}