package chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTest.java
 *
 * Unit tests for the Message class, using the test data supplied in the
 * PROG5121 Part 2 brief.
 *
 * Test Case 1: recipient +27718693002, "Hi Mike, can you join us for dinner tonight?" (send)
 * Test Case 2: recipient 08575975889,  "Hi Keegan, did you receive the payment?"      (disregard)
 */
class MessageTest {

    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Every test starts with no sent messages and its own storage file.
        Message.resetSentMessages();
        Message.setStorageFile(tempDir.resolve("storedMessages.json"));
    }

    // ---------- Message length (assertEquals) ----------

    @Test
    void testMessageLength_success() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    void testMessageLength_exactly250CharactersIsAllowed() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    void testMessageLength_failure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    // ---------- Recipient cell number (assertEquals) ----------

    @Test
    void testRecipientCell_success() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    void testRecipientCell_failure() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // ---------- Message hash (assertEquals) ----------

    @Test
    void testMessageHash_testCase1() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void testMessageHash_exampleFromBrief() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, "Hi Mike, thanks!");
        assertEquals("00:0:HITHANKS", message.createMessageHash());
    }

    @Test
    void testMessageHash_remainingMessagesInALoop() {
        String[] ids = {"0012345678", "0087654321"};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {MESSAGE_1, MESSAGE_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], i, recipients[i], texts[i]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    // ---------- Message ID (assertEquals / assertTrue) ----------

    @Test
    void testMessageIDCreated() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message ID generated: " + message.getMessageID(),
                message.messageIDGeneratedMessage());
    }

    @Test
    void testMessageID_isTenDigits() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.getMessageID().matches("\\d{10}"));
        assertTrue(message.checkMessageID());
    }

    @Test
    void testMessageID_tooLongIsRejected() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, MESSAGE_1);
        assertFalse(message.checkMessageID());
    }

    // ---------- Send / discard / store (assertEquals) ----------

    @Test
    void testSentMessage_send() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(Message.SEND_MESSAGE));
    }

    @Test
    void testSentMessage_disregard() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(Message.DISREGARD_MESSAGE));
    }

    @Test
    void testSentMessage_store() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(Message.STORE_MESSAGE));
    }

    @Test
    void testSentMessage_invalidOption() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Invalid option; please choose 1, 2 or 3.", message.sentMessage(9));
    }

    // ---------- Total messages and printing ----------

    @Test
    void testReturnTotalMessages_afterSendingOneAndDisregardingOne() {
        Message first = new Message(0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message(1, RECIPIENT_2, MESSAGE_2);

        first.sentMessage(Message.SEND_MESSAGE);
        second.sentMessage(Message.DISREGARD_MESSAGE);

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void testPrintMessages_onlySentMessagesAreListed() {
        Message first = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message("0087654321", 1, RECIPIENT_2, MESSAGE_2);

        first.sentMessage(Message.SEND_MESSAGE);
        second.sentMessage(Message.DISREGARD_MESSAGE);

        assertEquals("Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: Hi Mike, can you join us for dinner tonight?",
                Message.printMessages());
    }

    @Test
    void testPrintMessages_noneSent() {
        assertEquals("No messages have been sent.", Message.printMessages());
    }

    // ---------- JSON storage ----------

    @Test
    void testStoreMessage_writesToJsonFile() throws IOException {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.sentMessage(Message.STORE_MESSAGE);

        String json = Files.readString(tempDir.resolve("storedMessages.json"));
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("00:0:HITONIGHT"));
        assertTrue(json.contains(MESSAGE_1));
    }

    @Test
    void testStoreMessage_keepsEarlierStoredMessages() {
        new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1).sentMessage(Message.STORE_MESSAGE);
        new Message("0087654321", 1, RECIPIENT_2, MESSAGE_2).sentMessage(Message.STORE_MESSAGE);

        List<Message> stored = Message.loadStoredMessages();
        assertEquals(2, stored.size());
        assertEquals(MESSAGE_1, stored.get(0).getMessageText());
        assertEquals(MESSAGE_2, stored.get(1).getMessageText());
    }
}
