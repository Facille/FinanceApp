import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FinanceManager financeManager = new FinanceManager();
        financeManager.loadData();

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            System.out.println("Введите команду (или 'exit' для выхода):");
            command = scanner.nextLine();
            financeManager.processCommand(command);
        } while (!command.equalsIgnoreCase("exit"));

        financeManager.saveData();
        scanner.close();
    }
}