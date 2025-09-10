package tse.info2.service;

import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService {

    private final DatabaseService databaseService;

    public AuthenticationService() {
        this.databaseService = new DatabaseService(); // For user storage
    }

    public boolean login(String userName, String password) {
        if (!databaseService.usernameExists(userName)) {
            System.out.println("Error: Username does not exist.");
            return false;
        }
        String storedHash = databaseService.getPassword(userName);
        if (!BCrypt.checkpw(password, storedHash)) {
            System.out.println("Error: Invalid password.");
            return false;
        }
        System.out.println("Login successful!");
        return true;
    }

    public String registerUser(String userName, String password, String confirmPassword) {
        // Validate password
        if (!validatePassword(password, confirmPassword)) {
            return "Les mots de passe ne correspondent pas ou sont vides.";
        }

        // Check if username already exists
        if (databaseService.usernameExists(userName)) {
            return "Le nom d'utilisateur est déjà pris.";
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Add the new user with hashed password
        databaseService.addUser(userName, hashedPassword);
        return "User registered successfully.";
    }

    private boolean validatePassword(String password, String confirmPassword) {
        return password != null && !password.isEmpty() && password.equals(confirmPassword);
    }
}
