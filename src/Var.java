
public class Var extends Symbol {
    String value;

   /* Var(Type returnType, String name, String value_) {
        super(returnType, name);
        this.value = value_;
    }*/

    Var(String returnType, String name, String value_) {
        super(returnType, name);
        this.value = value_;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + super.type +
                ", name=" + super.name +
                ", value=" + value +
                '}';
    }


}