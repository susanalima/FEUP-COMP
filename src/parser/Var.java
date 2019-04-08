package parser;

import exp.*;
import parser.Type;

public class Var extends Symbol {
    String value;

    Var(Type returnType, String name, String value_) {
        super(returnType, name);
        this.value = value_;
    }

}