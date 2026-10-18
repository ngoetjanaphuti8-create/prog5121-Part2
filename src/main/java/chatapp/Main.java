package chatapp;

import java.util.Scanner;

/**
 * Main.java
 *
 * Console entry point for PROG5121 Part 2 - QuickChat.
 *
 * The user registers and logs in (Part 1) and can only reach QuickChat once
 * they have logged in successfully. They then choose how many messages they
 * want to enter and use a numeric menu to send messages, see the "Coming
 * Soon" recent-messages feature, or quit.
 */
public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        registerNewUser(scanner, login);

        if (!logIn(scanner, login)) {
            System.out.println("Too many failed login attempts. Goodbye.");
            scanner.close();
            return;
        }

        runQuickChat(scanner);
        scanner.close();
    }

    // ---------- Part 1: registration and login ----------

    private static void registerNewUser(Scanner scanner, Login login) {
        System.out.println("=== Registration ===");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        String username;
        do {
            System.out.print("Enter a username (must contain an underscore and be no more than 5 characters): ");
            username = scanner.nextLine();
            if (!login.checkUserName(username)) {
                System.out.println("Username is not correctly formatted; please ensure that your "
                        + "username contains an underscore and is no more than five characters in length.");
            }
        } while (!login.checkUserName(username));

        String password;
        do {
            System.out.print("Enter a password (min 8 characters, 1 capital letter, 1 number, 1 special character): ");
            password = scanner.nextLine();
            if (!login.checkPasswordComplexity(password)) {
                System.out.println("Password is not correctly formatted; please ensure that the "
                        + "password contains at least eight characters, a capital letter, a number, "
                        + "and a special character.");
            }
        } while (!login.checkPasswordComplexity(password));

        String cellphoneNumber;
        do {
            System.out.print("Enter your cell phone number (with international code, e.g. +27838968976): ");
            cellphoneNumber = scanner.nextLine();
            if (!login.checkCellPhoneNumber(cellphoneNumber)) {
                System.out.println("Cell number is incorrectly formatted or does not contain "
                        + "international code; please correct the number and try again.");
            }
        } while (!login.checkCellPhoneNumber(cellphoneNumber));

        System.out.println(login.registerUser(firstName, lastName, username, password, cellphoneNumber));
    }

    // Gives the user a limited number of attempts to log in.
    private static boolean logIn(Scanner scanner, Login login) {
        System.out.println("\n=== Please log in ===");
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            boolean loggedIn = login.loginUser(username, password);
            System.out.println(login.returnLoginStatus(loggedIn));
            if (loggedIn) {
                return true;
            }
        }
        return false;
    }

    // ---------- Part 2: QuickChat ----------

    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        int numMessages = readNumber(scanner, "How many messages do you wish to enter? ", 1, 1000);
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int choice = readNumber(scanner, "Choose an option: ", 1, 3);

            switch (choice) {
                case 1:
                    if (messagesEntered >= numMessages) {
                        System.out.println("You have already entered all " + numMessages + " message(s).");
                    } else {
                        messagesEntered = sendMessages(scanner, numMessages, messagesEntered);
                        System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                default:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
            }
        }
    }

    // Lets the user enter the messages they said they wanted to send. The for
    // loop counter is the message number used in the message hash.
    private static int sendMessages(Scanner scanner, int numMessages, int startIndex) {
        for (int i = startIndex; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            String recipient = readValidRecipient(scanner);
            String text = readValidMessage(scanner);

            Message message = new Message(i, recipient, text);
            System.out.println(message.messageIDGeneratedMessage());

            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int option = readNumber(scanner, "Choose an option: ", 1, 3);

            System.out.println(message.sentMessage(option));

            if (option == Message.DISREGARD_MESSAGE) {
                confirmDeletion(scanner);
            } else if (option == Message.SEND_MESSAGE) {
                System.out.println("\n" + message.getDetails());
            }
        }
        return numMessages;
    }

    private static String readValidRecipient(Scanner scanner) {
        while (true) {
            System.out.print("Enter the recipient's cell number (with international code): ");
            String recipient = scanner.nextLine().trim();
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.equals("Cell phone number successfully captured.")) {
                return recipient;
            }
        }
    }

    private static String readValidMessage(Scanner scanner) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("A message cannot be empty.");
                continue;
            }
            String result = Message.checkMessageLength(text);
            if (result.equals("Message ready to send.")) {
                System.out.println(result);
                return text;
            }
            System.out.println("Please enter a message of less than 250 characters.");
            System.out.println(result);
        }
    }

    // The user has to enter 0 to confirm that the message must be deleted.
    private static void confirmDeletion(Scanner scanner) {
        String input;
        do {
            System.out.print("> ");
            input = scanner.nextLine().trim();
        } while (!input.equals("0"));
        System.out.println("Message deleted.");
    }

    // Keeps asking until the user enters a whole number between min and max.
    private static int readNumber(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Not a number - fall through and ask again.
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }
}
