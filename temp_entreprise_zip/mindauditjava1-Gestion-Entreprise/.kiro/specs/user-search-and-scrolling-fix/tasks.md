# Implementation Plan
# Implementation Plan

## Bug 1: Recherche par critères non fonctionnelle

- [x] 1. Write bug condition exploration test - Search Results Display
  - **Property 1: Bug Condition** - Search Results Not Displayed in TableView
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate the search bug exists
  - **Scoped PBT Approach**: For deterministic bugs, scope the property to the concrete failing case(s) to ensure reproducibility
  - Test implementation details from Bug Condition in design:
    - Simulate search by Name "Jean" (assuming user "Jean Dupont" exists in test data)
    - Verify that `userService.searchByName("Jean")` returns results (backend OK)
    - Verify that `userTable.getItems().size()` does NOT match the filtered count (bug UI)
    - Simulate search by Age "25"
    - Verify that `userService.searchByAge(25)` returns results (backend OK)
    - Verify that `userTable.getItems()` still contains all users (bug UI)
    - Simulate search by ID "5"
    - Verify that `userService.getUserById(5)` returns a user (backend OK)
    - Verify that `userTable.getItems().size()` is NOT equal to 1 (bug UI)
  - The test assertions should match the Expected Behavior Properties from design:
    - After search, table should display ONLY filtered results
    - Success label should show correct count message
  - Run test on UNFIXED code
  - **EXPECTED OUTCOME**: Test FAILS (this is correct - it proves the bug exists)
  - Document counterexamples found to understand root cause:
    - Backend returns correct results but TableView does not refresh
    - `setItems()` does not trigger visual update
  - Mark task complete when test is written, run, and failure is documented
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 2. Write preservation property tests for search functionality (BEFORE implementing fix)
  - **Property 2: Preservation** - Non-Search Functionality Unchanged
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for non-buggy inputs:
    - Sort by "Nom (A-Z)" → Observe alphabetical order is correct
    - Sort by "Âge (Croissant)" → Observe numerical order is correct
    - Add a user → Observe it appears in the table
    - Update a user → Observe changes are saved
    - Delete a user → Observe it disappears from the table
    - Select a user → Observe form fields are populated
    - Enter invalid age "abc" → Observe error message "Âge invalide"
    - Enter invalid ID "xyz" → Observe error message "ID invalide"
    - Click "Réinitialiser" → Observe all users are reloaded
  - Write property-based tests capturing observed behavior patterns from Preservation Requirements:
    - Property: For all sort operations (not involving search), table order matches expected sort order
    - Property: For all CRUD operations (not involving search), table updates correctly
    - Property: For all selection operations (not involving search), form is populated correctly
    - Property: For all validation errors (invalid age/ID), error messages display correctly
    - Property: For reset operation, all users are reloaded
  - Property-based testing generates many test cases for stronger guarantees
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests PASS (this confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

- [-] 3. Fix search results display issue

  - [x] 3.1 Implement the fix in UserManagementController.java
    - Open `src/main/java/com/example/mindjavafx/controller/UserManagementController.java`
    - Locate the `handleSearch()` method (around line 103-106)
    - Add `userTable.setItems(null);` before `userTable.setItems(userList);` to clear the table first
    - Add `userTable.refresh();` after `userTable.setItems(userList);` to force visual refresh
    - Apply the same logic to `handleResetSearch()` method for consistency
    - Modified code should look like:
      ```java
      if (results != null) {
          userList = FXCollections.observableArrayList(results);
          userTable.setItems(null);  // NOUVEAU : Vider d'abord
          userTable.setItems(userList);  // Puis mettre à jour
          userTable.refresh();  // NOUVEAU : Forcer le rafraîchissement
          errorLabel.setText("");
          successLabel.setText("✅ " + results.size() + " résultat(s) trouvé(s)");
          successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
      }
      ```
    - _Bug_Condition: isBugCondition_Search(input) where searchType in ['Nom', 'Âge', 'ID'] and searchValue is valid and backend returns results but table does not update_
    - _Expected_Behavior: For any valid search, TableView SHALL display ONLY filtered results immediately (Property 1 from design)_
    - _Preservation: Non-search functionality (sort, CRUD, selection, validation, reset) SHALL remain unchanged (Property 3 from design)_
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

  - [ ] 3.2 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Search Results Displayed Correctly
    - **IMPORTANT**: Re-run the SAME test from task 1 - do NOT write a new test
    - The test from task 1 encodes the expected behavior
    - When this test passes, it confirms the expected behavior is satisfied
    - Run bug condition exploration test from step 1
    - **EXPECTED OUTCOME**: Test PASSES (confirms bug is fixed)
    - Verify that:
      - Search by Name "Jean" displays only matching users in table
      - Search by Age "25" displays only users aged 25 in table
      - Search by ID "5" displays only user with ID 5 in table
      - Success label shows correct count message
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

  - [ ] 3.3 Verify preservation tests still pass
    - **Property 2: Preservation** - Non-Search Functionality Unchanged
    - **IMPORTANT**: Re-run the SAME tests from task 2 - do NOT write new tests
    - Run preservation property tests from step 2
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions)
    - Confirm all tests still pass after fix:
      - Sort operations work correctly
      - CRUD operations work correctly
      - Selection populates form correctly
      - Validation errors display correctly
      - Reset reloads all users correctly
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

## Bug 2: Scrolling très lent

