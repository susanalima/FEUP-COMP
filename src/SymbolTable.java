
import java.util.HashMap;

public class SymbolTable {
    static final String GLOBAL = "#GLOBAL_SCOPE"; 
    HashMap<String, FunctionBlock> symbolTable; // First key is #fn#Param1Type#Param2Type
    boolean extends_;                           //^- Isto está certo? Não é fn&Param1Type($array)?&Param2Type (?)

    SymbolTable() {
        this.symbolTable = new HashMap<>();
        this.extends_ = false;
    }


    public void setExtends() {
        this.extends_ = true;
    }

    //checks if var is local, global or from a parent class
    boolean varExists(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return true;
        } else if(isVarGlobal(varName))
            return true;
            else return this.extends_;
    }

    String getVarType(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return this.symbolTable.get(funcName).getVarType(varName);
        } else if(isVarGlobal(varName))
            return this.symbolTable.get(SymbolTable.GLOBAL).getVarType(varName);
            else if(this.extends_)
                return "undefined";
            return ""; 
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


    String addFunction(String funcName) {
        if (this.symbolTable.get(funcName) != null) //if funcName is already a function
            return funcName;
        String processed_funcName = funcName;
        String returnType = "void";
        if(!funcName.equals(SymbolTable.GLOBAL)){
            String[] tokens = funcName.split("#");
            processed_funcName =  tokens[2];
            returnType = tokens[1] ;
            for(int i = 3; i < tokens.length; i+=2) {
                processed_funcName += "&" + tokens[i];
            }
        }
        if (this.symbolTable.get(processed_funcName) == null) { //if processed funcname is already a function (should throw error?)
            FunctionBlock fBlock = new FunctionBlock(returnType);
            this.symbolTable.put(processed_funcName, fBlock);
            addParams(funcName, processed_funcName);
        } 
        return processed_funcName;
    }

    String addSymbol(String processed_funcName, String varName, Symbol newSymbol) {
        addFunction(processed_funcName); //in case it is GLOBAL
        FunctionBlock fB = this.symbolTable.get(processed_funcName);
        Symbol toAdd = newSymbol;
        toAdd.setCounter(fB.contents.size() - 1);
        fB.addSymbol(varName, toAdd); 
        return processed_funcName;
    }


    Boolean checkFunctionReturnType(String funcName, String returnType) {
        return this.symbolTable.get(funcName).checkReturnType(returnType);
    }

    String getFunctionReturnType(String funcName) {
        return this.symbolTable.get(funcName).getReturnType();
    }

    int getCounter(String funcName, String varName) {
        Symbol s = this.symbolTable.get(funcName).contents.get(varName);
        if (s == null) {
            System.err.println("Symbol " + varName + " in " + funcName + " not found!");
            return 0;
        }
        return s.getCounter();
    }


    public void printSymbolTable() {
        System.out.println("extends: " + this.extends_);
        symbolTable.forEach((key, value) -> {
            System.out.println(key + " : ");
            value.printFunctionBlock();
        });
    }
}