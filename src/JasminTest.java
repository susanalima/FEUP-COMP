import java.util.AbstractMap;

public class JasminTest {

  SymbolTable symbolTable;
  String code;
  String finalCode;
  String classHeader;
  String functionHeader;
  int labelCount;
  boolean unreachableCode;
  int stackSize;

  JasminTest(SymbolTable sT) {
    this.symbolTable = sT;
    this.code = "";
    this.classHeader = "";
    this.functionHeader = "";
    this.labelCount = 0;
    this.unreachableCode = false;
    this.stackSize = 0;
    this.finalCode = "";
  }

  public String process(SimpleNode node, String symbol, String funcname, State state, String possibleReturnType) {

    if (this.unreachableCode && node.getId() != AlphaTreeConstants.JJTMETHOD_DECLARATION
        && node.getId() != AlphaTreeConstants.JJTMETHOD_DECLARATION)
      return symbol;

    switch (node.getId()) {

    case AlphaTreeConstants.JJTPROGRAM:
      process_nodeProgram(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTCLASSBODY:
      process_nodeClassBody(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTMETHOD_DECLARATION:
      funcname = getFuncname(node, "", funcname, State.PROCESS, false);
      break;
    case AlphaTreeConstants.JJTMAINDECLARATION:
      funcname = getFuncname(node, "", funcname, State.PROCESS, true);
      break;
    case AlphaTreeConstants.JJTVAR_DECLARATION:
      process_nodeVarDeclaration(node, state);
      break;
    case AlphaTreeConstants.JJTIDENTIFIER:
      symbol = process_nodeIdentifier(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTINT:
      symbol += "int";
      break;
    case AlphaTreeConstants.JJTINTEGER:
      symbol += "int";
      stackSize++;
      code += "ldc " + node.val + "\n";
      break;
    case AlphaTreeConstants.JJTINDEX:
      symbol = process_nodeIndex(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTTRUE:
    case AlphaTreeConstants.JJTFALSE:
      symbol = process_nodeTrueFalse(node, symbol, state);
      break;
    case AlphaTreeConstants.JJTPLUS:
    case AlphaTreeConstants.JJTMINUS:
    case AlphaTreeConstants.JJTPRODUCT:
    case AlphaTreeConstants.JJTDIVISION:
      symbol = process_nodeOperator(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTNEWFUNC:
      symbol = process_nodeNewFunc(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTFUNC_ARG:
      symbol = process_nodeFuncArg(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTARGS:
    case AlphaTreeConstants.JJTARG:
    case AlphaTreeConstants.JJTFUNC_ARGS:
    case AlphaTreeConstants.JJTFUNC:
      symbol = process_nodeFunc(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTDOT:
      symbol = process_nodeDot(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTEQUAL:
      symbol = process_nodeEqual(node, symbol, funcname);
      break;
    case AlphaTreeConstants.JJTMINOR:
      symbol = process_nodeMinor(node, symbol, funcname, true);
      break;
    case AlphaTreeConstants.JJTAND:
      symbol = process_nodeAnd(node, symbol, funcname, state, possibleReturnType, symbol);
      break;
    case AlphaTreeConstants.JJTIF:
      symbol = process_nodeIf(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTWHILE:
      symbol = process_nodeWhile(node, symbol, funcname, state, possibleReturnType);
      break;
    case AlphaTreeConstants.JJTNOT: 
      symbol = process_nodeNot(node, symbol, funcname, state);
      break;
    case AlphaTreeConstants.JJTRETURN:
      process_nodeDefault(node, symbol, funcname, State.PROCESS, possibleReturnType);
      break;
    default:
      process_nodeDefault(node, symbol, funcname, State.BUILD, possibleReturnType);
      break;
    }
    return symbol;
  }

  private String paramType(String returnType) {
    switch (returnType) {
    case "boolean":
      return "Z";

    case "void":
      return "V";

    case "int":
      return "I";

    case "string":
      return "Ljava/lang/String;";

    case "int$array":
      return "[I";

    case "string$array":
      return "[Ljava/lang/String;";

    case SymbolTable.UNDEFINED_TYPE: // TODO CHECK IF THIS SHOULD BE DONE SOMEWHERE ELSE
      return "I";

    default:
      return "L" + returnType + ";";
    }
  }

  private String process_func_call(String funcname, String expression, boolean checkMethod,
      String possible_return_type, boolean isThis) {
    String[] tokens = expression.split(SymbolTable.AND_SEPARATOR);
    String processed = tokens[0] + "(";

    String prevExpression = expression;

    //System.out.println("expression1 : " + expression);

    if (checkMethod)
      expression = symbolTable.methodExistsWithUndefinedValues(expression);

     // System.out.println("expression2 : " + isThis);

    if(isThis && symbolTable.extends_ && expression.equals("")){
      expression = prevExpression;
      checkMethod = false;
    }

    tokens = expression.split(SymbolTable.AND_SEPARATOR);
    for (int i = 1; i < tokens.length; i++) {
      processed += paramType(tokens[i]);
    }
    processed += ")";

    if (!expression.equals("") && checkMethod) {
      String returnType = symbolTable.getFunctionReturnType(expression);
      processed += paramType(returnType);
    } else {
      processed += paramType(possible_return_type);
    }

    return processed;
  }

  private String get_jasmin_Funcname(String funcname) {
    String[] tokens = funcname.split(SymbolTable.AND_SEPARATOR);
    String jasmin_funcname = tokens[0] + "(";
    for (int i = 1; i < tokens.length; i++) {
      jasmin_funcname += paramType(tokens[i]);
    }
    return jasmin_funcname + ")";
  }

  private String build_funcDeclaration(String funcname, boolean isMain) {
    if (isMain)
      return ".method public static main([Ljava/lang/String;)V\n";
    return ".method public " + get_jasmin_Funcname(funcname) + paramType(symbolTable.getFunctionReturnType(funcname))
        + "\n";
  }

  private String getFuncname(SimpleNode node, String symbol, String funcname, State state, boolean isMain) {
    this.labelCount = 0; // reset label count
    this.unreachableCode = false; // reset unreachableCode flag
    this.stackSize = 0; // reset stackSize
    this.functionHeader = "";
    this.code = "";
    SimpleNode child_node;
    String tmp = "";
    State currState = State.BUILD;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      if (child_node.getId() == AlphaTreeConstants.JJTBODY) {

        if (state == State.PROCESS) {
          currState = state;
        }
        symbol += tmp;
        funcname = symbolTable.processFunction(symbol, false);
      }

      if (currState == State.BUILD)
        tmp += symbolTable.eval_build(child_node, symbol, funcname, currState);
      else if (currState == State.PROCESS) {
        functionHeader += build_funcDeclaration(funcname, isMain);
        tmp += process(child_node, "", funcname, currState, "int");

        if (!this.unreachableCode)
          code += getReturnInstruction(funcname) + "\n";

        functionHeader += ".limit stack " + stackSize + "\n" + ".limit locals "
            + (symbolTable.getLimitLocals(funcname) + 1) + "\n";
        finalCode += functionHeader + code + ".end method\n\n";
      }
    }
    return funcname;
  }

  private String getOperatorInstruction(SimpleNode node) {
    String instruction = "";

    switch (node.getId()) {
    case AlphaTreeConstants.JJTPLUS:
      instruction = "iadd \n";
      break;
    case AlphaTreeConstants.JJTMINUS:
      instruction = "isub \n";
      break;
    case AlphaTreeConstants.JJTPRODUCT:
      instruction = "imul \n";
      break;
    case AlphaTreeConstants.JJTDIVISION:
      instruction = "idiv \n";
      break;
    }

    return instruction;
  }

  private String getReturnInstruction(String funcname) {
    String instruction;
    String returnType = symbolTable.getFunctionReturnType(funcname);
    if (returnType.equals("int") || returnType.equals("boolean"))
      instruction = "ireturn";
    else if (returnType.equals("void")) {
      instruction = "return";
    } else {
      instruction = "areturn";
    }
    return instruction;
  }

  private void process_nodeVarDeclaration(SimpleNode node, State state) {
    if (state != State.PROCESS)
      return;
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(1);
    if (child_node.getId() == AlphaTreeConstants.JJTARRAY)
      child_node = (SimpleNode) node.jjtGetChild(2);
    if (symbolTable.isVarGlobal(child_node.val)) {
      classHeader += ".field " + child_node.val + " "
          + this.paramType(symbolTable.getVarType(SymbolTable.GLOBAL, child_node.val)) + "\n\n";
    }
  }

  private void process_nodeProgram(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    classHeader += ".class public " + symbolTable.getClassName() + "\n.super " + symbolTable.getParentClass() + "\n \n";
    finalCode += ".method public <init>()V\n\taload_0\n\tinvokespecial " + symbolTable.getParentClass()
        + "/<init>()V\n\treturn\n.end method\n\n";
    // TODO: Check Inheritance
    process_nodeDefault(node, symbol, funcname, state, possibleReturnType);
    finalCode = classHeader + finalCode;
  }

  private void process_nodeClassBody(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      if (child_node.getId() == AlphaTreeConstants.JJTVAR_DECLARATION)
        state = State.PROCESS;
      process(child_node, symbol, funcname, state, possibleReturnType);
    }
  }

  private String process_nodeIdentifier(SimpleNode node, String symbol, String funcname, State state) {
    if (state == State.PROCESS || state == State.CONDITION) {
      stackSize++;
      symbol += symbolTable.getVarType(funcname, node.val);
      if (symbolTable.isVarLocal(funcname, node.val)) {
        String type = symbolTable.getVarType(funcname, node.val);
        if (type.equals("int") || type.equals("boolean"))
          code += "iload ";
        else
          code += "aload ";
        code += symbolTable.getCounter(funcname, node.val) + "\n"; // MUDAR CONSOANTE O TIPO
      } else {
        code += "aload_0\n" + "getfield " + symbolTable.getClassName() + "/" + node.val + " "
            + this.paramType(symbolTable.getVarType(funcname, node.val)) + "\n";
      }
    } else {
      symbol += node.val;
    }
    return symbol;
  }

  private String process_nodeIndex(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
    process_nodeDefault(node, symbol, funcname, State.PROCESS, possibleReturnType);
    if (node.jjtGetParent().getId() != AlphaTreeConstants.JJTEQUAL) {
      stackSize++;
      code += "iaload\n";
    }
    symbol += symbolTable.getVarType(funcname, child_node.val).split("\\" + SymbolTable.ARRAY_SEPARATOR)[0];
    return symbol;
  }

  private String process_nodeTrueFalse(SimpleNode node, String symbol, State state) {
    int tmp = 0;
    if (node.getId() == AlphaTreeConstants.JJTTRUE)
      tmp = 1;
    symbol += "boolean";
    if (state == State.PROCESS || state == State.CONDITION) {
      stackSize++;
      code += "ldc " + tmp + "\n";
    }
    return symbol;
  }

  private String process_nodeOperator(SimpleNode node, String symbol, String funcname, State state) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += SymbolTable.AND_SEPARATOR + process(child_node, "", funcname, State.PROCESS, "int");
    }
    symbol = symbolTable.returnExpressionType(tmp);
    code += getOperatorInstruction(node);
    return symbol;
  }

  private String process_nodeMinor(SimpleNode node, String symbol, String funcname, boolean default_) {
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      process(child_node, "", funcname, State.PROCESS, "boolean");
    }

    if (node.jjtGetParent().getId() == AlphaTreeConstants.JJTAND) // in case the parent is an and
      default_ = false;

    if (default_) { // caso seja apenas uma chamada a x < y e nao uma condiçao de um if/loop
      String label_if = buildLabel();
      String label_goto = buildLabel();
      code += "if_icmpge  " + label_if + "\n" + "iconst_1\n" + "goto    " + label_goto + "\n" + label_if + ":\n"
          + "iconst_0\n" + label_goto + ":\n";
    }

    return "boolean";
  }

  private String process_nodeCondition(SimpleNode node, String symbol, String funcname, String possibleReturnType,
      boolean not) {
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
    String label = buildLabel();

    switch (child_node.getId()) {
    case AlphaTreeConstants.JJTMINOR:
      process_nodeMinor(child_node, symbol, funcname, false); // o menor faz parte de um if
      if (not)
        code += "if_icmplt    " + label + "\n";
      else
        code += "if_icmpge    " + label + "\n";
      break;
    case AlphaTreeConstants.JJTAND:
      label = process_nodeAnd(child_node, symbol, funcname, State.CONDITION,  "boolean", label);
      break;
    case AlphaTreeConstants.JJTNOT:
      label = process_nodeCondition((SimpleNode) child_node, symbol, funcname,  "boolean", true);
      break;
    default:
      process_nodeDefault(node, symbol, funcname, State.PROCESS, "boolean");
      if (not)
        code += "ifne    " + label + "\n";
      else
        code += "ifeq    " + label + "\n";
      break;
    }
    return label;
  }

  private String process_nodeIf(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    String label, new_label = "";
    boolean unreachable = false;
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0); // condition first child
    int childId = child_node.getId();
    boolean notTrue = false, notFalse = false;

    if(childId == AlphaTreeConstants.JJTNOT ) {
      SimpleNode tmp_child = (SimpleNode) child_node.jjtGetChild(0);
      if (tmp_child.getId() == AlphaTreeConstants.JJTTRUE)
        notTrue = true; 
      else if(tmp_child.getId() == AlphaTreeConstants.JJTFALSE)
        notFalse = true;
    }

    if (childId == AlphaTreeConstants.JJTTRUE || notFalse) { // case
                                                                                                                 // if(true/false
      child_node = (SimpleNode) node.jjtGetChild(1); // body
      symbol = process(child_node, symbol, funcname, state, possibleReturnType);

      if (this.unreachableCode)
        this.unreachableCode = false;

    } else if(childId == AlphaTreeConstants.JJTFALSE || notTrue) {

      child_node = (SimpleNode) node.jjtGetChild(2); // else
      symbol = process(child_node, symbol, funcname, state, possibleReturnType);

      if (this.unreachableCode)
        this.unreachableCode = false;
    } 
    else {
      // process_nodeDefault(node, symbol, funcname, State.BUILD, possibleReturnType);
      child_node = (SimpleNode) node.jjtGetChild(0); // condition
      label = process_nodeCondition(child_node, symbol, funcname, possibleReturnType, false); // process condition

      child_node = (SimpleNode) node.jjtGetChild(1); // body
      if (child_node.jjtGetNumChildren() == 0) { // caso nao seja um if vazio
        new_label = buildLabel();
        code += "goto   " + new_label + "\n";
      }
      process_nodeDefault(child_node, symbol, funcname, State.BUILD, possibleReturnType); // process body

      if (this.unreachableCode) {
        this.unreachableCode = false;
        unreachable = true;
      }

      child_node = (SimpleNode) node.jjtGetChild(2); // else
      process_nodeElse(child_node, symbol, funcname, possibleReturnType, label, unreachable);

      if (!new_label.equals(""))
        code += new_label + ":\n";
    }

    return symbol;
  }

  private String process_nodeWhile(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {

    String label_goto = buildLabel();
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0); // condition first child
    int childId = child_node.getId();
    boolean notFalse = false;

    if(childId == AlphaTreeConstants.JJTNOT ) {
      SimpleNode tmp_child = (SimpleNode) child_node.jjtGetChild(0);
      if (tmp_child.getId() == AlphaTreeConstants.JJTFALSE)
        notFalse = true; 
    }

    if (childId == AlphaTreeConstants.JJTTRUE || notFalse) { // case while(true/!false)
                                                                                                           
      code += label_goto + ":\n";
      child_node = (SimpleNode) node.jjtGetChild(1); // body
      symbol = process(child_node, symbol, funcname, state, possibleReturnType);
      code += "goto    " + label_goto + "\n";
      this.unreachableCode = true;

    } else {

      child_node = (SimpleNode) node.jjtGetChild(0); // condition
      code += label_goto + ":\n";
      String label = process_nodeCondition(child_node, symbol, funcname, possibleReturnType, false);

      child_node = (SimpleNode) node.jjtGetChild(1); // body
      process_nodeDefault(child_node, symbol, funcname, state, possibleReturnType);

      if (this.unreachableCode)
        this.unreachableCode = false;
      else {
        code += "goto    " + label_goto + "\n" + label + ":\n";
      }
    }

    return symbol;
  }


  private String process_nodeNot(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0);
    process(child_node, symbol, funcname, state, "boolean"); 
    if(child_node.getId() == AlphaTreeConstants.JJTAND || node.jjtGetParent().getId() == AlphaTreeConstants.JJTAND)
      return "boolean";
    String label_if = buildLabel() ,label_goto = buildLabel(); 
    code += "ifne " + label_if + "\niconst_1\n" + "goto    " + label_goto + "\n" + label_if + ":\n" + "iconst_0\n" + label_goto + ":\n";
    return "boolean";
  }


  private String process_nodeAnd(SimpleNode node, String symbol, String funcname, State state,
  String possibleReturnType, String label) {
    return process_nodeAnd(node, symbol, funcname, state, possibleReturnType, label, label, false);
  }



  private String process_nodeAnd(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType, String label, String notLabel, boolean swap) {

    if (node.jjtGetParent().getId() != AlphaTreeConstants.JJTAND && label.equals(""))
      label = buildLabel();

    if (state == State.BUILD)
      state = State.PROCESS;

    if(notLabel.equals(""))
        notLabel = label;
    
    SimpleNode left_child_node = (SimpleNode) node.jjtGetChild(0); // left child
    SimpleNode right_child_node = (SimpleNode) node.jjtGetChild(1); // rigth child
    boolean notLeft = false, notRight = false, notParent = false;

    if(node.jjtGetParent().getId() == AlphaTreeConstants.JJTNOT ) {
       notParent = true;
    }

    if( left_child_node.getId() == AlphaTreeConstants.JJTNOT)
      notLeft = true;

    if( right_child_node.getId() == AlphaTreeConstants.JJTNOT)
      notRight = true;

    if(notParent) {
      if((notLeft && notRight)|| (notLeft && !notRight) || (!notLeft && notRight) || (!notLeft && !notRight)) {
        swap = true;
        notLabel = buildLabel();
      } 
    }
    
 
    process_nodeAnd_side(left_child_node, label, funcname, state, possibleReturnType, notLabel, false); 
    process_nodeAnd_side(right_child_node, label, funcname, state, possibleReturnType, notLabel, swap);
 

    if (state != State.CONDITION) {
      String label_goto = buildLabel();
      code +=  "iconst_1\n" + "goto    " + label_goto + "\n" + label + ":\n" + "iconst_0\n" + label_goto + ":\n";
    }

    return label;
  }

  private void process_nodeAnd_side(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType, String label, boolean swap) {

    int nodeId = node.getId();

    if (nodeId == AlphaTreeConstants.JJTAND) {
      process_nodeAnd(node, symbol, funcname, State.CONDITION, possibleReturnType, symbol, label, swap);
      return;
     }


    process(node, symbol, funcname, state, possibleReturnType);


    if (nodeId == AlphaTreeConstants.JJTMINOR) {
      if (swap)
        code += "if_icmplt    " + symbol + "\n";
      else
        code += "if_icmpge    " + label + "\n";
    } else if (nodeId == AlphaTreeConstants.JJTNOT) {
      if (swap)
        code += "ifeq    " + symbol + "\n";
      else
        code += "ifne    " + label + "\n";
    } else {
      if (swap)
        code += "ifne    " + symbol + "\n";
      else
        code += "ifeq    " + label + "\n";
    }

    if(!label.equals(symbol) && swap) {
      label += ":\n";
      code += label;
    }


    return ;
  }


  private String process_nodeElse(SimpleNode node, String symbol, String funcname, String possibleReturnType,
      String label, boolean unreachableCode) {
    String new_label = buildLabel();
    SimpleNode child_node;
    child_node = (SimpleNode) node.jjtGetChild(0);
    boolean emptyElse = true;
    if (child_node.jjtGetNumChildren() > 0 && !unreachableCode) { // not an empty else (first child is body)
      code += "goto    " + new_label + "\n";
      emptyElse = false;
    }

    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      if (i == 0)
        code += label + ":\n";
      child_node = (SimpleNode) node.jjtGetChild(i);
      process(child_node, symbol, funcname, State.PROCESS, possibleReturnType);
    }

    if (!emptyElse)
      code += new_label + ":\n";
    return symbol;
  }

  private String process_nodeNewFunc(SimpleNode node, String symbol, String funcname, State state) {
    SimpleNode child_node;
    child_node = (SimpleNode) node.jjtGetChild(1);
    if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
      child_node = (SimpleNode) child_node.jjtGetChild(0);
      code += "new " + child_node.val + "\ndup\ninvokespecial " + child_node.val + "/" + "<init>"// child_node.val
          + "()V\n";
      symbol = child_node.val;
      stackSize += 2;
    } else if (child_node.getId() == AlphaTreeConstants.JJTINT) {
      process((SimpleNode) node.jjtGetChild(2), "", funcname, State.PROCESS, "");
      code += "newarray int\n";
      symbol = "int$array";
    }
    return symbol;
  }

  private String process_nodeFuncArg(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += SymbolTable.AND_SEPARATOR + process(child_node, symbol, funcname, State.PROCESS, possibleReturnType);
    }
    symbol += tmp;
    return symbol;
  }

