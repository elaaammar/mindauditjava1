# Login Authentication Fix - Bugfix Design

## Overview

The authentication system fails because the password hash stored in the database does not match the hash generated from the correct password. Analysis reveals that the stored hash `a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3` is the SHA-256 hash of "123", not "admin123". This mismatch causes `PasswordUtil.verifyPassword()` to always return false for the correct password.

The fix involves two components:
1. **Database Correction**: Update the stored password hash to match the SHA-256 hash of "admin123"
2. **Code Cleanup**: Remove temporary debug bypasses that were added to work around the authentication failure

This approach ensures proper password verification while maintaining all existing validation and security checks.

## Glossary

- **Bug_Condition (C)**: The condition that triggers the bug - when a user enters correct credentials (admin@mindaudit.com / admin123) but the stored hash doesn't match the generated hash
- **Property (P)**: The desired behavior when correct credentials are entered - authentication should succeed and user should be logged in
- **Preservation**: Existing validation logic (email format, password length, account status checks) that must remain unchanged by the fix
- **PasswordUtil.verifyPassword()**: The function in `src/main/java/com/example/mindjavafx/util/PasswordUtil.java` that compares input password hash with stored hash
- **AuthenticationService.login()**: The function in `src/main/java/com/example/mindjavafx/service/AuthenticationService.java` that orchestrates the authentication flow
- **password_hash**: The database column in `userjava` table that stores SHA-256 hashed passwords

## Bug Details

### Bug Condition

The bug manifests when a user enters the correct credentials (admin@mindaudit.com / admin123) but the authentication fails. The `PasswordUtil.verifyPassword()` function generates a SHA-256 hash from the input password and compares it to the stored hash, but the comparison fails because the stored hash in the database is incorrect.

**Formal Specification:**
```
FUNCTION isBugCondition(input)
  INPUT: input of type LoginAttempt {email: String, password: String}
  OUTPUT: boolean
  
  RETURN input.email == "admin@mindaudit.com"
         AND input.password == "admin123"
         AND storedHash(input.email) == "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
         AND hashPassword(input.password) != storedHash(input.email)
         AND authenticationFails(input)
END FUNCTION
```

### Examples

- **Example 1**: User enters "admin@mindaudit.com" and "admin123" → Expected: Login succeeds | Actual: "Mot de passe incorrect" error
- **Example 2**: User enters "admin@mindaudit.com" and "123" → Expected: "Mot de passe incorrect" | Actual: Login succeeds (due to debug bypass)
- **Example 3**: User enters "user@mindaudit.com" and correct password → Expected: Login succeeds | Actual: May fail if hash is also incorrect
- **Edge case**: User enters "admin@mindaudit.com" with any password → Expected: Proper validation | Actual: Debug mode bypasses all checks

## Expected Behavior

### Preservation Requirements

**Unchanged Behaviors:**
- Email format validation must continue to reject invalid email formats
- Password length validation must continue to reject passwords shorter than 6 characters
- Empty field validation must continue to reject empty email or password fields
- Account status checks must continue to reject disabled accounts (actif = false)
- User existence checks must continue to reject non-existent email addresses
- Database error handling must continue to display appropriate error messages
- Logout functionality must continue to clear the current user session
- Role-based access control checks must continue to work correctly

**Scope:**
All inputs that do NOT involve the specific admin@mindaudit.com account with password "admin123" should be completely unaffected by this fix. This includes:
- Login attempts with invalid email formats
- Login attempts with incorrect passwords (for any account)
- Login attempts with non-existent email addresses
- Login attempts with disabled accounts
- Login attempts with empty fields
- Login attempts with passwords shorter than 6 characters

## Hypothesized Root Cause

Based on the bug description and code analysis, the root causes are:

1. **Incorrect Database Hash**: The stored hash `a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3` is the SHA-256 hash of "123", not "admin123"
   - Verified: SHA-256("123") = a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3
   - Expected: SHA-256("admin123") = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
   - This mismatch causes `PasswordUtil.verifyPassword()` to always return false for "admin123"

2. **Debug Bypass Code**: Temporary workarounds were added to allow login despite the hash mismatch
   - `AuthenticationService.login()` contains: `if (email.equals("admin@mindaudit.com")) { return true; }`
   - `PasswordUtil.verifyPassword()` contains: `if (storedHash.equals("admin123") && inputPassword.equals("admin123")) { return true; }`
   - These bypasses skip proper password verification and create security vulnerabilities

