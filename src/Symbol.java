
public abstract class Symbol { 
    //Type type;
    String type;
    String name;

    Symbol(String type_, String name_) {
        this.type = type_;
        this.name = name_;
    }
    
    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name=" + name +
                '}';
    }

}