# Compiler of the Java-- language to Java Bytecodes

## Y2019-G66

Afonso Azevedo, up201603523, 19, 33%

Gonçalo Santos, up201603265, 19, 33%

Susana Lima, up201603634, 19, 34%


## GLOBAL Grade of the project: 

Considering all the required features to have a ceiling of 18 and the “-o” optimization were implemented, and the group only has 3 elements, we grade our project a 19.

## SUMMARY:
Our tool generates the jasmin instructions for a file written in the Java-- language. To achieve this, the tool starts by constructing an AST with the structures and expressions present in the code. After generating the AST, it proceeds to construct a symbol table and then executes the semantic analysis of the code. At this point the tools has all necessary structures to start generating the jasmin code, if no errors were found, the tool outputs the generated code to a file with the “.j” extension.

## EXECUTE: 
java -jar JMM.jar [-o] <fileName>

## DEALING WITH SYNTACTIC ERRORS: 

As specified in the project assignment, if an error is found in a while statement, the compiler ignores every token until the next ‘{‘ token, showing an error message indicating the line/column where the error occurred and the token responsible for the error. Our tool doesn’t stop after finding an error, continuing until it reaches the end, to provide a better user experience.

Errors:
 - Ignores errors found in a while statement, skipping to the next ‘{‘

## SEMANTIC ANALYSIS:

Our tool implements the following semantic rules:

- Function not declared;
- Function type mismatch;
- Wrong number of arguments for a function;
- Wrong type of arguments;
- Redefinition of local variables;
- Redefinition of global variables;
- Variable assignment type mismatches
- Variables previously defined as other types;
- Undefined variables when the class does not extend;
- Detection if operations are done with variables of type integer;
- Detection if expressions for conditions are of boolean type;
- Return type mismatch;
- Method can be declared after or before any other function calls it
- Supports the overload of functions (functions with the same name but different parameters)
- Validates functions from the io class
- and others


## INTERMEDIATE REPRESENTATIONS 

### Symbol Table

#### Symbol table

This class is responsible for building the Symbol Table and making the semantic analysis. The Symbol Table is a HashMap with the information regarding each method of the class. The key is a string with the name and parameters type of the function and the value is a FunctionBlock class. In this structure are also stored the global variables.

#### Function block

This class is used to store a method’s information, namely its return type and local variables. Again, the local variables information is stored in a HashMap, the key being the variable’s name and the value a Symbol class.

#### Symbol

This class is used to store the information of a variable, most importantly it’s name, type, allocated register index. It also stores the variable’s value to be used in “-o” optimization.


## CODE GENERATION:

If there aren’t any errors (from the grammar or the semantic analysis) the code for the specified file is generated. This process is done by the JasminTest class. To do so, the AST is analyzed and for every node the children are analyzed first and then the node itself, allowing the correct generation of code regarding the operations order. The code generated for each node is stored in a variable, global to the class, which is printed to a specified file in the end of the execution. The instructions used to load constants are ‘ldc’ and ‘iconst_’, the ones used to load registers are ‘iload’, ‘aload’ and to store registers ‘istore’, ‘astore’. The stack and locals number is calculated for each function while analysing the AST and added to the code.


## OVERVIEW: 

To develop the tool we didn’t use any third-party tools and/or packages or any external algorithms. All the program’s structure and code was developed by us. The AST is analysed three times over the program’s execution, one for building the Symbol Table, another for the Semantic Analysis and the last one for generating the code. This means that the code structure to do this three procedures is very similar, which facilitated our work in general. In retrospect the structure could have been implemented in a simpler way in order to facilitate the implementation of certain optimizations, however we think that in general, the structure implemented serves its purpose with great efficiency.

## TASK DISTRIBUTION:

The realization of the project in question was possible due to the joint effort of all the members of the group, in an organized and synchronized way so that there was not great disparity of contribution between the different elements. Each element focused more on the module with which it was more comfortable, but in general the work was divided equally among all.

## PROS: 

Our tool implements most of the specified requirements, being behind only regarding the optimizations, The “-o” optimization was implemented, but, unfortunately there was not enough time to implement the “-r” optimization. This being said, we are very pleased with the developed tool. It should be noted that the program uses the context in which a variable is used to discover its type if it was not declared and the class extends from another class (we assumed the not defined variables are field from the parent class), which could be a very useful feature especially if the project was to be improved in the future.

 
## CONS: 

As mentioned above the register allocation optimization was not implemented and the error reporting is not very detailed due to the complexity of the code for the semantic analysis.
