package chatapp;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * PROG5121 - Part 1: Registration and Login feature.
 *
 * This class is responsible for validating a new user's details on
 * registration (username, password, South African cell phone number),
 * registering the user, and then verifying their credentials on login.
 *
 * Regular expressions are used to validate the username, password and
 * cell phone number, as required by the assignment brief. General
 * reference for using regular expressions in Java:
 * Oracle, "Pattern (Java Platform SE 8)", Oracle Help Center.
 * Available at: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 */
public class Login {

    // Regex: username must contain an underscore and be no more than 5 characters in total.
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.{1,5}$).*_.*$");

    // Regex: password must be at least 8 characters, contain at least one
    // capital letter, one number and one special character.
    private static final Pattern PASSWORD_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");

    // Regex: South African cell number - starts with a "+" and country code,
    // e.g. +27838968976.
    private static final Pattern CELLPHONE_PATTERN = Pattern.compile("^\\+\\d{2}\\d{9}$");

    // Stored details of the currently registered user.
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellphoneNumber;
    private boolean userIsRegistered = false;

    /**
     * Checks that the username contains an underscore and is no more
     * than five characters long.
     *
     * @param username the username to validate
     * @return true if the username is correctly formatted, false otherwise
     */
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Checks that the password is at least eight characters long and
     * contains a capital letter, a number, and a special character.
     *
     * @param password the password to validate
     * @return true if the password meets the complexity rules, false otherwise
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        boolean longEnough = password.length() >= 8;
        boolean hasUppercase = PASSWORD_UPPERCASE.matcher(password).matches();
        boolean hasDigit = PASSWORD_DIGIT.matcher(password).matches();
        boolean hasSpecialChar = PASSWORD_SPECIAL_CHAR.matcher(password).matches();
        return longEnough && hasUppercase && hasDigit && hasSpecialChar;
    }

    /**
     * Checks that the cell phone number contains the international
     * country code followed by the number.
     *
     * @param cellphoneNumber the cell phone number to validate
     * @return true if correctly formatted, false otherwise
     */
    public boolean checkCellPhoneNumber(String cellphoneNumber) {
        if (cellphoneNumber == null) {
            return false;
        }
        return CELLPHONE_PATTERN.matcher(cellphoneNumber).matches();
    }

    /**
     * Registers a new user, validating each field in turn. The first
     * validation to fail determines the message that is returned.
     * If every check passes, the user's details are stored and a
     * success message is returned.
     *
     * NOTE: the brief's login message ("Welcome <first name>, <last
     * name>...") needs a name to greet, so first and last name are
     * captured here at registration alongside username/password/cell
     * number. Confirm this matches what your lecturer expects.
     *
     * @param firstName       user's first name
     * @param lastName        user's last name
     * @param username        chosen username
     * @param password        chosen password
     * @param cellphoneNumber South African cell phone number
     * @return a message describing the outcome of registration
     */
    public String registerUser(String firstName, String lastName, String username,
                                String password, String cellphoneNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your "
                    + "username contains an underscore and is no more than five "
                    + "characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the "
                    + "password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellphoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain "
                    + "international code; please correct the number and try again.";
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphoneNumber = cellphoneNumber;
        this.userIsRegistered = true;

        return "Username successfully captured. Password successfully captured. "
                + "Cell number successfully captured. Registration successful, "
                + "you can now log in.";
    }

    /**
     * Verifies that the supplied username and password match the
     * details of the currently registered user.
     *
     * @param username the username entered at login
     * @param password the password entered at login
     * @return true if the credentials match a registered user, false otherwise
     */
    public boolean loginUser(String username, String password) {
        if (!userIsRegistered) {
            return false;
        }
        return this.username != null && this.username.equals(username)
                && this.password != null && this.password.equals(password);
    }

    /**
     * Returns the appropriate message for a login attempt.
     *
     * @param loginStatus the result of {@link #loginUser(String, String)}
     * @return a welcome message if login succeeded, or an error message otherwise
     */
    public String returnLoginStatus(boolean loginStatus) {
        if (loginStatus) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Getters - useful for the console app and for tests.
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public boolean isUserRegistered() {
        return userIsRegistered;
    }
}
