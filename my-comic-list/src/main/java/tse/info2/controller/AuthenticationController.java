package tse.info2.controller;

import tse.info2.service.AuthenticationService;

public class AuthenticationController {
    private final AuthenticationService authService;
    private String errorMessage; // To hold error messages for the view

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    public boolean login(String userName, String password) {
        if (!authService.login(userName, password)) {
            errorMessage = "Nom d'utilisateur ou mot de passe incorrect.";
            return false;
        }
        return true;
    }

    public boolean register(String userName, String password, String confirmPassword) {
        String result = authService.registerUser(userName, password, confirmPassword);
        if (!result.equals("User registered successfully.")) {
            errorMessage = result; // Store the error message
            return false;
        }
        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
