
import java.util.HashMap;

public class IoFunctions {

    private static IoFunctions instance = null;

    HashMap<String, String> functions; //  key : fn&Param1Type($array)?&Param2Type  value : return type

    private IoFunctions() { 
        buildFuntions();
    } 

    private void buildFuntions() {
        functions = new HashMap<>();
        functions.put("read", "int");
        functions.put("print&int", "void");
        functions.put("println", "void");
        functions.put("println&int", "void");
    }

    public static IoFunctions getInstance() { 
        if (instance == null) 
            instance = new IoFunctions(); 
        return instance; 
    } 

    public boolean isIoFunction(String funcname) {
        if(functions.get(funcname) == null)
            return false;
        return true;
    }

    public String getFunctionReturnType(String funcname) {
        if(!isIoFunction(funcname))
            return "";
        return functions.get(funcname);
    }

    public String methodExistsWithUndefinedValues(String funcname) {
        if (isIoFunction(funcname))
            return funcname;
        else
            return checkMethod(funcname);
    }

    private String checkMethod(String funcname) {
        String[] expected_functionBlock = funcname.split(SymbolTable.AND_SEPARATOR);
        String[] key_functionBlock;
        int count_func = 0, counter = 0;
        String return_key = "";
        for (String key : functions.keySet()) {
            key_functionBlock = key.split(SymbolTable.AND_SEPARATOR);
            if (key_functionBlock.length != expected_functionBlock.length
                    || !key_functionBlock[0].equals(expected_functionBlock[0]))
                continue;
            counter = 0;
            for (int i = 0; i < key_functionBlock.length; i++) {

                if (key_functionBlock[i].equals(expected_functionBlock[i])
                        || SymbolTable.checkUndefinedType(expected_functionBlock[i]))
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
}