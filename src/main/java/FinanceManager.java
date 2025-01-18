import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceManager implements Serializable {
    private Map<String, User> users;
    private User currentUser ;

    public FinanceManager() {
        this.users = new HashMap<>();
        users.put("testUser ", new User("testUser ", "password"));
    }

    public void processCommand(String command) {
        Scanner scanner = new Scanner(System.in);
        String[] parts = command.split(" ");

        switch (parts[0].toLowerCase()) {
            case "register":
                if (InputValidator.isValidUsername(parts[1]) && InputValidator.isValidPassword(parts[2])) {
                    registerUser (parts[1], parts[2]);
                } else {
                    System.out.println("Некорректные данные для регистрации.");
                }
                break;
            case "login":
                loginUser (parts[1], parts[2]);
                break;
            case "add_income":
                if (InputValidator.isValidAmount(parts[2])) {
                    addTransaction(parts[1], Double.parseDouble(parts[2]), TransactionType.INCOME);
                } else {
                    System.out.println("Некорректная сумма дохода.");
                }
                break;
            case "add_expense":
                if (InputValidator.isValidAmount(parts[2])) {
                    addTransaction(parts[1], Double.parseDouble(parts[2]), TransactionType.EXPENSE);
                } else {
                    System.out.println("Некорректная сумма расхода.");
                }
                break;
            case "set_budget":
                if (InputValidator.isValidAmount(parts[2])) {
                    setBudget(parts[1], Double.parseDouble(parts[2]));
                } else {
                    System.out.println("Некорректная сумма бюджета.");
                }
                break;
            case "show_info":
                if (parts.length > 1) {
                    showInfo(parts[1]);
                } else {
                    showInfo(null);
                }
                break;
            case "transfer":
                if (InputValidator.isValidAmount(parts[2])) {
                    transfer(parts[1], parts[3], Double.parseDouble(parts[2]));
                } else {
                    System.out.println("Некорректная сумма перевода.");
                }
                break;
            case "exit":
                System.out.println("Выход из приложения.");
                break;
            default:
                System.out.println("Неизвестная команда.");
        }
    }

    private void registerUser (String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("Пользователь с таким именем уже существует.");
        } else {
            users.put(username, new User(username, password));
            System.out.println("Пользователь зарегистрирован.");
        }
    }

    private void loginUser (String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser  = user;
            System.out.println("Успешный вход в систему.");
        } else {
            System.out.println("Неверное имя пользователя или пароль.");
        }
    }

    private void addTransaction(String category, double amount, TransactionType type) {
        if (currentUser  == null) {
            System.out.println("Сначала выполните вход в систему.");
            return;
        }
        Transaction transaction = new Transaction(category, amount, type);
        currentUser .getWallet().addTransaction(transaction);
        System.out.println("Транзакция добавлена: " + type + " " + amount + " в категории " + category);
    }

    private void setBudget(String category, double amount) {
        if (currentUser  == null) {
            System.out.println("Сначала выполните вход в систему.");
            return;
        }
        currentUser .getWallet().setBudget(category, amount);
        System.out.println("Бюджет установлен для категории " + category + ": " + amount);
    }

    public void showInfo(String category) {
        if (currentUser  == null) {
            System.out.println("Сначала выполните вход в систему.");
            return;
        }

        Wallet wallet = currentUser .getWallet();

        // Вывод общей информации
        double totalIncome = wallet.getTotalIncome();
        double totalExpenses = wallet.getTotalExpenses();

        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpenses);

        // Вывод информации по категориям
        for (Map.Entry<String, Double> entry : wallet.getBudgets().entrySet()) {
            String cat = entry.getKey();
            double budget = entry.getValue();
            double expenses = wallet.getTransactions().stream()
                    .filter(t -> t.getCategory().equals(cat) && t.getType() == TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            System.out.println("Категория: " + cat + ", Бюджет: " + budget + ", Расходы: " + expenses);
            if (expenses > budget) {
                System.out.println("Предупреждение: Превышен бюджет для категории " + cat);
            }
        }

        // Если указана категория, выводим информацию только по ней
        if (category != null && !category.isEmpty()) {
            double categoryIncome = wallet.getTransactions().stream()
                    .filter(t -> t.getCategory().equals(category) && t.getType() == TransactionType.INCOME)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double categoryExpenses = wallet.getTransactions().stream()
                    .filter(t -> t.getCategory().equals(category) && t.getType() == TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            System.out.println("Категория: " + category);
            System.out.println("Доходы: " + categoryIncome);
            System.out.println("Расходы: " + categoryExpenses);
        }
    }

    private void transfer(String fromUsername, String toUsername, double amount) {
        User fromUser  = users.get(fromUsername);
        User toUser  = users.get(toUsername);

        if (fromUser  == null || toUser  == null) {
            System.out.println("Один из пользователей не найден.");
            return;
        }

        if (fromUser .getWallet().getTotalIncome() < amount) {
            System.out.println("Недостаточно средств для перевода.");
            return;
        }

        Transaction transaction = new Transaction("Transfer to " + toUser .getUsername(), amount, TransactionType.EXPENSE);
        fromUser .getWallet().addTransaction(transaction);
        toUser .getWallet().addTransaction(new Transaction("Transfer from " + fromUser .getUsername(), amount, TransactionType.INCOME));

        NotificationService.notifyUser ("Перевод " + amount + " от " + fromUser .getUsername() + " к " + toUser .getUsername() + " выполнен.");
    }

    public void loadData() {
        File file = new File("data.dat");
        if (!file.exists()) {
            System.out.println("Файл данных не найден. Будет создан новый файл при сохранении.");
            return; // Выход из метода, если файл не найден
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<String, User>) ois.readObject();
            System.out.println("Данные загружены.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.dat"))) {
            oos.writeObject(users);
            System.out.println("Данные сохранены.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
}