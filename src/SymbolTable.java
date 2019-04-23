
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    static final String GLOBAL = "#GLOBAL_SCOPE";
    HashMap<String, FunctionBlock> symbolTable; // First key is #fn#Param1Type#Param2Type
    boolean extends_; // ^- Isto está certo? Não é fn&Param1Type($array)?&Param2Type (?)
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

    String methodExistsWithUndefinedValues(String funcName) {
        if (this.symbolTable.get(funcName) != null)
            return funcName;
        else
            return checkMethod(funcName);
    }

    String checkMethod(String funcName) {
        String[] expected_functionBlock = funcName.split("&");
        String[] key_functionBlock;

        for (String key : symbolTable.keySet()) {
            key_functionBlock = key.split("&");
            if (key_functionBlock.length != expected_functionBlock.length
                    || !key_functionBlock[0].equals(expected_functionBlock[0]))
                continue;
            int counter = 0;
            for (int i = 0; i < key_functionBlock.length; i++) {
                if (key_functionBlock[i].equals(expected_functionBlock[i])
                        || expected_functionBlock[i].equals("undefined"))
                    counter++;
            }
            if (counter == key_functionBlock.length)
                return key;
        }
        return "";
    }

    void addParams(String funcName, String processed_funcName) {
        String[] tokens = funcName.split("#");
        for (int i = 3; i < tokens.length - 1; i += 2)
            this.symbolTable.get(processed_funcName).addSymbol(tokens[i + 1],
                    new Var(tokens[i], tokens[i + 1], "param"));
    }

    String processFunction(String funcName) {
        String processed_funcName = funcName;
        if (!funcName.equals(SymbolTable.GLOBAL)) {
            String[] tokens = funcName.split("&");
            processed_funcName = tokens[0];
            for (int i = 1; i < tokens.length; i += 2) {
                processed_funcName += "&" + tokens[i];
            }
        }
        return processed_funcName;
    }

    String addFunction(String funcName) {
        if (this.symbolTable.get(funcName) != null) // if funcName is already a function
            return funcName;
        String processed_funcName = funcName;
        String returnType = "void";
        if (!funcName.equals(SymbolTable.GLOBAL)) {
            String[] tokens = funcName.split("#");
            processed_funcName = tokens[2];
            returnType = tokens[1];
            for (int i = 3; i < tokens.length; i += 2) {
                processed_funcName += "&" + tokens[i];
            }
        }
        if (this.symbolTable.get(processed_funcName) == null) { // if processed funcname is already a function (should
                                                                // throw error?)
            FunctionBlock fBlock = new FunctionBlock(returnType);
            this.symbolTable.put(processed_funcName, fBlock);
            addParams(funcName, processed_funcName);
        }
        return processed_funcName;
    }

    String addSymbol(String processed_funcName, String varName, Symbol newSymbol) {
        addFunction(processed_funcName); // in case it is GLOBAL
        FunctionBlock fB = this.symbolTable.get(processed_funcName);
        Symbol toAdd = newSymbol;
        toAdd.setCounter(fB.contents.size() + 1);
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

    public String eval(SimpleNode node, String symbol, String funcname, State state) {

        switch (node.getId()) {

        case AlphaTreeConstants.JJTPROGRAM:
            symbol = evalNodeProgram(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTARGS:
        case AlphaTreeConstants.JJTARG:
        case AlphaTreeConstants.JJTCLASSBODY:
            symbol = evalNodeClassBody(node, symbol, funcname, state);
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
        case AlphaTreeConstants.JJTEXTENDS:
            setExtends();
            break;
        case AlphaTreeConstants.JJTLENGTH:
        case AlphaTreeConstants.JJTINTEGER: // se estes tiverem um val pode se juntar tudo numa so condiçao
            symbol = "&int";
            break;
        case AlphaTreeConstants.JJTTRUE: // separar???
        case AlphaTreeConstants.JJTFALSE:
            symbol = "&boolean";
            break;
        case AlphaTreeConstants.JJTINT:
            if (state == State.BUILD)
                symbol = "#int";
            else
                symbol = "&int";
            break;
        case AlphaTreeConstants.JJTSTRING:
            symbol = "#string";
            break;
        case AlphaTreeConstants.JJTARRAY:
            symbol = "$array";
            break;
        case AlphaTreeConstants.JJTBOOLEAN:
            symbol = "#boolean";
            break;
        case AlphaTreeConstants.JJTVOID:
            symbol = "#void";
            break;
        case AlphaTreeConstants.JJTMAIN:
            symbol = "#main";
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
            if (!checkFunctionReturnType(funcname,
                    eval((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS).split("&")[1])) {
                System.out.println("Invalid return type");
                System.exit(1);
            }
            break;
        case AlphaTreeConstants.JJTVAR_DECLARATION:
            symbol = evalNodeVarDeclaration(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTMAINDECLARATION:
        case AlphaTreeConstants.JJTMETHOD_DECLARATION:
            symbol = evalNodeFuncDeclaration(node, "", funcname, state);
            break;
        case AlphaTreeConstants.JJTFUNC_ARG:
            symbol = evalNodeFuncArg(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTFUNC:
            symbol = evalNodeFunc(node, symbol, funcname, state);
            break;
        case AlphaTreeConstants.JJTNEWFUNC:
            symbol = "&undefined";
            break;
        case AlphaTreeConstants.JJTDOT:
            symbol = evalNodeDot(node, symbol, funcname, state);
            break;
        default:
            symbol = "";
            evalNodeDefault(node, symbol, funcname, state);
            break;
        }
        return symbol;
    }

    private String evalNodeProgram(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        child_node = (SimpleNode) node.jjtGetChild(1);
        setClassName(child_node.val);
        for (int i = 2; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval(child_node, symbol, funcname, State.BUILD);
        }
        symbol += tmp;
        return symbol;
    }

    private String evalNodeClassBody(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        if (node.getId() == AlphaTreeConstants.JJTCLASSBODY)
            symbol = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval(child_node, symbol, funcname, State.BUILD);
        }
        symbol += tmp;
        return symbol;
    }

    private String evalNodeFuncArgs(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval(child_node, symbol, funcname, state);
        }
        symbol = tmp;
        if (node.getId() == AlphaTreeConstants.JJTMINOR)
            symbol = "&boolean";
        return symbol;
    }

    private String evalNodeIdentifier(SimpleNode node, String symbol, String funcname, State state) {
        if (state == State.BUILD) // if it is building state the symbol must be the name of the variable
            symbol = "#" + node.val;
        else if (state == State.PROCESS) { // if it is processing state the variable must be validated and and symbol is
                                           // it's type
            symbol = getVarType(funcname, node.val);
            if (symbol.equals("")) {// if the variable was not declared aborts the program
                System.out.println("Variable not declared: " + node.val);
                System.exit(0);
            }
            symbol = "&" + symbol;
        }
        return symbol;
    }

    private String evalNodeIndex(SimpleNode node, String symbol, String funcname, State state) {
        symbol = eval((SimpleNode) node.jjtGetChild(0), symbol, funcname, State.PROCESS); // validates the identifier
        String index = eval((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.PROCESS); // validates the index
        if (!symbol.contains("$array") || !evaluateExpressionInt(index)) // case the variable was declared but is not an
                                                                         // array or the index is not and int
        {
            System.out.println("Variable not an array or index not an integer");
            System.exit(0);
        }
        symbol = symbol.split("\\$")[0];
        return symbol;
    }

    private String evalNodeIfWhile(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval(child_node, symbol, funcname, state);
        }
        return symbol;
    }

    private String evalNodeCondition(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval(child_node, symbol, funcname, State.PROCESS);
        }
        tmp = returnExpressionType(symbol);
        if (!tmp.equals("boolean") && !tmp.equals("undefined")) {
            System.out.println("Invalid Condition!");
            System.exit(0);
        }
        return symbol;
    }

    private String evalNodeEqual(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
        SimpleNode child_node;
        String tmp = "";
        String varType = eval(identifier, symbol, funcname, State.PROCESS);
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            tmp += eval(child_node, symbol, funcname, State.PROCESS);
        }
        symbol += tmp;
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
                symbol += tmp;
                String value = "local";
                if (funcname.equals(SymbolTable.GLOBAL))
                    value = "global";
                addSymbol(funcname, child_node.val, new Var(symbol.split("#")[1], child_node.val, value));
            }
            tmp += eval(child_node, symbol, funcname, State.BUILD);
        }
        return symbol;
    }

    private String evalNodeFuncDeclaration(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        String tmp = "";

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            if (child_node.getId() == AlphaTreeConstants.JJTBODY) { // this means the arguments are over and can create
                                                                    // the
                                                                    // symboltable entry
                symbol += tmp;
                funcname = addFunction(symbol);
            }
            tmp += eval(child_node, symbol, funcname, State.BUILD);
        }

        return symbol;
    }

    private String evalNodeFunc(SimpleNode node, String symbol, String funcname, State state) {
        symbol = ((SimpleNode) node.jjtGetChild(0)).val;
        SimpleNode child_node;
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol += eval(child_node, symbol, funcname, State.PROCESS);
        }
        return symbol;
    }

    private String evalNodeFuncArg(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            symbol = eval(child_node, symbol, funcname, State.PROCESS);
            if (symbol.equals(""))
                continue;
            symbol = "&" + returnExpressionType(symbol);
            if (symbol.equals("&")) {
                System.out.println("Invalid type 1");
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
            symbol = eval((SimpleNode) node.jjtGetChild(1), symbol, funcname, state);
            tmp = methodExistsWithUndefinedValues(symbol);
            if (tmp.equals("")) { // se a funçao com aqueles argumentos nao existir
                System.out.println("Invalid function");
                System.exit(0);
            } else {
                symbol = "&" + getFunctionReturnType(tmp);
            }
        } else if (tmp.equals("undefined")) {
            symbol = "undefined";
        } else {
            for (int i = 1; i < node.jjtGetNumChildren(); i++) {
                child_node = (SimpleNode) node.jjtGetChild(i);
                symbol = eval(child_node, symbol, funcname, state);
            }
        }
        return symbol;
    }

    private String evalNodeDefault(SimpleNode node, String symbol, String funcname, State state) {
        SimpleNode child_node;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            child_node = (SimpleNode) node.jjtGetChild(i);
            eval(child_node, symbol, funcname, state);
        }
        return symbol;
    }

    public static String returnExpressionType(String expression) {
        if (!expression.substring(0, 1).equals("&")) // if it is a outside function
            return "undefined";
        String expectedType = expression.split("&")[1];
        if (evaluateExpressionType("&" + expectedType, expression))
            return expectedType;
        else
            return "";
    }

    public static boolean evaluateExpressionType(String expectedType, String expression) {
        String[] tokens = expression.split("&");
        String processed_expectedType = expectedType.split("&")[1];
        if (processed_expectedType.equals("undefined"))
            return true;
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals("undefined"))
                continue;
            if (!processed_expectedType.equals(tokens[i]))
                return false;
        }
        return true;
    }

    public static boolean evaluateExpressionInt(String expression) {
        return evaluateExpressionType("&int", expression);
    }

}