3. **Inconsistent Hash Generation**: The database may have been populated with hashes generated by a different method or with incorrect input

## Correctness Properties

Property 1: Bug Condition - Correct Credentials Authentication

_For any_ login attempt where the email is "admin@mindaudit.com" and the password is "admin123", the fixed authentication system SHALL successfully authenticate the user, set the currentUser in AuthenticationService, and load the dashboard interface.

**Validates: Requirements 2.1, 2.2, 2.3**

Property 2: Preservation - Invalid Input Rejection

_For any_ login attempt where the credentials are invalid (wrong email format, wrong password, non-existent user, disabled account, empty fields, or short password), the fixed authentication system SHALL produce exactly the same validation error messages and rejection behavior as the original system, preserving all existing security checks.

**Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7**

## Fix Implementation

### Changes Required

Assuming our root cause analysis is correct:

**File 1**: Database (`mindaudit` database, `userjava` table)

**Change**: Update password hash for admin@mindaudit.com

**Specific Changes**:
1. **Update Admin Password Hash**: Execute SQL to update the password_hash column
   ```sql
   UPDATE userjava 
   SET password_hash = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9' 
   WHERE email = 'admin@mindaudit.com';
   ```
   - This sets the hash to SHA-256("admin123")
   - Ensures `PasswordUtil.verifyPassword("admin123", storedHash)` returns true

**File 2**: `src/main/java/com/example/mindjavafx/service/AuthenticationService.java`

**Function**: `login(String email, String password)`

**Specific Changes**:
1. **Remove Debug Bypass**: Delete the debug mode code that bypasses authentication
   ```java
   // DELETE THESE LINES:
   if (email.equals("admin@mindaudit.com")) {
       System.out.println("DEBUG: Connexion admin autorisée (mode debug)");
       this.currentUser = user;
       return true;
   }
   ```
   - This ensures all users go through proper password verification
   - Restores security by requiring correct password hashes

**File 3**: `src/main/java/com/example/mindjavafx/util/PasswordUtil.java`

**Function**: `verifyPassword(String inputPassword, String storedHash)`

**Specific Changes**:
1. **Remove Plaintext Password Bypass**: Delete the compatibility mode code
   ```java
   // DELETE THESE LINES:
   if (storedHash.equals("admin123") && inputPassword.equals("admin123")) {
       System.out.println("DEBUG: Mot de passe en clair accepté (mode compatibilité)");
       return true;
   }
   ```
   - This ensures passwords are always verified via hash comparison
   - Prevents accepting plaintext passwords stored in database

2. **Remove Debug Logging**: Optionally remove or reduce debug print statements
   ```java
   // OPTIONALLY REMOVE OR COMMENT OUT:
   System.out.println("DEBUG: Input hash: " + inputHash);
   System.out.println("DEBUG: Stored hash: " + storedHash);
   System.out.println("DEBUG: Match: " + (inputHash != null && inputHash.equals(storedHash)));
   ```
   - Reduces console noise in production
   - Prevents password hash exposure in logs

**File 4**: `mindaudit.sql` (optional - for future database setup)

**Change**: Update the default admin password hash in the INSERT statement

**Specific Changes**:
1. **Update SQL Script**: Change the hash in the INSERT statement
   ```sql
   -- Change from:
   ('Admin Audit', 'admin@mindaudit.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', ...)
   
   -- To:
   ('Admin Audit', 'admin@mindaudit.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', ...)
   ```
   - Ensures future database setups have the correct hash
   - Prevents recurrence of this bug

## Testing Strategy

### Validation Approach

The testing strategy follows a two-phase approach: first, surface counterexamples that demonstrate the bug on unfixed code, then verify the fix works correctly and preserves existing behavior.

### Exploratory Bug Condition Checking

**Goal**: Surface counterexamples that demonstrate the bug BEFORE implementing the fix. Confirm or refute the root cause analysis. If we refute, we will need to re-hypothesize.

**Test Plan**: Write tests that attempt to authenticate with correct credentials (admin@mindaudit.com / admin123) and verify that authentication fails on the UNFIXED code. Also test with the incorrect password "123" to verify it incorrectly succeeds due to hash mismatch. Run these tests on the UNFIXED code to observe failures and understand the root cause.

