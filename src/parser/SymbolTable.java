package parser;

import exp.*;
import Type;

public class SymbolTable {
    HashMap<String, FunctionBlock> symbolTable; //First key is fn#Param1Type#Param2Type
    static final String GLOBAL = "#GLOBAL_SCOPE";

    boolean varExists(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return true;
        } else {
            return isVarGlobal(varName);
        }
    }

    boolean isVarLocal(String funcName, String var) {
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
    Type getVarType(String funcName, String var) {
    }
    boolean methodExists(String funcName);
    void addSymbol(String funcName, Symbol newSymbol);
}