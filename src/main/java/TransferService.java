public class TransferService {
    public static void transfer(User fromUser , User toUser , double amount) {
        if (fromUser .getWallet().getTotalIncome() < amount) {
            System.out.println("Недостаточно средств для перевода.");
            return;
        }
        Transaction transaction = new Transaction("Transfer to " + toUser .getUsername(), amount, TransactionType.EXPENSE);
        fromUser .getWallet().addTransaction(transaction);
        toUser .getWallet().addTransaction(new Transaction("Transfer from " + fromUser .getUsername(), amount, TransactionType.INCOME));
        NotificationService.notifyUser ("Перевод " + amount + " от " + fromUser .getUsername() + " к " + toUser .getUsername() + " выполнен.");
    }
}