  private String process_nodeFunc(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    String tmp = "";
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += process(child_node, symbol, funcname, State.BUILD, possibleReturnType);
    }
    symbol += tmp;
    return symbol;
  }

  private AbstractMap.SimpleEntry<String, Boolean> process_nodeDot_buildHeader(SimpleNode node, String symbol,
      String funcname, State state, String possibleReturnType) {

    String header = "";
    boolean checkMethod = true;
    SimpleNode child_node = (SimpleNode) node.jjtGetChild(0); // left child

    if (child_node.getId() == AlphaTreeConstants.JJTTHIS) {
      header = "invokevirtual " + symbolTable.getClassName();
    } else if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER) {
      if (symbolTable.wasVarDeclared(funcname, child_node.val)) {
        String vT = symbolTable.getVarType(funcname, child_node.val);
        if (!symbolTable.getClassName().equals(vT) && !symbolTable.getParentClass().equals(vT))
          checkMethod = false;
        stackSize++;
        code += "aload " + symbolTable.getCounter(funcname, child_node.val) + "\n";
        header = "invokevirtual " + symbolTable.getVarType(funcname, child_node.val);
      } else {
        header = "invokestatic " + child_node.val;
        checkMethod = false;
      }
    } else if (child_node.getId() == AlphaTreeConstants.JJTNEWFUNC) {
      process(child_node, symbol, funcname, state, possibleReturnType);
      child_node = (SimpleNode) child_node.jjtGetChild(1);
      checkMethod = false;
      if (child_node.getId() == AlphaTreeConstants.JJTFUNC) {
        child_node = (SimpleNode) child_node.jjtGetChild(0);
        if (child_node.val.equals(symbolTable.getClassName())) {
          header = "invokevirtual " + symbolTable.getClassName();
          checkMethod = true;
        } else {
          header = "invokevirtual " + child_node.val; // TODO CHECK THIS (invokevirtual: To invoke a public non-static
                                                      // method)
        }
      } else if (child_node.getId() == AlphaTreeConstants.JJTINT) {
        header = "invokevirtual " + "[I"; // TODO qual é a instrução?
      }

    } else {
      checkMethod = false;
    }

    AbstractMap.SimpleEntry<String, Boolean> returnValues = new AbstractMap.SimpleEntry<>(header, checkMethod);
    return returnValues;

  }

  private AbstractMap.SimpleEntry<String, String> process_nodeDot_children(SimpleNode node, String symbol,
      String funcname, State state, String possibleReturnType) {

    String[] tmp_symbol_tokens = null;
    SimpleNode child_node, tmp_node;
    String tmp_symbol = symbolTable.eval_process((SimpleNode) node.jjtGetChild(1), symbol, funcname, State.BUILD);
    String tmp = "";

    tmp_node = (SimpleNode) node.jjtGetChild(0);

    if (!SymbolTable.checkUndefinedType(tmp_symbol)) {

      if (tmp_node.val.equals("io"))
        tmp_symbol = IoFunctions.getInstance().methodExistsWithUndefinedValues(tmp_symbol);
      else
        tmp_symbol = symbolTable.methodExistsWithUndefinedValues(tmp_symbol);

      if (tmp_symbol.equals("")) {
        tmp_symbol = SymbolTable.UNDEFINED_TYPE;
      } else {
        tmp_symbol_tokens = tmp_symbol.split(SymbolTable.AND_SEPARATOR);
      }
    } else
      tmp_symbol = SymbolTable.UNDEFINED_TYPE;

    String returnValue = "";

    child_node = (SimpleNode) node.jjtGetChild(0);
    if (child_node.getId() == AlphaTreeConstants.JJTTHIS) {
      stackSize++;
      code += "aload_0\n";
    }

    for (int i = 1; i < node.jjtGetNumChildren(); i++) {
      if (tmp_symbol.equals(SymbolTable.UNDEFINED_TYPE))
        returnValue = "int";
      else {
        if (tmp_symbol_tokens.length != 1) // TODO CHECK THIS
          returnValue = tmp_symbol_tokens[i];
      }
      child_node = (SimpleNode) node.jjtGetChild(i);
      tmp += process(child_node, symbol, funcname, state, returnValue);
    }

    symbol += tmp;

     //System.out.println("symbol : " + symbol);

    AbstractMap.SimpleEntry<String, String> returnValues = new AbstractMap.SimpleEntry<>(symbol, tmp_symbol);
    return returnValues;
  }

  private String process_nodeDot(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {

    SimpleNode right_child_node = (SimpleNode) node.jjtGetChild(1);
    SimpleNode left_child_node = (SimpleNode) node.jjtGetChild(0);
    String header = "", tmp_symbol, pop = "", processedType;
    boolean checkMethod = true, io = false;
    symbol = "";

    if (right_child_node.getId() == AlphaTreeConstants.JJTFUNC) {

      AbstractMap.SimpleEntry<String, Boolean> headerAndCheckMethod = process_nodeDot_buildHeader(node, symbol,
          funcname, state, possibleReturnType);
      header = headerAndCheckMethod.getKey();
      checkMethod = headerAndCheckMethod.getValue();

      AbstractMap.SimpleEntry<String, String> symbols = process_nodeDot_children(node, symbol, funcname, state,
          possibleReturnType);
      symbol = symbols.getKey();
      tmp_symbol = symbols.getValue();

      if (left_child_node.val.equals("io")) {
        io = true;
        possibleReturnType = IoFunctions.getInstance().getFunctionReturnType(tmp_symbol);
      }


      String vT = symbolTable.getVarType(funcname, left_child_node.val);

      boolean isThis = ((left_child_node.getId() == AlphaTreeConstants.JJTTHIS) || (symbolTable.getParentClass().equals(vT)));

      processedType = process_func_call(funcname, symbol, checkMethod, possibleReturnType, isThis );

     // System.out.println("processedType1 : " + processedType);

      if (node.jjtGetParent().getId() == AlphaTreeConstants.JJTBODY
          && !processedType.substring(processedType.length() - 1).equals("V"))
        pop = "pop\n";
      else
        pop = "";

      code += header + "/" + processedType + "\n" + pop;

      if (!tmp_symbol.equals(SymbolTable.UNDEFINED_TYPE) && !io)
        symbol = symbolTable.getFunctionReturnType(tmp_symbol);
      else if (io) {
        symbol = possibleReturnType;
      } else
        symbol = tmp_symbol;

    } else if (right_child_node.getId() == AlphaTreeConstants.JJTLENGTH) {
      process(left_child_node, symbol, funcname, State.PROCESS, possibleReturnType);
      code += "arraylength\n";
      // checkMethod = false;
      symbol = "int";
    }

    return symbol;
  }

  private String process_nodeEqual(SimpleNode node, String symbol, String funcname) {
    SimpleNode child_node, left_child_node;

    String storeType, type;
    boolean isArray = false;

    left_child_node = (SimpleNode) node.children[0]; // left child -> identifier
    if (left_child_node.getId() == AlphaTreeConstants.JJTINDEX) { // in case it is an array assignment
      left_child_node = (SimpleNode) left_child_node.jjtGetChild(0);
      storeType = "iastore"; // TODO PODE SER aastore caso seja uma referencia (acho que nunca acontece)
      isArray = true;
    } else {
      type = symbolTable.getVarType(funcname, left_child_node.val);
      if (type.equals("int") || type.equals("boolean"))
        storeType = "istore ";
      else
        storeType = "astore ";
      storeType += symbolTable.getCounter(funcname, left_child_node.val);
    }

    if (!symbolTable.isVarLocal(funcname, left_child_node.val) && !isArray) {
      code += "aload_0\n";
      stackSize++;
    }

    String left_child_type = symbolTable.getVarType(funcname, left_child_node.val);

    State s = State.BUILD;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      if(child_node.getId() == AlphaTreeConstants.JJTNOT)
        s = State.PROCESS;
      process(child_node, symbol, funcname, s, left_child_type);
    }
    
    child_node = (SimpleNode) node.jjtGetChild(1); // right child


    if (child_node.getId() == AlphaTreeConstants.JJTIDENTIFIER || child_node.getId() == AlphaTreeConstants.JJTTRUE
        || child_node.getId() == AlphaTreeConstants.JJTFALSE) // CASE IT IS AN IDENTIFIER LIKE a = s
      process(child_node, symbol, funcname, State.PROCESS, symbol);

    if (child_node.getId() == AlphaTreeConstants.JJTINDEX) { // CASE IT IS AN INDEX LIKE a = x[2]
      code += "iaload\n";
      stackSize++;
    }

    if (symbolTable.isVarLocal(funcname, left_child_node.val) || isArray) {
      code += storeType + "\n";
    } else if (!isArray) {
      code += "putfield " + symbolTable.getClassName() + "/" + left_child_node.val + " "
          + this.paramType(symbolTable.getVarType(funcname, left_child_node.val)) + "\n";
    }

    return symbol;
  }

  private void process_nodeDefault(SimpleNode node, String symbol, String funcname, State state,
      String possibleReturnType) {
    SimpleNode child_node;
    for (int i = 0; i < node.jjtGetNumChildren(); i++) {
      child_node = (SimpleNode) node.jjtGetChild(i);
      process(child_node, symbol, funcname, state, possibleReturnType);
    }
  }

  private String buildLabel() {
    String label = "L" + this.labelCount;
    this.labelCount++;
    return label;
  }

}
