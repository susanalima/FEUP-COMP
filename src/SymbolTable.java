
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    static final String CARDINAL_SEPARATOR = "#";
    static final String AND_SEPARATOR = "&";
    static final String ARRAY_SEPARATOR = "$";
    static final String UNDEFINED_TYPE = "@";
    static final String GLOBAL = "#GLOBAL_SCOPE";

    HashMap<String, FunctionBlock> symbolTable; // First key is fn&Param1Type($array)?&Param2Type
    String className;
    boolean extends_;
    String parentClass;
    Optimization optimization;

    SymbolTable() {
        this.symbolTable = new HashMap<>();
        this.extends_ = false;
        this.parentClass = "java/lang/Object";
        this.optimization = Optimization.O;
    }

    public void setExtends(SimpleNode node) {
        this.extends_ = true;
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
        this.parentClass = child_node.val;
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

    boolean wasVarDeclared(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return true;
        } else
            return isVarGlobal(varName);
    }

    int getLimitLocals(String funcName) {
        return this.symbolTable.get(funcName).getLimitLocals();
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
        int count_func = 0, counter = 0;
        String return_key = "";
        for (String key : symbolTable.keySet()) {
            key_functionBlock = key.split(AND_SEPARATOR);
            if (key_functionBlock.length != expected_functionBlock.length
                    || !key_functionBlock[0].equals(expected_functionBlock[0]))
                continue;
            counter = 0;
            for (int i = 0; i < key_functionBlock.length; i++) {

                if (key_functionBlock[i].equals(expected_functionBlock[i])
                        || checkUndefinedType(expected_functionBlock[i]))
                    counter++;
                else {
                    count_func--;
                    break;
                }
            }
            if (counter == key_functionBlock.length) {
                if (count_func == 0) {
                    return_key = key;
                } else {
                    return_key = "";
                }
            }
            count_func++;
        }
        return return_key;
    }

    public static boolean checkUndefinedType(String expression) {
        if (expression.equals(""))
            return false;
        return expression.substring(0, 1).equals(UNDEFINED_TYPE);
    }

    void addParams(String funcName, String processed_funcName) {
        String[] tokens = funcName.split(CARDINAL_SEPARATOR);
        int counter;
        Symbol toAdd;
        for (int i = 3; i < tokens.length - 1; i += 2) {
            toAdd = new Symbol(tokens[i], tokens[i + 1], "param");

            counter = this.symbolTable.get(processed_funcName).contents.size() + 1;
            toAdd.setCounter(counter);

            this.symbolTable.get(processed_funcName).addSymbol(tokens[i + 1], toAdd);
        }

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
        if (this.symbolTable.get(processed_funcName) == null && add) { // if processed funcname is already a function
                                                                       // (should
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
        int counter = fB.contents.size() + 1;
        toAdd.setCounter(counter);
        return fB.addSymbol(varName, toAdd);
    }

    Boolean checkFunctionReturnType(String funcName, String returnType) {
        return this.symbolTable.get(funcName).checkReturnType(returnType);
    }

    String getFunctionReturnType(String funcName) { 
        return this.symbolTable.get(funcName).getReturnType();
    }

    int getCounter(String funcName, String varName) {
        Symbol s = null;
        if (isVarLocal(funcName, varName)) {
            s = this.symbolTable.get(funcName).contents.get(varName);
        } else if (isVarGlobal(varName))
            s = this.symbolTable.get(SymbolTable.GLOBAL).contents.get(varName);

        if (s == null) {
            System.err.println("Symbol " + varName + " in " + funcName + " not found!");
            return 0;
        }
        return s.getCounter();
    }

    String getClassName() {
        return this.className;
    }

    String getParentClass() {
        return this.parentClass;
    }

    void setClassName(String className) {
        this.className = className;
    }

    void setGlobalSymbolType(String varName, String varType) {
        this.symbolTable.get(GLOBAL).setSymbolType(varName, varType);
    }

    void setSymbolSize(String funcName, String varName, int size) {
        if (isVarLocal(funcName, varName)) {
            this.symbolTable.get(funcName).setVarSize(varName, size);
        } else
            this.symbolTable.get(GLOBAL).setVarSize(varName, size);
    }

    int getSymbolSize(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return this.symbolTable.get(funcName).getVarSize(varName);
        } else
            return this.symbolTable.get(GLOBAL).getVarSize(varName);
    }


    void setSymbolConstValue(String funcName, String varName, String value) {

        if (isVarLocal(funcName, varName)) {
            this.symbolTable.get(funcName).setVarConstValue(varName, value);
        } else
            this.symbolTable.get(GLOBAL).setVarConstValue(varName, value);
    }

    String getSymbolConstValue(String funcName, String varName) {
        if (isVarLocal(funcName, varName)) {
            return this.symbolTable.get(funcName).getVarConstValue(varName);
        } else
            return this.symbolTable.get(GLOBAL).getVarConstValue(varName);
    }


    public void printSymbolTable() {
        System.out.println("\n\n---SYMBOL TABLE---\n\n");
        System.out.println("Class name: " + this.className);
        System.out.println("extends: " + this.parentClass);
        symbolTable.forEach((key, value) -> {
            System.out.println(key + " : ");
            value.printFunctionBlock();
        });
    }

    public void buildAndAnalise(SimpleNode root) {
        eval_build(root, "", GLOBAL, State.BUILD);
        eval_process(root, "", GLOBAL, State.PROCESS);
        printSymbolTable();
    }

    // build symbol table
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
            setExtends(node);
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
            symbol = ARRAY_SEPARATOR + "array";
            break;
        case AlphaTreeConstants.JJTVAR_DECLARATION:
            symbol = evalNodeVarDeclaration(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTMAINDECLARATION:
        case AlphaTreeConstants.JJTMETHOD_DECLARATION:
            funcname = evalNodeFuncDeclaration(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTEQUAL:
            evalNodeEqual_build(node, funcname);
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
            symbol = evalNodeOperator(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTFUNC_ARGS:
            symbol = evalNodeFuncArgs(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTIDENTIFIER:
            symbol = evalNodeIdentifier(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTINTEGER:
            symbol = AND_SEPARATOR + "int";
            break;
        case AlphaTreeConstants.JJTLENGTH:
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
            symbol = ARRAY_SEPARATOR + "array";
            break;
        case AlphaTreeConstants.JJTIF:
        case AlphaTreeConstants.JJTWHILE:
            symbol = evalNodeIfWhile(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTCONDITION:
            symbol = evalNodeCondition(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTNOT:
            symbol = evalNodeNot(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTINDEX:
            symbol = evalNodeIndex(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTEQUAL: // formato &type1&type2 etc...
            symbol = evalNodeEqual_process(node, symbol, funcname, state);
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
            symbol = evalNodeNewFunc(node, symbol, funcname, state);
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

    public String evalNodeProgram_build(SimpleNode node, String symbol, String funcname, State state) {
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

    public String evalNodeProgram_process(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (child_node.getId() == AlphaTreeConstants.JJTCLASSBODY) {
                tmp += eval_process(child_node, symbol, funcname, State.PROCESS);
                break;
            }
        }
        symbol += tmp;
        return symbol;
    }

    public void evalNodeReturn(SimpleNode node, String symbol, String funcname, State state) {
        String tmp = eval_process((SimpleNode) node.jjtGetChild(0), getFunctionReturnType(funcname), funcname,
                State.PROCESS);
        tmp = returnExpressionType(tmp);
        if (!checkFunctionReturnType(funcname, tmp) && !checkUndefinedType(tmp)) {
            System.out.println("Invalid return type");
            System.exit(1);
        }
    }

    public String evalNodeClassBody(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        if (node.getId() == AlphaTreeConstants.JJTCLASSBODY)
            symbol = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (state == State.BUILD)
                tmp += eval_build(child_node, symbol, funcname, state);
            else
                tmp += eval_process(child_node, symbol, funcname, state);

        }
        symbol += tmp;
        return symbol;
    }

    public String evalNodeFuncArgs(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_process(child_node, symbol, funcname, state);
        }
        symbol = tmp;
        return symbol;
    }

    public String evalNodeOperator(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "", varType = "int";
        if (node.getId() == AlphaTreeConstants.JJTAND)
            varType = "boolean";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_process(child_node, varType, funcname, state);
        }
        symbol = tmp;

        if (node.getId() != AlphaTreeConstants.JJTAND && !evaluateExpressionInt(symbol)) {
            System.out.println("Invalid Operation!");
            System.exit(0);
        }

        if (node.getId() == AlphaTreeConstants.JJTMINOR) {
            if (returnExpressionType(symbol).equals("")) {
                System.out.println("Invalid Condition!");
                System.exit(0);
            }
            symbol = AND_SEPARATOR + "boolean";
        }

        return symbol;
    }

    public String evalNodeIdentifier(SimpleNode node, String symbol, String funcname, State state) {
        if (state == State.BUILD) {// if it is building state the symbol must be the name of the variable
            symbol = CARDINAL_SEPARATOR + node.val;
        } else if (state == State.PROCESS) { // if it is processing state the variable must be validated and and symbol
                                             // is
                                             // it's type
            if (this.extends_) {
                if (!wasVarDeclared(funcname, node.val))
                    addSymbol(SymbolTable.GLOBAL, node.val, new Symbol(symbol, node.val, "global"));
                else if (getVarType(funcname, node.val).equals(UNDEFINED_TYPE)) {
                    if (!symbol.equals(""))
                        setGlobalSymbolType(node.val, symbol);
                }
            }
            symbol = getVarType(funcname, node.val);

            if (symbol.equals(UNDEFINED_TYPE)) {
                symbol = UNDEFINED_TYPE + node.val;
            }

            if (symbol.equals("")) {// if the variable was not declared aborts the program
                System.out.println("Variable not declared: " + node.val);
                System.exit(0);
            }
            if (node.val.equals(getClassName())) {
                System.out.println("Invalid var : " + node.val);
                System.exit(0);
            }

            symbol = AND_SEPARATOR + symbol;
        }
        return symbol;
    }

    public String evalNodeIndex(SimpleNode node, String symbol, String funcname, State state) {

        SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);

        String varname = child_node.val;

        boolean skip = false;

        if (this.extends_) {
            if (!wasVarDeclared(funcname, varname))
                addSymbol(SymbolTable.GLOBAL, varname, new Symbol("int$array", varname, "global"));
            else if (getVarType(funcname, varname).equals(UNDEFINED_TYPE)) {
                setGlobalSymbolType(varname, "int$array");
            }
        }

        symbol = eval_process(child_node, symbol, funcname, State.PROCESS); // validates the identifier

        child_node = (SimpleNode) node.jjtGetChild(1);
        String index = eval_process((SimpleNode) child_node, symbol, funcname, State.PROCESS); // validates the index

        if (!symbol.contains(ARRAY_SEPARATOR + "array") && !skip) {
            System.out.println("Variable not an array");
            System.exit(0);
        }

        if (!evaluateExpressionInt(index)) {
            System.out.println("Index not an integer");
            System.exit(0);
        }

        int size = getSymbolSize(funcname, varname);
        try {
            if (size != -1 && Integer.parseInt(child_node.val) >= size) {
                System.out.println("Index out of bounds");
                System.exit(0);
            }
        } catch(NumberFormatException e) {

        }
       

        symbol = symbol.split("\\" + ARRAY_SEPARATOR)[0];
        return symbol;
    }

    public String evalNodeIfWhile(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, symbol, funcname, state);
        }
        return symbol;
    }

    public String evalNodeCondition(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, "boolean", funcname, State.PROCESS);
        }
        tmp = returnExpressionType(symbol);
        if (!tmp.equals("boolean") && !checkUndefinedType(tmp)) {
            System.out.println("Invalid Condition!");
            System.exit(0);
        }
        return symbol;
    }

    private String evalNodeNot(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
        symbol = eval_process(child_node, "boolean", funcname, state);
        if(!this.evaluateExpressionType("&boolean", symbol)) {
            System.out.println("Invalid use of not");
            System.exit(0);
        }
        return symbol;
    }

    public String evalNodeEqual_process(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode identifier = (SimpleNode) node.jjtGetChild(0), child_node;
        String tmp = "", varType = AND_SEPARATOR + "int";
        boolean process_identifier = true;

        if (wasVarDeclared(funcname, identifier.val)) {
            varType = eval_process(identifier, "", funcname, State.PROCESS);
            process_identifier = false;
        }
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval_process(child_node, varType.split(AND_SEPARATOR)[1], funcname, State.PROCESS);
        }
        symbol += tmp;

        String expressionType = returnExpressionType(symbol);

        if (process_identifier)
            varType = eval_process(identifier, expressionType, funcname, State.PROCESS);

        if (expressionType.equals("void")) {
            System.out.println("Invalid assigment");
            System.exit(0);
        }

        if (!evaluateExpressionType(varType, AND_SEPARATOR + expressionType)) {
            if (!(this.extends_ && varType.equals(AND_SEPARATOR + this.parentClass) && expressionType.equals(this.className) )) {
                System.out.println("Invalid assigment");
                System.exit(0);
            }
        }

        if (isVarGlobal(identifier.val) && getVarType(GLOBAL, identifier.val).equals(UNDEFINED_TYPE)) {
            setGlobalSymbolType(identifier.val, expressionType);
        }

        return symbol;
    }

    public String evalNodeVarDeclaration(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER && i != 0) { 

                if (child_node.val.equals(getClassName())) {
                    System.out.println("Invalid var declaration : " + child_node.val);
                    System.exit(0);
                }

                symbol += tmp;
                String value = "local";
                if (funcname.equals(SymbolTable.GLOBAL))
                    value = "global";
                if (!addSymbol(funcname, child_node.val,
                        new Symbol(symbol.split(CARDINAL_SEPARATOR)[1], child_node.val, value))) {
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

                if (state == State.PROCESS) {
                    add = false;
                    currState = state;
                }
                symbol += tmp;
                funcname = processFunction(symbol, add);
            }

            if (currState == State.BUILD)
                tmp += eval_build(child_node, symbol, funcname, currState);
            else if (currState == State.PROCESS)
                tmp += eval_process(child_node, symbol, funcname, currState);
        }
        return funcname;
    }

    public void evalNodeEqual_build(SimpleNode node, String funcname) {
        SimpleNode grand_child_node, child_node = (SimpleNode) node.jjtGetChild(0); // identifier
        String varname = child_node.val;
        int id = child_node.getId();
        child_node = (SimpleNode) node.jjtGetChild(1); //right child

        String cValue = Symbol.UNDEFINED_CVALUE;
        int child_id = child_node.getId();
        if (child_id == AlphaTreeConstants.JJTINTEGER) {
            cValue = child_node.val;
        } else if(child_id == AlphaTreeConstants.JJTTRUE) {
            cValue = "1";
        } else if(child_id == AlphaTreeConstants.JJTFALSE) {
            cValue = "0";
        } else {
            if (child_id == AlphaTreeConstants.JJTNEWFUNC) { // new func
                grand_child_node = (SimpleNode) child_node.jjtGetChild(1);
                if (grand_child_node.getId() == AlphaTreeConstants.JJTINT) { // se for array
                    grand_child_node = (SimpleNode) child_node.jjtGetChild(2);
                    if (grand_child_node.getId() == AlphaTreeConstants.JJTINTEGER) {
                        setSymbolSize(funcname, varname, Integer.parseInt(grand_child_node.val));
                    }
                }
            }
        }

        if(id == AlphaTreeConstants.JJTIDENTIFIER) {

            SimpleNode parent_node = (SimpleNode) node.jjtGetParent();
            int parent_id = parent_node.getId();
            SimpleNode grandParent_node = (SimpleNode) parent_node.jjtGetParent();
            int grandParent_id = grandParent_node.getId();

            if(parent_id == AlphaTreeConstants.JJTBODY && (grandParent_id == AlphaTreeConstants.JJTIF || grandParent_id == AlphaTreeConstants.JJTELSE))
                cValue = Symbol.UNDEFINED_CVALUE;

            
            setSymbolConstValue(funcname, varname, cValue);
        }
    }

    private void setUndefinedArgsType(String expression_undefined, String expression) {

        String[] undefined_tokens = expression_undefined.split(AND_SEPARATOR);
        String[] expression_tokens = expression.split(AND_SEPARATOR);
        String varName, varType;

        for (int i = 1; i < undefined_tokens.length; i++) {

            if (!undefined_tokens[i].equals(expression_tokens[i]) && !undefined_tokens[i].equals(UNDEFINED_TYPE)) {
                varName = undefined_tokens[i].split(UNDEFINED_TYPE)[1];
                varType = expression_tokens[i];
                setGlobalSymbolType(varName, varType);
            }
        }

    }

    public String evalNodeFunc(SimpleNode node, String symbol, String funcname, State state) {
        symbol = ((SimpleNode) node.jjtGetChild(0)).val;
        SimpleNode child_node;
        String new_symbol;
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol += eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        if (state == State.PROCESS) {
            new_symbol = methodExistsWithUndefinedValues(symbol);
            if (new_symbol.equals("")) {
                new_symbol = AND_SEPARATOR + UNDEFINED_TYPE;
                return new_symbol;
            }
            if (!new_symbol.equals(symbol)) {
                setUndefinedArgsType(symbol, new_symbol);
            }
        } else
            new_symbol = symbol;

        return new_symbol;
    }

    public String evalNodeNewFunc(SimpleNode node, String symbol, String funcname, State state) {

        symbol = AND_SEPARATOR + UNDEFINED_TYPE;
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(1);
        if (child_node.getId() == AlphaTreeConstants.JJTFUNC) { // se for func significa que é um contrutor e que 
                                                                // etorna uma classe
            child_node = (SimpleNode) child_node.jjtGetChild(0);
            symbol = AND_SEPARATOR + child_node.val;
        } else if (child_node.getId() == AlphaTreeConstants.JJTINT) { // se for int significa que é um array de 
                                                                      // nteiros pelo que retorna um int$array
            child_node = (SimpleNode) node.jjtGetChild(2);
            symbol = eval_process(child_node, symbol, funcname, State.PROCESS);
            if (!evaluateExpressionInt(symbol)) {
                System.out.println("Invalid array index!");
                System.exit(0);
            }
            symbol = AND_SEPARATOR + "int$array";
        }

        return symbol;
    }

    public String evalNodeFuncArg(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval_process(child_node, UNDEFINED_TYPE, funcname, State.PROCESS);
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

    public String evalNodeDot(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
        String tmp = "";
        tmp = getVarType(funcname, child_node.val);

        if (child_node.getId() == AlphaTreeConstants.JJTTHIS || tmp.equals(getClassName())) {

            symbol = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, state);

            if (checkUndefinedType(symbol))
                symbol = symbol.split(UNDEFINED_TYPE)[0];

            tmp = methodExistsWithUndefinedValues(symbol);

            if (tmp.equals("") && !this.extends_) { // se a funçao com aqueles argumentos nao existir
                System.out.println("Invalid function1");
                System.exit(0);
            } else {
                if(this.extends_)
                    symbol = "int"; //TODO SEI LA....
                else
                    symbol = AND_SEPARATOR + getFunctionReturnType(tmp);
            }
            /*
             * } else if (checkUndefinedType(tmp)) { symbol = AND_SEPARATOR +
             * UNDEFINED_TYPE;
             */ // TODO TEST THIS, CANNOT REMEMBER WHY THIS WAS WERE
        } else if (child_node.getId() == AlphaTreeConstants.JJTNEWFUNC) {
            child_node = (SimpleNode) child_node.jjtGetChild(1);
            if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
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
                } else {
                    symbol = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.BUILD);
                }
            } else {
                return eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.BUILD);
            }
        } else if (child_node.val.equals("io")) { 

            symbol = eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.BUILD);

            if (checkUndefinedType(symbol))
                symbol = symbol.split(UNDEFINED_TYPE)[0];

            tmp = IoFunctions.getInstance().methodExistsWithUndefinedValues(symbol);

            if (tmp.equals("")) { // se a funçao com aqueles argumentos nao existir
                System.out.println("Invalid function");
                System.exit(0);
            } else {
                if (!tmp.equals(symbol)) {
                    setUndefinedArgsType(symbol, tmp);
                }
                symbol = AND_SEPARATOR + IoFunctions.getInstance().getFunctionReturnType(tmp);
            }

        } else {
            for (int i = 1; i < node.jjtGetNumChildren(); i++) { // i= 0 no caso de de se ter de analisar as variaveis
                                                                 // antes do dot
                child_node = (SimpleNode) node.jjtGetChild(i);
                symbol = eval_process(child_node, symbol, funcname, State.BUILD);
            }
            // System.out.println("symbol2 : " + symbol);
            // symbol = AND_SEPARATOR + UNDEFINED_TYPE;
        }

        child_node = (SimpleNode) node.jjtGetChild(1);
        String[] tokens = tmp.split("\\" + ARRAY_SEPARATOR);
        if (child_node.getId() == AlphaTreeConstants.JJTLENGTH && tokens.length == 1) {
            System.out.println("Invalid use of length");
            System.exit(0);
        }

        return symbol;
    }

    public String evalNodeDefault_build(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            eval_build(child_node, symbol, funcname, State.BUILD);
        }
        return symbol;
    }

    public String evalNodeDefault_process(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            eval_process(child_node, symbol, funcname, State.PROCESS);
        }
        return symbol;
    }

    public String returnExpressionType(String expression) {
        if (!expression.substring(0, 1).equals(AND_SEPARATOR)) // if it is an outside function
            return UNDEFINED_TYPE;
        String expectedType = expression.split(AND_SEPARATOR)[1];
        if (evaluateExpressionType(AND_SEPARATOR + expectedType, expression))
            return expectedType;
        else
            return "";
    }

    public boolean evaluateExpressionType(String expectedType, String expression) {

        String[] tokens = expression.split(AND_SEPARATOR);
        String processed_expectedType = expectedType.split(AND_SEPARATOR)[1];
        if (processed_expectedType.equals(UNDEFINED_TYPE) || checkUndefinedType(processed_expectedType)) {
            return true;
        }
        for (int i = 1; i < tokens.length; i++) {

            if (tokens[i].equals(UNDEFINED_TYPE))
                continue;
            if (!processed_expectedType.equals(tokens[i]))
                return false;
        }
        return true;
    }

    public boolean evaluateExpressionInt(String expression) {
        return evaluateExpressionType(AND_SEPARATOR + "int", expression);
    }

}