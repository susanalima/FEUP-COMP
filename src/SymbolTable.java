
import java.util.HashMap;

public class SymbolTable {
    HashMap<String, FunctionBlock> symbolTable; //First key is fn#Param1Type#Param2Type
    static final String GLOBAL = "#GLOBAL_SCOPE";

     SymbolTable() {
        this.symbolTable = new HashMap<>();
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
        return isVarLocal(this.GLOBAL, varName);
    }

    /*Type getVarType(String funcName, String varName) {
       if (isVarGlobal(varName))
            funcName = this.GLOBAL;
        
         FunctionBlock fBlock = this.symbolTable.get(funcName);
         return fBlock.getVarType(varName);    
    }*/

    String getVarType(String funcName, String varName) {
        if (isVarGlobal(varName))
             funcName = this.GLOBAL;
         
          FunctionBlock fBlock = this.symbolTable.get(funcName);
          return fBlock.getVarType(varName);    
     }
 
    boolean methodExists(String funcName) {
        if(this.symbolTable.get(funcName) != null)
            return true;
        else
            return false;
    }

    void addSymbol(String funcName, String varName, Symbol newSymbol) {
        if(this.symbolTable.get(funcName) == null){
            FunctionBlock fBlock = new FunctionBlock();
            this.symbolTable.put(funcName,fBlock);
        } else {
            this.symbolTable.get(funcName).addSymbol(varName, newSymbol); //nao sei se isto e valido
        }
    }
}