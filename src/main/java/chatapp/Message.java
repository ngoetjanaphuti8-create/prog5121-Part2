package chatapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Message.java
 *
 * PROG5121 - Part 2: Sending Messages.
 *
 * Represents a single QuickChat message. It generates the message ID and
 * message hash, validates the recipient and message length, lets the user
 * send / disregard / store the message, and keeps track of every message
 * sent while the program is running.
 *
 * JSON storage: the approach of converting Java objects to JSON with Gson's
 * toJson() and reading them back with fromJson() and a TypeToken follows
 * the examples in:
 * Google, "Gson User Guide". Available at: https://google.github.io/gson/UserGuide.html
 * (accessed October 2026).
 */
public class Message {

    // Menu options offered once a message has been completed.
    public static final int SEND_MESSAGE = 1;
    public static final int DISREGARD_MESSAGE = 2;
    public static final int STORE_MESSAGE = 3;

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MESSAGE_ID_LENGTH = 10;

    // Part 1's cell phone checker is reused to validate the recipient.
    private static final Login CELL_VALIDATOR = new Login();

    // Gson converts the Message objects to and from JSON.
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    // Every message sent while the program is running.
    private static final List<Message> sentMessages = new ArrayList<>();

    // The JSON file that stored messages are written to.
    private static Path storageFile = Paths.get("storedMessages.json");

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /**
     * Creates a message with an automatically generated, random ten-digit ID.
     *
     * @param messageNumber the message's number (the loop counter, starting at 0)
     * @param recipient     the recipient's cell phone number
     * @param messageText   the message text
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /**
     * Creates a message with a specific ID (used for testing, where the
     * hash has to be predictable).
     *
     * @param messageID     the ten-digit message ID
     * @param messageNumber the message's number (the loop counter, starting at 0)
     * @param recipient     the recipient's cell phone number
     * @param messageText   the message text
     */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = buildHash(messageID, messageNumber, messageText);
    }

    /**
     * Generates a random ten-digit message ID, built one digit at a time.
     * A leading zero is allowed, so the ID is kept as a String.
     *
     * @return a ten-digit message ID
     */
    public static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(ThreadLocalRandom.current().nextInt(10));
        }
        return id.toString();
    }

    /**
     * Checks that the message ID is not more than ten characters long.
     *
     * @return true if the message ID is valid, false otherwise
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MESSAGE_ID_LENGTH;
    }

    /**
     * Confirms that the message ID has been created.
     *
     * @return "Message ID generated: " followed by the message ID
     */
    public String messageIDGeneratedMessage() {
        return "Message ID generated: " + messageID;
    }

    /**
     * Checks that a recipient cell number contains an international code
     * and is correctly formatted. This reuses the Part 1 cell phone checker.
     *
     * @param recipient the cell number to check
     * @return a success or failure message
     */
    public static String checkRecipientCell(String recipient) {
        if (CELL_VALIDATOR.checkCellPhoneNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /**
     * Checks this message's recipient cell number.
     *
     * @return a success or failure message
     */
    public String checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    /**
     * Checks that a message does not exceed 250 characters.
     *
     * @param text the message text
     * @return a success message, or a failure message saying how many
     *         characters over the limit the message is
     */
    public static String checkMessageLength(String text) {
        int length = (text == null) ? 0 : text.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int excess = length - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /**
     * Checks this message's length.
     *
     * @return a success or failure message
     */
    public String checkMessageLength() {
        return checkMessageLength(messageText);
    }

    /**
     * Creates and returns the message hash: the first two digits of the
     * message ID, a colon, the message number, a colon, then the first and
     * last words of the message, all in capital letters (e.g. 00:0:HITONIGHT).
     *
     * @return the message hash
     */
    public String createMessageHash() {
        return buildHash(messageID, messageNumber, messageText);
    }

    // Builds the hash using string manipulation (substring, split, replaceAll).
    private static String buildHash(String id, int number, String text) {
        String prefix = (id.length() >= 2) ? id.substring(0, 2) : id;
        String[] words = text.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);
        return (prefix + ":" + number + ":" + firstWord + lastWord).toUpperCase();
    }

    // Removes punctuation (such as the "?" in "tonight?") so only letters and digits remain.
    private static String cleanWord(String word) {
        return word.replaceAll("[^\\p{L}\\p{N}]", "");
    }

    /**
     * Applies the option the user chose for this message.
     *
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the message to show the user
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case SEND_MESSAGE:
                sentMessages.add(this);
                return "Message successfully sent.";
            case DISREGARD_MESSAGE:
                return "Press 0 to delete the message.";
            case STORE_MESSAGE:
                return storeMessage();
            default:
                return "Invalid option; please choose 1, 2 or 3.";
        }
    }

    /**
     * Stores this message in the JSON file. Messages already in the file are
     * read first, so storing a new message never overwrites earlier ones.
     *
     * @return a success message, or an error message if the file could not be written
     */
    public String storeMessage() {
        List<Message> stored = loadStoredMessages();
        stored.add(this);
        try (Writer writer = Files.newBufferedWriter(storageFile, StandardCharsets.UTF_8)) {
            GSON.toJson(stored, writer);
            return "Message successfully stored.";
        } catch (IOException e) {
            return "Message could not be stored: " + e.getMessage();
        }
    }

    /**
     * Reads the stored messages back from the JSON file.
     *
     * @return the stored messages (an empty list if there are none yet)
     */
    public static List<Message> loadStoredMessages() {
        if (!Files.exists(storageFile)) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Message>>() { }.getType();
        try (Reader reader = Files.newBufferedReader(storageFile, StandardCharsets.UTF_8)) {
            List<Message> loaded = GSON.fromJson(reader, listType);
            return (loaded == null) ? new ArrayList<>() : loaded;
        } catch (IOException | JsonParseException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Returns this message's full details in the required order:
     * Message ID, Message Hash, Recipient, Message.
     *
     * @return the message details
     */
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    /**
     * Returns every message sent while the program has been running.
     *
     * @return the details of all sent messages, or a note if none have been sent
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder all = new StringBuilder();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (i > 0) {
                all.append("\n\n");
            }
            all.append(sentMessages.get(i).getDetails());
        }
        return all.toString();
    }

    /**
     * Returns the total number of messages sent while the program has been running.
     *
     * @return the number of sent messages
     */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // Package-private helpers so the unit tests start from a clean state.
    static void resetSentMessages() {
        sentMessages.clear();
    }

    static void setStorageFile(Path file) {
        storageFile = file;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }
}
