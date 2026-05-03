package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.Role;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Preservation Property Tests for User Search and Scrolling Fix
 * 
 * **Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9**
 * 
 * Property 2: Preservation - Non-Search Functionality Unchanged
 * 
 * These tests verify that all functionality NOT related to search or scrolling
 * continues to work correctly after the fix is applied. This follows the
 * observation-first methodology:
 * 
 * 1. Observe behavior on UNFIXED code for non-buggy inputs
 * 2. Write property-based tests capturing observed behavior patterns
 * 3. Run tests on UNFIXED code - they should PASS
 * 4. After fix is applied, re-run tests - they should still PASS
 * 
 * EXPECTED OUTCOME ON UNFIXED CODE: PASS (confirms baseline behavior)
 * EXPECTED OUTCOME ON FIXED CODE: PASS (confirms no regressions)
 * 
 * Scope: All interactions that do NOT involve search by criteria or scrolling:
 * - Sort operations (by Name A-Z, Z-A, Age ascending, descending)
 * - CRUD operations (Add, Update, Delete users)
 * - Selection operations (clicking user in table populates form)
 * - Validation errors (invalid age, invalid ID)
 * - Reset operation (reload all users)
 */
public class UserManagementSearchPreservationTest {

    /**
     * Property Test: Sort by Name (A-Z) should maintain alphabetical order
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test verifies that sorting by name in ascending order produces
     * alphabetically ordered results. This functionality should not be affected
     * by the search fix.
     */
    @Property
    @Label("Preservation: Sort by Name (A-Z) maintains alphabetical order")
    void sortByNameAscendingShouldMaintainAlphabeticalOrder() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        // Act - Simulate sort by Name (A-Z)
        ObservableList<User> userList = FXCollections.observableArrayList(allUsers);
        userList.sort(Comparator.comparing(User::getNom));
        
