package bank;

public class Account {
    int id;
    String name;

    Account(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return name;
    }
}