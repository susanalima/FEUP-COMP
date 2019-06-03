
public class Symbol { 

    static final String UNDEFINED_CVALUE = "@";
    String type;
    String name;
    String value;
    int counter;
    int size;
    String constValue;

    Symbol(String type_, String name_, String value_) {
        this.type = type_;
        this.name = name_;
        this.value = value_;
        this.counter = 0;
        this.size = -1;
        this.constValue = UNDEFINED_CVALUE;
    }
    
    void setCounter(int counter_) {
        this.counter = counter_;
    }

    int getCounter() {
        return this.counter;
    }

    void setSize(int size_) {
        this.size = size_;
    }

    int getSize() {
        return this.size;
    }

    void setConstValue(String value) {
        this.constValue = value;
    }

    String getConstValue() {
        return this.constValue;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name=" + name +
                ", value=" + value +
                ", counter=" + counter +
                ", size=" + size +
                ", constValue=" + constValue +
                '}';
    }

}