- [ ] 4. Write bug condition exploration test - Scrolling Performance
  - **Property 1: Bug Condition** - Slow Scrolling with ScrollPane Wrapper
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate the scrolling bug exists
  - **Scoped PBT Approach**: Measure scroll performance with 50 users in test data
  - Test implementation details from Bug Condition in design:
    - Create 50 test users in the database
    - Measure time to scroll from top to bottom of the table
    - Verify that scroll time is > 2 seconds (slow with ScrollPane)
    - Verify that scrolling is laggy/unresponsive
  - The test assertions should match the Expected Behavior Properties from design:
    - After fix, scroll time should be < 500ms (fast and smooth)
    - Scrolling should be responsive without lag
  - Run test on UNFIXED code
  - **EXPECTED OUTCOME**: Test FAILS (this is correct - it proves the bug exists)
  - Document counterexamples found to understand root cause:
    - ScrollPane wrapper interferes with TableView native scroll
    - Virtualization is disabled or ineffective
    - All rows are rendered even when off-screen
  - Mark task complete when test is written, run, and failure is documented
  - _Requirements: 2.6, 2.7_

- [ ] 5. Write preservation property tests for scrolling functionality (BEFORE implementing fix)
  - **Property 2: Preservation** - Non-Scrolling Functionality Unchanged
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for non-scrolling interactions:
    - All observations from task 2 still apply (sort, CRUD, selection, validation, reset)
    - Additionally observe that table columns display correctly
    - Observe that form interactions work normally
  - Write property-based tests capturing observed behavior patterns:
    - Reuse tests from task 2 (they cover all non-scrolling functionality)
    - Add test for column display: All columns (ID, Nom, Email, Âge, Rôle, Statut) are visible
  - Run tests on UNFIXED code
  - **EXPECTED OUTCOME**: Tests PASS (this confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

- [ ] 6. Fix scrolling performance issue

  - [x] 6.1 Implement the fix in user-management.fxml
    - Open `src/main/resources/fxml/user-management.fxml`
    - Remove the root `<ScrollPane>` element (line 13) and replace with `<VBox>` as root element
    - Remove ScrollPane attributes: `fitToWidth="true"`, `vbarPolicy="AS_NEEDED"`, `hbarPolicy="NEVER"`
    - Remove the closing `</ScrollPane>` tag at the end of the file
    - Modify the VBox to be the root element with necessary attributes:
      - Add `xmlns="http://javafx.com/javafx/17"` and `xmlns:fx="http://javafx.com/fxml/1"`
      - Add `fx:controller="com.example.mindjavafx.controller.UserManagementController"`
      - Keep `spacing="20"` and `style="-fx-padding: 20;"`
    - Increase TableView height from `prefHeight="400"` to `prefHeight="500"` (line 56)
    - Structure should look like:
      ```xml
      <VBox xmlns="http://javafx.com/javafx/17" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.example.mindjavafx.controller.UserManagementController"
            spacing="20" style="-fx-padding: 20;">
          <!-- Existing content remains identical -->
      </VBox>
      ```
    - _Bug_Condition: isBugCondition_Scroll(input) where userTable is wrapped in ScrollPane and scrolling is slow/laggy_
    - _Expected_Behavior: For any scroll event, scrolling SHALL be smooth and responsive with normal speed (Property 2 from design)_
    - _Preservation: All non-scrolling functionality SHALL remain unchanged (Property 3 from design)_
    - _Requirements: 2.6, 2.7, 3.1, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

  - [ ] 6.2 Remove obsolete setupFastScrolling() method
    - Open `src/main/java/com/example/mindjavafx/controller/UserManagementController.java`
    - Locate the `setupFastScrolling()` method (lines 41-45)
    - Remove this empty method as it's no longer needed
    - The method only contained a comment explaining the problem, which is now fixed

  - [ ] 6.3 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Fast and Smooth Scrolling
    - **IMPORTANT**: Re-run the SAME test from task 4 - do NOT write a new test
    - The test from task 4 encodes the expected behavior
    - When this test passes, it confirms the expected behavior is satisfied
    - Run bug condition exploration test from step 4
    - **EXPECTED OUTCOME**: Test PASSES (confirms bug is fixed)
    - Verify that:
      - Scroll time from top to bottom is < 500ms (fast)
      - Scrolling is smooth and responsive without lag
      - TableView native scroll is working optimally
    - _Requirements: 2.6, 2.7_

  - [ ] 6.4 Verify preservation tests still pass
    - **Property 2: Preservation** - Non-Scrolling Functionality Unchanged
    - **IMPORTANT**: Re-run the SAME tests from task 5 - do NOT write new tests
    - Run preservation property tests from step 5
    - **EXPECTED OUTCOME**: Tests PASS (confirms no regressions)
    - Confirm all tests still pass after fix:
      - All functionality from task 3.3 still works
      - Table columns display correctly
      - Form interactions work normally
    - _Requirements: 3.1, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

- [ ] 7. Checkpoint - Ensure all tests pass
  - Run all bug condition exploration tests (tasks 1 and 4)
  - Run all preservation property tests (tasks 2 and 5)
  - Verify that:
    - Search by Name, Age, and ID displays filtered results correctly
    - Success messages show correct counts
    - Scrolling is fast and smooth (< 500ms for 50 users)
    - All non-buggy functionality (sort, CRUD, selection, validation, reset) works correctly
    - No regressions introduced
  - If any test fails, investigate and fix before proceeding
  - Ask the user if questions arise
