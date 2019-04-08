package parser;

import exp.*;

import java.util.HashMap;

import Type;

public class FunctionBlock {
    HashMap<String, Symbol> contents;  //Key is varName

    public FunctionBlock() {
        this.contents = new HashMap<>();
    }

    void addSymbol(Symbol content) {
        contents.put(content.name, content);
    }

    boolean varExists(String varName) {
        return contents.containsKey(varName);
    }

    Type getVarType(String varName) {
        return contents.get(varName).type;
    }
}