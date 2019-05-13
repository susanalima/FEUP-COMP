
public class Symbol { 

    String type;
    String name;
    String value;
    int counter;
    int size;

    Symbol(String type_, String name_, String value_) {
        this.type = type_;
        this.name = name_;
        this.value = value_;
        this.counter = 0;
        this.size = -1;
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

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name=" + name +
                ", value=" + value +
                ", counter=" + counter +
                ", size=" + size +
                '}';
    }

}