package chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginTest.java
 *
 * Unit tests for the Login class, using the exact test data supplied
 * in the PROG5121 Part 1 assignment brief.
 */
class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    // ---------- checkUserName ----------

    @Test
    void testUsernameCorrectlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void testUsernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    // ---------- checkPasswordComplexity ----------

    @Test
    void testPasswordMeetsComplexityRequirements() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void testPasswordDoesNotMeetComplexityRequirements() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // ---------- checkCellPhoneNumber ----------

    @Test
    void testCellPhoneNumberCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void testCellPhoneNumberIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // ---------- registerUser (assertEquals on returned messages) ----------

    @Test
    void testRegisterUser_usernameIncorrect() {
        String result = login.registerUser("Kyle", "Bezuidenhout", "kyle!!!!!!!",
                "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted; please ensure that your "
                + "username contains an underscore and is no more than five "
                + "characters in length.", result);
    }

    @Test
    void testRegisterUser_passwordIncorrect() {
        String result = login.registerUser("Kyle", "Bezuidenhout", "kyl_1",
                "password", "+27838968976");
        assertEquals("Password is not correctly formatted; please ensure that the "
                + "password contains at least eight characters, a capital "
                + "letter, a number, and a special character.", result);
    }

    @Test
    void testRegisterUser_cellPhoneIncorrect() {
        String result = login.registerUser("Kyle", "Bezuidenhout", "kyl_1",
                "Ch&&sec@ke99!", "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain "
                + "international code; please correct the number and try again.", result);
    }

    @Test
    void testRegisterUser_allDetailsCorrect() {
        String result = login.registerUser("Kyle", "Bezuidenhout", "kyl_1",
                "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username successfully captured. Password successfully captured. "
                + "Cell number successfully captured. Registration successful, "
                + "you can now log in.", result);
    }

    // ---------- loginUser (assertTrue / assertFalse) ----------

    @Test
    void testLoginUser_successful() {
        login.registerUser("Kyle", "Bezuidenhout", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void testLoginUser_failed() {
        login.registerUser("Kyle", "Bezuidenhout", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
    }

    // ---------- returnLoginStatus (assertEquals on returned messages) ----------

    @Test
    void testReturnLoginStatus_successful() {
        login.registerUser("Kyle", "Bezuidenhout", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean status = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Bezuidenhout it is great to see you again.",
                login.returnLoginStatus(status));
    }

    @Test
    void testReturnLoginStatus_failed() {
        login.registerUser("Kyle", "Bezuidenhout", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean status = login.loginUser("kyl_1", "wrongPassword1!");
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(status));
    }
}
