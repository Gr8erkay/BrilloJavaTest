package com.gr8erkay.brillojavatest1;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserValidationApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Part A: Perform individual field validations
        boolean validationPassed = true;
        String failedValidations = "";

        System.out.print("Username (use minimum of 4 characters) : ");
        String username = scanner.nextLine();
        if (!isValidUsername(username)) {
            validationPassed = false;
            failedValidations += "Username: Invalid format (minimum 4 characters)\n";
        }

        System.out.print("Email (e.g: user@example.com) : ");
        String email = scanner.nextLine();
        if (!isValidEmail(email)) {
            validationPassed = false;
            failedValidations += "Email: Invalid format\n";
        }

        System.out.print("Password (Password should contain minimum 8 characters, 1 uppercase, 1 special character, 1 number) : ");
        String password = scanner.nextLine();
        if (!isValidPassword(password)) {
            validationPassed = false;
            failedValidations += "Password: Invalid format (minimum 8 characters, 1 uppercase, 1 special character, 1 number)\n";
        }

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dobString = scanner.nextLine();
        if (!isValidDateOfBirth(dobString)) {
            validationPassed = false;
            failedValidations += "Date of Birth: Invalid format or age is less than 16\n";
        }

        if (validationPassed) {
            System.out.println("All validations passed!");
        } else {
            System.out.println("Validation failed for the following fields:\n" + failedValidations);
        }

        // Part B: Perform concurrent field validations
        boolean concurrentValidationPassed = validateFieldsConcurrently(username, email, password, dobString);
        System.out.println("Concurrent Validation: " + (concurrentValidationPassed ? "True" : "Failed"));

        // Part C: Generate and return a signed JWT
        if (concurrentValidationPassed) {
            String token = generateToken(username);
            System.out.println("Generated Token: " + token);
            // Part D: Verify the token
            boolean verificationPassed = verifyToken(token);
            System.out.println("Token Verification: " + (verificationPassed ? "Passed" : "Failed"));
        }
    }

    // Individual field validations

    private static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username is empty.");
            return false;
        }
        if (username.length() < 4) {
            System.out.println("Username should have a minimum of 4 characters.");
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            System.out.println("Email is empty.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Email has an invalid format.");
            return false;
        }
        return true;
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            System.out.println("Password is empty.");
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.*[0-9]).{8,}$";
        if (!Pattern.matches(passwordPattern, password)) {
            System.out.println("Password has an invalid format. It should have a minimum of 8 characters, at least 1 uppercase letter, 1 special character, and 1 number.");
            return false;
        }
        return true;
    }

    private static boolean isValidDateOfBirth(String dobString) {
        if (dobString == null || dobString.isEmpty()) {
            System.out.println("Date of Birth is empty.");
            return false;
        }
        try {
            LocalDate dob = LocalDate.parse(dobString);
            LocalDate today = LocalDate.now();
            LocalDate minAgeDate = today.minusYears(16);
            if (dob.isAfter(today)) {
                System.out.println("Date of Birth is in the future.");
                return false;
            }
            if (dob.isAfter(minAgeDate)) {
                System.out.println("Age must be at least 16 years old.");
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            System.out.println("Date of Birth has an invalid format. Use the format: YYYY-MM-DD");
            return false;
        }
    }

    // Concurrent field validations

    private static boolean validateFieldsConcurrently(String username, String email, String password, String dobString) {
        boolean isValidUsername = isValidUsername(username);
        boolean isValidEmail = isValidEmail(email);
        boolean isValidPassword = isValidPassword(password);
        boolean isValidDateOfBirth = isValidDateOfBirth(dobString);

        System.out.println(isValidUsername && isValidEmail && isValidPassword && isValidDateOfBirth);
        if (isValidUsername && isValidEmail && isValidPassword && isValidDateOfBirth) {
            return true;
        }
        return false;
    }


    // Token generation and verification

    // Set the secret key used to sign the JWT token
    private static String SECRET_KEY = "5970337336763979244226452948404D635166546A576E5A7234743777217A25";

    private static String generateToken(String username) {
        // Set the expiration time for the token (e.g., 1 hour from now)
        long expirationTimeMillis = System.currentTimeMillis() + 3600000;

        // Generate the JWT token
        String token = Jwts.builder()
                .claim("username", username) // Include the username as a claim
                .setExpiration(new Date(expirationTimeMillis))
                .serializeToJsonWith(new JacksonSerializer<>())
                .signWith(getSignInKey())
                .compact();

        return token;
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .deserializeJsonWith(new JacksonDeserializer<>())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
