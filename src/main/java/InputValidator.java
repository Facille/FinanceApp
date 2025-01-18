public class InputValidator {
    public static boolean isValidAmount(String amountStr) {
        try {
            Double.parseDouble(amountStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}