
public class Symbol { 

    String type;
    String name;
    String value;
    int counter;

   /* Symbol() {
        this.type = "int";
        this.name = "a";
        this.value = "local";
        this.counter = 0;
    }*/

    Symbol(String type_, String name_, String value_) {
        this.type = type_;
        this.name = name_;
        this.value = value_;
        this.counter = 0;
    }
    
    void setCounter(int counter_) {
        this.counter = counter_;
    }

    int getCounter() {
        return this.counter;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name=" + name +
                ", value=" + value +
                ", counter=" + counter +
                '}';
    }


    /*int getCounter(int a) {
        return this.counter;
    }*/

    boolean check_test(boolean a) {
        return a;
    }

    public void Test(int[] array) {
        //int x = new Symbol().getCounter(1);
        boolean a= true;
        int c = 1;
        if(a && this.check_test(a) && this.check_test(true)) {
            c = c+1;
        } else {
            c = c-1;
        }
        
        
    }

}