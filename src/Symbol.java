
public abstract class Symbol { 
    //Type type;
    String type;
    String name;
    int counter;

    Symbol(String type_, String name_) {
        this.type = type_;
        this.name = name_;
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
                '}';
    }

}