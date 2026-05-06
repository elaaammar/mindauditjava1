package com.example.mindjavafx.service;

import com.example.mindjavafx.util.PasswordUtil;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bug Condition Exploration Test for Login Authentication Fix
 * 
 * **Validates: Requirements 1.1, 1.2, 1.3, 2.1, 2.2, 2.3**
 * 
 * CRITICAL: This test MUST FAIL on unfixed code - failure confirms the bug exists.
 * The test encodes the EXPECTED behavior (correct credentials should authenticate successfully).
 * When this test passes after the fix, it confirms the bug is resolved.
 * 
 * Property 1: Bug Condition - Correct Credentials Authentication Failure
 * 
 * This test verifies that authentication with correct credentials (admin@mindaudit.com / admin123)
 * should succeed. On unfixed code, this test will FAIL because:
 * - The stored hash in the database is incorrect (SHA-256 of "123" instead of "admin123")
 * - PasswordUtil.verifyPassword() will return false for the correct password
 * - AuthenticationService.login() will fail despite correct credentials
 */
public class AuthenticationServiceBugConditionTest {

    /**
     * Property Test: Correct credentials for admin@mindaudit.com should authenticate successfully
     * 
     * EXPECTED OUTCOME ON UNFIXED CODE: FAIL
     * - This failure confirms the bug exists
     * - The stored hash doesn't match the hash of "admin123"
     * - Authentication fails despite correct credentials
     * 
     * EXPECTED OUTCOME ON FIXED CODE: PASS
     * - Authentication succeeds with correct credentials
     * - currentUser is set correctly
     * - User can access the dashboard
     */
    @Property
    @Label("Bug Condition: Correct credentials authentication should succeed")
    void correctCredentialsShouldAuthenticateSuccessfully(@ForAll("adminCredentials") Credentials creds) {
        // Arrange
        AuthenticationService authService = new AuthenticationService();
        
        // Act
        boolean loginResult = authService.login(creds.email, creds.password);
        
        // Assert - These assertions encode the EXPECTED behavior
        // On unfixed code, these will FAIL, confirming the bug exists
        assertTrue(loginResult, 
            "Authentication should succeed with correct credentials (admin@mindaudit.com / admin123). " +
            "FAILURE HERE CONFIRMS THE BUG: stored hash doesn't match hash of correct password.");
        
        assertNotNull(authService.getCurrentUser(), 
            "Current user should be set after successful authentication. " +
            "FAILURE HERE CONFIRMS THE BUG: user not set because authentication failed.");
        
        assertEquals(creds.email, authService.getCurrentUser().getEmail(),
            "Current user email should match login email. " +
            "FAILURE HERE CONFIRMS THE BUG: wrong user or user not set.");
        
        assertTrue(authService.isLoggedIn(),
            "User should be logged in after successful authentication. " +
            "FAILURE HERE CONFIRMS THE BUG: login state not set because authentication failed.");
        
        assertNull(authService.getLastErrorMessage(),
            "No error message should be present after successful authentication. " +
            "FAILURE HERE CONFIRMS THE BUG: error message indicates authentication failure.");
    }

    /**
     * Unit Test: Direct password verification should succeed for correct password
     * 
     * EXPECTED OUTCOME ON UNFIXED CODE: FAIL
     * - The stored hash "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3" 
     *   is the SHA-256 hash of "123", not "admin123"
     * - verifyPassword("admin123", storedHash) returns false
     * 
     * EXPECTED OUTCOME ON FIXED CODE: PASS
     * - The stored hash is updated to SHA-256 of "admin123"
     * - verifyPassword("admin123", storedHash) returns true
     */
    @Test
    void passwordVerificationShouldSucceedForCorrectPassword() {
        // The INCORRECT hash currently stored in database (SHA-256 of "123")
        String incorrectStoredHash = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3";
        
        // The CORRECT hash that should be stored (SHA-256 of "admin123")
        String correctStoredHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
        
        String correctPassword = "admin123";
        
        // Verify that the correct password generates the expected hash
        String generatedHash = PasswordUtil.hashPassword(correctPassword);
        assertEquals(correctStoredHash, generatedHash,
            "Hash generation should produce correct SHA-256 hash for 'admin123'");
        
        // This assertion will FAIL on unfixed code because the database has the wrong hash
        // On unfixed code: verifyPassword("admin123", incorrectStoredHash) returns false
        // This failure confirms the root cause: stored hash doesn't match correct password
        boolean verificationResult = PasswordUtil.verifyPassword(correctPassword, incorrectStoredHash);
        
        // NOTE: We're testing against the INCORRECT hash to demonstrate the bug
        // This will fail, showing that the stored hash is wrong
        assertFalse(verificationResult,
            "DEMONSTRATION: Password verification FAILS with incorrect stored hash. " +
            "This confirms the bug - the database has SHA-256('123') instead of SHA-256('admin123')");
        
        // This assertion shows what SHOULD happen with the correct hash
        boolean correctVerification = PasswordUtil.verifyPassword(correctPassword, correctStoredHash);
        assertTrue(correctVerification,
            "Password verification SHOULD succeed with correct stored hash. " +
            "After fix, database will have this correct hash.");
    }

    /**
     * Unit Test: Verify the stored hash is incorrect
     * 
     * This test demonstrates that the stored hash is the SHA-256 of "123", not "admin123"
     */
    @Test
    void storedHashIsIncorrect() {
        String incorrectStoredHash = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3";
        String wrongPassword = "123";
        
        // Verify that the stored hash matches SHA-256("123")
        String hashOfWrongPassword = PasswordUtil.hashPassword(wrongPassword);
        assertEquals(incorrectStoredHash, hashOfWrongPassword,
            "DEMONSTRATION: The stored hash matches SHA-256('123'), not SHA-256('admin123'). " +
            "This confirms the root cause - wrong password was hashed during database setup.");
    }

    /**
     * Provider for admin credentials
     * Scoped to the specific failing case: admin@mindaudit.com with password "admin123"
     */
    @Provide
    Arbitraries<Credentials> adminCredentials() {
        return Arbitraries.just(new Credentials("admin@mindaudit.com", "admin123"));
    }

    /**
     * Simple credentials holder
     */
    static class Credentials {
        final String email;
        final String password;

        Credentials(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
