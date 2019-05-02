
public class Symbol { 

    String type;
    String name;
    String value;
    int counter;

    Symbol(String type_, String name_, String value_) {
        this.type = type_;
        this.name = name_;
        this.value = value_;
        counter = 0;
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

}