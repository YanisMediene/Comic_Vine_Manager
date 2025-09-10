package tse.info2.util;

import org.junit.jupiter.api.Test;
import tse.info2.controller.AuthenticationController;
import tse.info2.service.AuthenticationService;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationControllerTest {


    @Test
    public void testRegisterDuplicateUser() {
        AuthenticationService authService = new AuthenticationService();
        AuthenticationController authController = new AuthenticationController(authService);

        // Initial registration
        authController.register("testUser", "password123", "password123");

        // Attempt to register a duplicate user
        boolean duplicateRegistration = authController.register("testUser", "password123", "password123");
        assertFalse(duplicateRegistration, "L'inscription d'un utilisateur dupliqué devrait échouer.");
        assertEquals("Le nom d'utilisateur est déjà pris.", authController.getErrorMessage(),
                "Le message d'erreur devrait indiquer que le nom d'utilisateur est déjà pris.");
    }

    @Test
    public void testLoginWithCorrectCredentials() {
        AuthenticationService authService = new AuthenticationService();
        AuthenticationController authController = new AuthenticationController(authService);

        // Register a user
        authController.register("testUser", "password123", "password123");

        // Test login with correct credentials
        boolean loginSuccess = authController.login("testUser", "password123");
        assertTrue(loginSuccess, "La connexion avec les bonnes informations devrait réussir.");
    }

    @Test
    public void testLoginWithIncorrectPassword() {
        AuthenticationService authService = new AuthenticationService();
        AuthenticationController authController = new AuthenticationController(authService);

        // Register a user
        authController.register("testUser", "password123", "password123");

        // Test login with incorrect password
        boolean loginFailure = authController.login("testUser", "wrongPassword");
        assertFalse(loginFailure, "La connexion avec un mot de passe incorrect devrait échouer.");
        assertEquals("Nom d'utilisateur ou mot de passe incorrect.", authController.getErrorMessage(),
                "Le message d'erreur devrait indiquer des informations d'identification incorrectes.");
    }

    @Test
    public void testLoginWithNonExistingUser() {
        AuthenticationService authService = new AuthenticationService();
        AuthenticationController authController = new AuthenticationController(authService);

        // Test login with a non-existing user
        boolean nonExistentUserLogin = authController.login("unknownUser", "password123");
        assertFalse(nonExistentUserLogin, "La connexion avec un utilisateur inexistant devrait échouer.");
        assertEquals("Nom d'utilisateur ou mot de passe incorrect.", authController.getErrorMessage(),
                "Le message d'erreur devrait indiquer des informations d'identification incorrectes.");
    }
}