**Test Cases**:
1. **Correct Credentials Test**: Attempt login with admin@mindaudit.com / admin123 (will fail on unfixed code - returns "Mot de passe incorrect")
2. **Wrong Password Test**: Attempt login with admin@mindaudit.com / 123 (may succeed on unfixed code due to hash match)
3. **Hash Verification Test**: Directly test `PasswordUtil.verifyPassword("admin123", "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3")` (will return false on unfixed code)
4. **Hash Generation Test**: Verify that `PasswordUtil.hashPassword("admin123")` produces "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9" (should pass - hash function is correct)

**Expected Counterexamples**:
- Authentication fails for correct password "admin123" because stored hash doesn't match
- Possible causes: incorrect hash in database, hash generation inconsistency, encoding issues

### Fix Checking

**Goal**: Verify that for all inputs where the bug condition holds, the fixed function produces the expected behavior.

**Pseudocode:**
```
FOR ALL input WHERE isBugCondition(input) DO
  result := AuthenticationService.login_fixed(input.email, input.password)
  ASSERT result == true
  ASSERT AuthenticationService.getCurrentUser() != null
  ASSERT AuthenticationService.getCurrentUser().getEmail() == input.email
END FOR
```

**Test Cases**:
1. **Admin Login Success**: Login with admin@mindaudit.com / admin123 should succeed
2. **Current User Set**: After successful login, getCurrentUser() should return the admin user object
3. **Dashboard Load**: After successful login, dashboard should load without errors
4. **Session State**: After successful login, isLoggedIn() should return true and isAdmin() should return true

### Preservation Checking

**Goal**: Verify that for all inputs where the bug condition does NOT hold, the fixed function produces the same result as the original function.

**Pseudocode:**
```
FOR ALL input WHERE NOT isBugCondition(input) DO
  ASSERT AuthenticationService.login_original(input.email, input.password) 
         == AuthenticationService.login_fixed(input.email, input.password)
END FOR
```

**Testing Approach**: Property-based testing is recommended for preservation checking because:
- It generates many test cases automatically across the input domain
- It catches edge cases that manual unit tests might miss
- It provides strong guarantees that behavior is unchanged for all non-buggy inputs

**Test Plan**: Observe behavior on UNFIXED code first for invalid inputs, then write property-based tests capturing that behavior.

**Test Cases**:
1. **Invalid Email Preservation**: Verify that invalid email formats still produce "Email invalide" error
2. **Wrong Password Preservation**: Verify that incorrect passwords still produce "Mot de passe incorrect" error
3. **Non-Existent User Preservation**: Verify that non-existent emails still produce "Utilisateur introuvable" error
4. **Disabled Account Preservation**: Verify that disabled accounts still produce "Le compte est désactivé" error
5. **Empty Fields Preservation**: Verify that empty fields still produce "Email et mot de passe requis" error
6. **Short Password Preservation**: Verify that passwords < 6 chars still produce "Mot de passe invalide" error
7. **Database Error Preservation**: Verify that database errors still produce appropriate error messages

### Unit Tests

- Test `PasswordUtil.hashPassword()` produces correct SHA-256 hashes
- Test `PasswordUtil.verifyPassword()` correctly compares hashes (no plaintext bypass)
- Test `AuthenticationService.login()` with correct credentials succeeds
- Test `AuthenticationService.login()` with incorrect credentials fails
- Test `AuthenticationService.login()` with invalid email format fails
- Test `AuthenticationService.login()` with empty fields fails
- Test `AuthenticationService.login()` with disabled account fails
- Test `AuthenticationService.login()` with non-existent user fails
- Test that debug bypass code is removed (no special cases for admin@mindaudit.com)

### Property-Based Tests

- Generate random valid email/password combinations and verify authentication behavior is consistent
- Generate random invalid email formats and verify they all produce "Email invalide" error
- Generate random short passwords (< 6 chars) and verify they all produce validation error
- Generate random user states (active/inactive) and verify disabled accounts are rejected
- Test that hash verification is deterministic (same input always produces same result)

### Integration Tests

- Test full login flow: enter credentials → click login → verify dashboard loads
- Test login failure flow: enter wrong password → verify error message → verify user stays on login screen
- Test validation flow: enter invalid email → verify error message appears immediately
- Test session management: login → verify isLoggedIn() → logout → verify isLoggedIn() is false
- Test role-based access: login as admin → verify isAdmin() returns true
- Test database connectivity: simulate database error → verify appropriate error message
