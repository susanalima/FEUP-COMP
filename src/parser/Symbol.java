package parser;

import exp.*;
import parser.Type;

public abstract class Symbol { 
    Type type;
    String name;

    Symbol(Type type_, String name_) {
        this.type = type_;
        this.name = name_;
    }
}