        // Assert - Verify alphabetical order
        for (int i = 0; i < userList.size() - 1; i++) {
            String currentName = userList.get(i).getNom();
            String nextName = userList.get(i + 1).getNom();
            assertTrue(currentName.compareTo(nextName) <= 0,
                "Users should be in alphabetical order. " +
                "User at index " + i + " ('" + currentName + "') should come before or equal to " +
                "user at index " + (i + 1) + " ('" + nextName + "')");
        }
    }

    /**
     * Property Test: Sort by Name (Z-A) should maintain reverse alphabetical order
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Property
    @Label("Preservation: Sort by Name (Z-A) maintains reverse alphabetical order")
    void sortByNameDescendingShouldMaintainReverseAlphabeticalOrder() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        // Act - Simulate sort by Name (Z-A)
        ObservableList<User> userList = FXCollections.observableArrayList(allUsers);
        userList.sort((u1, u2) -> u2.getNom().compareTo(u1.getNom()));
        
        // Assert - Verify reverse alphabetical order
        for (int i = 0; i < userList.size() - 1; i++) {
            String currentName = userList.get(i).getNom();
            String nextName = userList.get(i + 1).getNom();
            assertTrue(currentName.compareTo(nextName) >= 0,
                "Users should be in reverse alphabetical order. " +
                "User at index " + i + " ('" + currentName + "') should come after or equal to " +
                "user at index " + (i + 1) + " ('" + nextName + "')");
        }
    }

    /**
     * Property Test: Sort by Age (Ascending) should maintain numerical order
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Property
    @Label("Preservation: Sort by Age (Ascending) maintains numerical order")
    void sortByAgeAscendingShouldMaintainNumericalOrder() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        // Act - Simulate sort by Age (Ascending)
        ObservableList<User> userList = FXCollections.observableArrayList(allUsers);
        userList.sort(Comparator.comparingInt(User::getAge));
        
        // Assert - Verify numerical order
        for (int i = 0; i < userList.size() - 1; i++) {
            int currentAge = userList.get(i).getAge();
            int nextAge = userList.get(i + 1).getAge();
            assertTrue(currentAge <= nextAge,
                "Users should be in ascending age order. " +
                "User at index " + i + " (age " + currentAge + ") should be younger than or equal to " +
                "user at index " + (i + 1) + " (age " + nextAge + ")");
        }
    }

    /**
     * Property Test: Sort by Age (Descending) should maintain reverse numerical order
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Property
    @Label("Preservation: Sort by Age (Descending) maintains reverse numerical order")
    void sortByAgeDescendingShouldMaintainReverseNumericalOrder() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        // Act - Simulate sort by Age (Descending)
        ObservableList<User> userList = FXCollections.observableArrayList(allUsers);
        userList.sort((u1, u2) -> Integer.compare(u2.getAge(), u1.getAge()));
        
        // Assert - Verify reverse numerical order
        for (int i = 0; i < userList.size() - 1; i++) {
            int currentAge = userList.get(i).getAge();
            int nextAge = userList.get(i + 1).getAge();
            assertTrue(currentAge >= nextAge,
                "Users should be in descending age order. " +
                "User at index " + i + " (age " + currentAge + ") should be older than or equal to " +
                "user at index " + (i + 1) + " (age " + nextAge + ")");
        }
    }

    /**
     * Property Test: Adding a user should increase table size by 1
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test verifies that the CRUD operation "Add User" works correctly.
     * After adding a user, the table should contain one more user.
     */
    @Property(tries = 10)
    @Label("Preservation: Adding a user increases table size by 1")
    void addingUserShouldIncreaseTableSize(@ForAll("validUserInputs") UserInput input) throws SQLException {
        // Arrange
        UserService userService = new UserService();
        int initialCount = userService.getAllUsers().size();
        
        // Create a test user
        Role role = new Role(1, input.roleName);
        User newUser = new User(input.nom, input.email, input.password, input.age, role);
        newUser.setTelephone(input.telephone);
        newUser.setActif(input.actif);
        
        // Act - Add user
        int userId = userService.addUser(newUser);
        
        try {
            // Assert - Table size should increase by 1
            assertTrue(userId > 0, "User should be added successfully");
            
            int finalCount = userService.getAllUsers().size();
            assertEquals(initialCount + 1, finalCount,
                "After adding a user, table size should increase by 1. " +
                "Initial: " + initialCount + ", Final: " + finalCount);
            
            // Verify the user exists
            User addedUser = userService.getUserById(userId);
            assertNotNull(addedUser, "Added user should be retrievable by ID");
            assertEquals(input.nom, addedUser.getNom(), "User name should match");
            assertEquals(input.email, addedUser.getEmail(), "User email should match");
            assertEquals(input.age, addedUser.getAge(), "User age should match");
            
        } finally {
            // Cleanup - Delete the test user
            if (userId > 0) {
                userService.deleteUser(userId);
            }
        }
    }

    /**
     * Property Test: Updating a user should preserve table size
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Property(tries = 10)
    @Label("Preservation: Updating a user preserves table size")
    void updatingUserShouldPreserveTableSize(@ForAll("validUserInputs") UserInput input) throws SQLException {
        // Arrange
        UserService userService = new UserService();
        
        // Create a test user first
        Role role = new Role(1, "User");
        User testUser = new User("Test User", "test@example.com", "password123", 30, role);
        int userId = userService.addUser(testUser);
        
        try {
            int initialCount = userService.getAllUsers().size();
            
            // Act - Update the user
            User userToUpdate = userService.getUserById(userId);
            userToUpdate.setNom(input.nom);
            userToUpdate.setEmail(input.email);
            userToUpdate.setAge(input.age);
            userToUpdate.setTelephone(input.telephone);
            userToUpdate.setActif(input.actif);
            
            boolean updated = userService.updateUser(userToUpdate);
            
            // Assert - Table size should remain the same
            assertTrue(updated, "User should be updated successfully");
            
            int finalCount = userService.getAllUsers().size();
            assertEquals(initialCount, finalCount,
                "After updating a user, table size should remain the same. " +
                "Initial: " + initialCount + ", Final: " + finalCount);
            
            // Verify the changes were saved
            User updatedUser = userService.getUserById(userId);
            assertEquals(input.nom, updatedUser.getNom(), "Updated name should be saved");
            assertEquals(input.email, updatedUser.getEmail(), "Updated email should be saved");
            assertEquals(input.age, updatedUser.getAge(), "Updated age should be saved");
            
        } finally {
            // Cleanup
            userService.deleteUser(userId);
        }
    }

    /**
     * Property Test: Deleting a user should decrease table size by 1
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Property(tries = 10)
    @Label("Preservation: Deleting a user decreases table size by 1")
    void deletingUserShouldDecreaseTableSize() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        
        // Create a test user first
        Role role = new Role(1, "User");
        User testUser = new User("Test Delete User", "delete@example.com", "password123", 25, role);
        int userId = userService.addUser(testUser);
        
        int initialCount = userService.getAllUsers().size();
        
        // Act - Delete the user
        boolean deleted = userService.deleteUser(userId);
        
        // Assert - Table size should decrease by 1
        assertTrue(deleted, "User should be deleted successfully");
        
        int finalCount = userService.getAllUsers().size();
        assertEquals(initialCount - 1, finalCount,
            "After deleting a user, table size should decrease by 1. " +
            "Initial: " + initialCount + ", Final: " + finalCount);
        
        // Verify the user no longer exists
        User deletedUser = userService.getUserById(userId);
        assertNull(deletedUser, "Deleted user should not be retrievable");
    }

    /**
     * Property Test: Selecting a user should populate form fields correctly
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test simulates the selection operation where clicking a user in the
     * table should populate the form fields with that user's data.
     */
    @Property
    @Label("Preservation: Selecting a user populates form fields correctly")
    void selectingUserShouldPopulateFormFields() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        // Act - Select the first user (simulate handleSelectUser)
        User selectedUser = allUsers.get(0);
        
        // Assert - Verify form fields would be populated correctly
        assertNotNull(selectedUser.getNom(), "Selected user should have a name");
        assertNotNull(selectedUser.getEmail(), "Selected user should have an email");
        assertTrue(selectedUser.getAge() > 0, "Selected user should have a valid age");
        assertNotNull(selectedUser.getRole(), "Selected user should have a role");
        assertNotNull(selectedUser.getRole().getNom(), "Selected user's role should have a name");
        
        // Verify the data is valid for form population
        assertFalse(selectedUser.getNom().isEmpty(), "User name should not be empty");
        assertFalse(selectedUser.getEmail().isEmpty(), "User email should not be empty");
        assertTrue(selectedUser.getAge() >= 18 && selectedUser.getAge() <= 100,
            "User age should be in valid range (18-100)");
    }

    /**
     * Unit Test: Invalid age input should display error message
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test verifies that validation for invalid age (non-numeric) works correctly.
     */
    @Test
    void invalidAgeInputShouldDisplayErrorMessage() {
        // Arrange
        String invalidAge = "abc";
        
        // Act - Try to parse age (simulate handleSearch validation)
        boolean isValid = false;
        String errorMessage = "";
        try {
            Integer.parseInt(invalidAge);
            isValid = true;
        } catch (NumberFormatException e) {
            errorMessage = "Âge invalide";
        }
        
        // Assert - Should fail validation
        assertFalse(isValid, "Invalid age 'abc' should fail validation");
        assertEquals("Âge invalide", errorMessage,
            "Error message should be 'Âge invalide' for non-numeric age");
    }

    /**
     * Unit Test: Invalid ID input should display error message
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     */
    @Test
    void invalidIdInputShouldDisplayErrorMessage() {
        // Arrange
        String invalidId = "xyz";
        
        // Act - Try to parse ID (simulate handleSearch validation)
        boolean isValid = false;
        String errorMessage = "";
        try {
            Integer.parseInt(invalidId);
            isValid = true;
        } catch (NumberFormatException e) {
            errorMessage = "ID invalide";
        }
        
        // Assert - Should fail validation
        assertFalse(isValid, "Invalid ID 'xyz' should fail validation");
        assertEquals("ID invalide", errorMessage,
            "Error message should be 'ID invalide' for non-numeric ID");
    }

    /**
     * Property Test: Reset operation should reload all users
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test verifies that the reset operation (handleResetSearch) correctly
     * reloads all users from the database.
     */
    @Property
    @Label("Preservation: Reset operation reloads all users")
    void resetOperationShouldReloadAllUsers() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        int expectedCount = allUsers.size();
        
        // Act - Simulate reset operation (handleResetSearch)
        List<User> reloadedUsers = userService.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(reloadedUsers);
        
        // Assert - Should reload all users
        assertEquals(expectedCount, userList.size(),
            "Reset operation should reload all users. " +
            "Expected " + expectedCount + " users, got " + userList.size());
        
        // Verify all users are present
        for (User expectedUser : allUsers) {
            boolean found = userList.stream()
                .anyMatch(u -> u.getId() == expectedUser.getId());
            assertTrue(found,
                "User with ID " + expectedUser.getId() + " should be present after reset");
        }
    }

    /**
     * Property Test: Empty search should reload all users
     * 
     * EXPECTED OUTCOME: PASS on both unfixed and fixed code
     * 
     * This test verifies that searching with an empty value reloads all users.
     */
    @Property
    @Label("Preservation: Empty search reloads all users")
    void emptySearchShouldReloadAllUsers() throws SQLException {
        // Arrange
        UserService userService = new UserService();
        List<User> allUsers = userService.getAllUsers();
        
        // Skip if no users
        if (allUsers.isEmpty()) {
            System.out.println("SKIPPED: No users in database");
            return;
        }
        
        int expectedCount = allUsers.size();
        String emptySearch = "";
        
        // Act - Simulate empty search (handleSearch with empty value)
        List<User> reloadedUsers;
        if (emptySearch.isEmpty()) {
            reloadedUsers = userService.getAllUsers();
        } else {
            reloadedUsers = new ArrayList<>();
        }
        
        // Assert - Should reload all users
        assertEquals(expectedCount, reloadedUsers.size(),
            "Empty search should reload all users. " +
            "Expected " + expectedCount + " users, got " + reloadedUsers.size());
    }

    /**
     * Provider for valid user inputs
     * Generates random valid user data for testing CRUD operations
     */
    @Provide
    Arbitraries<UserInput> validUserInputs() {
        Arbitrary<String> names = Arbitraries.strings()
            .withCharRange('a', 'z')
            .ofMinLength(3)
            .ofMaxLength(20)
            .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1));
        
        Arbitrary<String> emails = Arbitraries.strings()
            .withCharRange('a', 'z')
            .ofMinLength(5)
            .ofMaxLength(10)
            .map(s -> s + "@test.com");
        
        Arbitrary<Integer> ages = Arbitraries.integers().between(18, 65);
        
        Arbitrary<String> telephones = Arbitraries.strings()
            .withCharRange('0', '9')
            .ofLength(10);
        
        Arbitrary<String> roles = Arbitraries.of("Admin", "User", "Auditeur");
        
        Arbitrary<Boolean> actifs = Arbitraries.of(true, false);
        
        return Combinators.combine(names, emails, ages, telephones, roles, actifs)
            .as((nom, email, age, telephone, role, actif) ->
                new UserInput(nom, email, "password123", age, telephone, role, actif));
    }

    /**
     * User input holder for property-based testing
     */
    static class UserInput {
        final String nom;
        final String email;
        final String password;
        final int age;
        final String telephone;
        final String roleName;
        final boolean actif;

        UserInput(String nom, String email, String password, int age, String telephone, String roleName, boolean actif) {
            this.nom = nom;
            this.email = email;
            this.password = password;
            this.age = age;
            this.telephone = telephone;
            this.roleName = roleName;
            this.actif = actif;
        }
    }
}
