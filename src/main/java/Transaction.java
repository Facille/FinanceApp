import java.io.Serializable;

public class Transaction implements Serializable {
    private String category;
    private double amount;
    private TransactionType type;

    public Transaction(String category, double amount, TransactionType type) {
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }
}