
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {


    private static String CARDINAL_SEPARATOR = "#";
    private static String AND_SEPARATOR = "&";
    private static String ARRAY_SEPARATOR = "$";
    private static String UNDEFINED_TYPE = "undefined";

    static final String GLOBAL = "#GLOBAL_SCOPE";
    HashMap<String, FunctionBlock> symbolTable; // First key is  fn&Param1Type($array)?&Param2Type 
    boolean extends_;
    String className;

    SymbolTable() {
        this.symbolTable = new HashMap<>();
        this.extends_ = false;
    }

    public void setExtends() {
        this.extends_ = true;
    }

    // checks if var is local, global or from a parent class
    boolean varExists(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return true;
        } else if (isVarGlobal(varName))
            return true;
        else
            return this.extends_;
    }

    String getVarType(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return this.symbolTable.get(funcName).getVarType(varName);
        } else if (isVarGlobal(varName))
            return this.symbolTable.get(SymbolTable.GLOBAL).getVarType(varName);
        else if (this.extends_)
            return UNDEFINED_TYPE;
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

    String methodExistsWithUndefinedValues(String funcName) {
        if (this.symbolTable.get(funcName) != null)
            return funcName;
        else
            return checkMethod(funcName);
    }

    String checkMethod(String funcName) {
        String[] expected_functionBlock = funcName.split(AND_SEPARATOR);
        String[] key_functionBlock;
        int count_func = 0;
        String return_key = "";
        for (String key : symbolTable.keySet()) {
            key_functionBlock = key.split(AND_SEPARATOR);
            if (key_functionBlock.length != expected_functionBlock.length
                    || !key_functionBlock[0].equals(expected_functionBlock[0]))
                continue;
            int counter = 0;
            for (int i = 0; i < key_functionBlock.length; i++) {
                if (key_functionBlock[i].equals(expected_functionBlock[i])
                        || expected_functionBlock[i].equals(UNDEFINED_TYPE))
                    counter++;
            }
            if (counter == key_functionBlock.length) {
                if(count_func == 0) {
                    return_key = key;
                } else {
                    return_key = "";
                }
            }
            count_func++;
        }
        return return_key;
    }

    void addParams(String funcName, String processed_funcName) {
        String[] tokens = funcName.split(CARDINAL_SEPARATOR);
        for (int i = 3; i < tokens.length - 1; i += 2)
            this.symbolTable.get(processed_funcName).addSymbol(tokens[i + 1],
                    new Var(tokens[i], tokens[i + 1], "param"));
    }

    String processFunction(String funcName) {
        String processed_funcName = funcName;
        if (!funcName.equals(SymbolTable.GLOBAL)) {
            String[] tokens = funcName.split(AND_SEPARATOR);
            processed_funcName = tokens[0];
            for (int i = 1; i < tokens.length; i += 2) {
                processed_funcName += AND_SEPARATOR + tokens[i];
            }
        }
        return processed_funcName;
    }

    String processFunction(String funcName, boolean add) {
        if (this.symbolTable.get(funcName) != null) // if funcName is already a function
            return funcName;
        String processed_funcName = funcName;
        String returnType = "void";
        if (!funcName.equals(SymbolTable.GLOBAL)) {
            String[] tokens = funcName.split(CARDINAL_SEPARATOR);
            processed_funcName = tokens[2];
            returnType = tokens[1];
            for (int i = 3; i < tokens.length; i += 2) {
                processed_funcName += AND_SEPARATOR + tokens[i];
            }
        }
        if (this.symbolTable.get(processed_funcName) == null && add) { // if processed funcname is already a function (should
                                                                // throw error?)
            FunctionBlock fBlock = new FunctionBlock(returnType);
            this.symbolTable.put(processed_funcName, fBlock);
            addParams(funcName, processed_funcName);
        }
        return processed_funcName;
    }

    boolean addSymbol(String processed_funcName, String varName, Symbol newSymbol) {
        processFunction(processed_funcName, true); // in case it is GLOBAL
        FunctionBlock fB = this.symbolTable.get(processed_funcName);
        Symbol toAdd = newSymbol;
        toAdd.setCounter(fB.contents.size() + 1);
        return fB.addSymbol(varName, toAdd);
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

    String getClassName() {
        return this.className;
    }

    void setClassName(String className) {
        this.className = className;
    }

    public void printSymbolTable() {
        System.out.println("Class name: " + this.className);
        System.out.println("extends: " + this.extends_);
        symbolTable.forEach((key, value) -> {
            System.out.println(key + " : ");
            value.printFunctionBlock();
        });
    }




    //build symbol table
    public String eval_build(SimpleNode node, String symbol, String funcname, State state) {

        switch (node.getId()) {

        case AlphaTreeConstants.JJTPROGRAM:
            symbol = evalNodeProgram_build(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTARGS:
        case AlphaTreeConstants.JJTARG:
        case AlphaTreeConstants.JJTCLASSBODY:
            symbol = evalNodeClassBody(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTIDENTIFIER:
            symbol = evalNodeIdentifier(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTEXTENDS:
            setExtends();
            break;
        case AlphaTreeConstants.JJTINT:
            symbol = CARDINAL_SEPARATOR + "int";
            break; 
        case AlphaTreeConstants.JJTSTRING:
        case AlphaTreeConstants.JJTBOOLEAN:
        case AlphaTreeConstants.JJTVOID:
        case AlphaTreeConstants.JJTMAIN:
            symbol = CARDINAL_SEPARATOR + node.toString().toLowerCase();
            break;
        case AlphaTreeConstants.JJTARRAY:
            symbol = ARRAY_SEPARATOR  + "array";
            break;
        case AlphaTreeConstants.JJTVAR_DECLARATION:
            symbol = evalNodeVarDeclaration(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTMAINDECLARATION:
        case AlphaTreeConstants.JJTMETHOD_DECLARATION:
            funcname = evalNodeFuncDeclaration(node, "", funcname, state);
            break;
        default:
            symbol = "";
            evalNodeDefault_build(node, symbol, funcname, state);
            break;
        }
        return symbol;
    }


    public String eval_process(SimpleNode node, String symbol, String funcname, State state) {

        switch (node.getId()) {
            case AlphaTreeConstants.JJTPROGRAM:
            symbol = evalNodeProgram_process(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTARGS:
        case AlphaTreeConstants.JJTARG:
        case AlphaTreeConstants.JJTCLASSBODY:
            symbol = evalNodeClassBody(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTEXTENDS:
            setExtends();
            break;
        case AlphaTreeConstants.JJTVAR_DECLARATION:
            symbol = "";
            break;
        case AlphaTreeConstants.JJTMAINDECLARATION:
        case AlphaTreeConstants.JJTMETHOD_DECLARATION:
            funcname = evalNodeFuncDeclaration(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTPLUS:
        case AlphaTreeConstants.JJTMINUS:
        case AlphaTreeConstants.JJTPRODUCT:
        case AlphaTreeConstants.JJTDIVISION:
        case AlphaTreeConstants.JJTMINOR:
        case AlphaTreeConstants.JJTAND:
        case AlphaTreeConstants.JJTFUNC_ARGS:
            symbol = evalNodeFuncArgs(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTIDENTIFIER:
            symbol = evalNodeIdentifier(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTLENGTH:
        case AlphaTreeConstants.JJTINTEGER: // se estes tiverem um val pode se juntar tudo numa so condiçao
            symbol = AND_SEPARATOR + "int";
            break;
        case AlphaTreeConstants.JJTTRUE: 
        case AlphaTreeConstants.JJTFALSE:
            symbol = AND_SEPARATOR + "boolean";
            break;
        case AlphaTreeConstants.JJTINT:
            symbol = AND_SEPARATOR + "int";
            break; 
        case AlphaTreeConstants.JJTSTRING:
        case AlphaTreeConstants.JJTBOOLEAN:
            symbol = CARDINAL_SEPARATOR + node.toString().toLowerCase();
            break;
        case AlphaTreeConstants.JJTARRAY:
            symbol = ARRAY_SEPARATOR  + "array";
            break;
        case AlphaTreeConstants.JJTIF:
        case AlphaTreeConstants.JJTWHILE:
            symbol = evalNodeIfWhile(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTCONDITION:
            symbol = evalNodeCondition(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTINDEX:
            symbol = evalNodeIndex(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTEQUAL: // formato &type1&type2 etc...
            symbol = evalNodeEqual(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTRETURN:
            evalNodeReturn(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTFUNC_ARG:
            symbol = evalNodeFuncArg(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTFUNC:
            symbol = evalNodeFunc(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTNEWFUNC:
            symbol = AND_SEPARATOR  + UNDEFINED_TYPE;
            break;
        case AlphaTreeConstants.JJTDOT:
            symbol = evalNodeDot(node, symbol, funcname, state);
            break;
        default:
            symbol = "";
            evalNodeDefault_process(node, symbol, funcname, state);
            break;
        }
        return symbol;
    }



    private String evalNodeProgram_build(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        child_node = (SimpleNode) node.jjtGetChild(1);
        setClassName(child_node.val);
        for (int i = 2; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_build(child_node, symbol, funcname, State.BUILD);
        }
        symbol += tmp;
        return symbol;
    }


    private String evalNodeProgram_process(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if(child_node.getId() == AlphaTreeConstants.JJTCLASSBODY) {
                tmp += eval_process(child_node, symbol, funcname, State.PROCESS);
                break;
            }
        }
        symbol += tmp;
        return symbol;
    }


    private void evalNodeReturn(SimpleNode node, String symbol, String funcname, State state) {
        String tmp = eval_process((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS);
        tmp = returnExpressionType(tmp);
        if (!checkFunctionReturnType(funcname,tmp) && !tmp.equals(UNDEFINED_TYPE)) {
            System.out.println("Invalid return type");
            System.exit(1);
        }
    }


    private String evalNodeClassBody(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        if (node.getId() == AlphaTreeConstants.JJTCLASSBODY)
            symbol = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if(state == State.BUILD)
            tmp += eval_build(child_node, symbol, funcname, state);
            else 
            tmp += eval_process(child_node, symbol, funcname, state);

        }
        symbol += tmp;
        return symbol;
    }

    private String evalNodeFuncArgs(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_process(child_node, symbol, funcname, state);
        }
        symbol = tmp;
        
        if (node.getId() == AlphaTreeConstants.JJTMINOR) {
            if(returnExpressionType(symbol).equals("")){
                System.out.println("Invalid Condition!");
                System.exit(0);
            }
            symbol = AND_SEPARATOR + "boolean";
        }
           
        return symbol;
    }

    private String evalNodeIdentifier(SimpleNode node, String symbol, String funcname, State state) {        
        if (state == State.BUILD) // if it is building state the symbol must be the name of the variable
            symbol = CARDINAL_SEPARATOR + node.val;
        else if (state == State.PROCESS) { // if it is processing state the variable must be validated and and symbol is
                                           // it's type
            symbol = getVarType(funcname, node.val);
            if (symbol.equals("")) {// if the variable was not declared aborts the program
                System.out.println("Variable not declared: " + node.val);
                System.exit(0);
            }
            if(node.val.equals(getClassName())){
                System.out.println("Invalid var : " + node.val);
                 System.exit(0);
             }
            symbol = AND_SEPARATOR + symbol;
        }
        return symbol;
    }

    private String evalNodeIndex(SimpleNode node, String symbol, String funcname, State state) {
        symbol = eval_process((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS); // validates the identifier
        String index = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.PROCESS); // validates the index
        if (!symbol.contains( ARRAY_SEPARATOR + "array") || !evaluateExpressionInt(index)) // case the variable was declared but is not an
                                                                         // array or the index is not and int
        {
            System.out.println("Variable not an array or index not an integer");
            System.exit(0);
        }
        symbol = symbol.split("\\" + ARRAY_SEPARATOR)[0];
        return symbol;
    }

    private String evalNodeIfWhile(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, symbol, funcname, state);
        }
        return symbol;
    }

    private String evalNodeCondition(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        tmp = returnExpressionType(symbol);
        if (!tmp.equals("boolean") && !tmp.equals(UNDEFINED_TYPE)) {
            System.out.println("Invalid Condition!");
            System.exit(0);
        }
        return symbol;
    }

    private String evalNodeEqual(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
        SimpleNode child_node;
        String tmp = "";
        String varType = eval_process(identifier, symbol, funcname, State.PROCESS);
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        symbol += tmp;
        /*System.out.println("vt: " + varType);
        System.out.println("sy " + symbol);*/
        if (!evaluateExpressionType(varType, symbol)) {
            System.out.println("Invalid type");
            System.exit(0);
        }
        return symbol;
    }

    private String evalNodeVarDeclaration(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER && i != 0) { // this means the type is done and
                                                                                    // the
                                                                                    // identifier is next
           
                if(child_node.val.equals(getClassName())){
                    System.out.println("Invalid var declaration : " + child_node.val);
                     System.exit(0);
                 }

                symbol += tmp;                                                                    
                String value = "local";
                if (funcname.equals(SymbolTable.GLOBAL))
                    value = "global";
                if(!addSymbol(funcname, child_node.val, new Var(symbol.split(CARDINAL_SEPARATOR)[1], child_node.val, value))) {
                   System.out.println("Duplicated var declaration : " + child_node.val);
                    System.exit(0);
                }
            }
            tmp += eval_build(child_node, symbol, funcname, State.BUILD);
        }
        return symbol;
    }

    public String evalNodeFuncDeclaration(SimpleNode node, String symbol, String funcname, State state) {        
        SimpleNode child_node;
        String tmp = "";
        State currState = State.BUILD;
        boolean add = true;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (child_node.getId() == AlphaTreeConstants.JJTBODY) { 
                
                if(state == State.PROCESS) {
                    add = false;
                    currState = state;
                }
                symbol += tmp;
                funcname = processFunction(symbol, add);
              
            }

            if(currState == State.BUILD)
                tmp += eval_build(child_node, symbol, funcname, currState);
            else if(currState == State.PROCESS)
                tmp += eval_process(child_node, symbol, funcname, currState);
        }
        return funcname;
    }





    private String evalNodeFunc(SimpleNode node, String symbol, String funcname, State state) {
        symbol = ((SimpleNode) node.jjtGetChild(0)).val;
        SimpleNode child_node;
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol += eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        symbol = methodExistsWithUndefinedValues(symbol);
        if(symbol.equals(""))
            symbol = AND_SEPARATOR + UNDEFINED_TYPE;
        return symbol;
    }

    private String evalNodeFuncArg(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, symbol, funcname, State.PROCESS);
            if (symbol.equals(""))
                continue;
            symbol = AND_SEPARATOR + returnExpressionType(symbol);
            if (symbol.equals(AND_SEPARATOR)) {
                System.out.println("Invalid type ");
                System.exit(0);
            }
        }
        return symbol;
    }

    private String evalNodeDot(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
        String tmp = "";
        tmp = getVarType(funcname, child_node.val);
        if (child_node.getId() == AlphaTreeConstants.JJTTHIS || tmp.equals(getClassName())) { // if first child is THIS
                                                                                              // eval
                                                                                              // function
            symbol = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, state);
            //System.out.println("symbol1 : " + symbol);
            tmp = methodExistsWithUndefinedValues(symbol);
            if (tmp.equals("")) { // se a funçao com aqueles argumentos nao existir
                System.out.println("Invalid function");
                System.exit(0);
            } else {
                symbol = AND_SEPARATOR + getFunctionReturnType(tmp);
            }
        } else if (tmp.equals(UNDEFINED_TYPE)) {
            symbol = AND_SEPARATOR + UNDEFINED_TYPE;
        } else if (child_node.getId() == AlphaTreeConstants.JJTNEWFUNC) {
            child_node = (SimpleNode) child_node.jjtGetChild(1);
            if(child_node.getId() == AlphaTreeConstants.JJTFUNC) {
              child_node = (SimpleNode) child_node.jjtGetChild(0);
              if (child_node.val.equals(getClassName())) {
                symbol = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, state);
                tmp = methodExistsWithUndefinedValues(symbol);
                if (tmp.equals("")) { // se a funçao com aqueles argumentos nao existir
                    System.out.println("Invalid function");
                    System.exit(0);
                } else {
                    symbol = AND_SEPARATOR + getFunctionReturnType(tmp);
                }
             }
           }
         } else {
            for (int i = 1; i < node.jjtGetNumChildren(); i++) {  // i= 0 no caso de de se ter de analisar as variaveis antes do dot
                child_node = (SimpleNode) node.jjtGetChild(i);
                symbol = eval_process(child_node, symbol, funcname, state);
            }
            //System.out.println("symbol2 : " + symbol);
           // symbol = AND_SEPARATOR + UNDEFINED_TYPE;
        }
        return symbol;
    }

    private String evalNodeDefault_build(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            eval_build(child_node, symbol, funcname, State.BUILD);
        }
        return symbol;
    }


    private String evalNodeDefault_process(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        return symbol;
    }

    private String returnExpressionType(String expression) {
        if (!expression.substring(0, 1).equals(AND_SEPARATOR)) // if it is an outside function
            return UNDEFINED_TYPE;
        String expectedType = expression.split(AND_SEPARATOR)[1];
        if (evaluateExpressionType(AND_SEPARATOR + expectedType, expression))
            return expectedType;
        else
            return "";
    }

    private boolean evaluateExpressionType(String expectedType, String expression) {
        String[] tokens = expression.split(AND_SEPARATOR);
        String processed_expectedType = expectedType.split(AND_SEPARATOR)[1];
        if (processed_expectedType.equals(UNDEFINED_TYPE))
            return true;
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals(UNDEFINED_TYPE))
                continue;
            if (!processed_expectedType.equals(tokens[i]))
                return false;
        }
        return true;
    }

    private boolean evaluateExpressionInt(String expression) {
        return evaluateExpressionType(AND_SEPARATOR + "int", expression);
    }

}