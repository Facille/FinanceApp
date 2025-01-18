import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet implements Serializable {
    private List<Transaction> transactions;
    private Map<String, Double> budget;

    public Wallet() {
        this.transactions = new ArrayList<>();
        this.budget = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public void setBudget(String category, double amount) {
        budget.put(category, amount);
    }

    public double getBudget(String category) {
        return budget.getOrDefault(category, 0.0);
    }

    public Map<String, Double> getBudgets() {
        return budget;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}