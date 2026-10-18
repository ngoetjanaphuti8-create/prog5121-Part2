PROG5121 - Part 2: Sending Messages (QuickChat)
A console-only Java application that builds on the Part 1 registration and
login feature. Once a user has logged in successfully they can send, discard
or store messages in QuickChat.
What's included
`src/main/java/chatapp/Login.java` — the Part 1 registration and login logic.
`src/main/java/chatapp/Message.java` — a single message: ID, hash, validation,
send / disregard / store logic, and JSON storage.
`src/main/java/chatapp/Main.java` — the console app (login, welcome message,
menu loop and message-entry loop).
`src/test/java/chatapp/MessageTest.java` — unit tests for the Message class,
using the test data from the Part 2 brief.
`src/test/java/chatapp/LoginTest.java` — the Part 1 unit tests.
`.github/workflows/TestJava.yml` — GitHub Actions workflow that runs every
test automatically on each push.
`pom.xml` — Maven project file (JUnit 5 and Gson).
How the app works
The user registers and logs in (three attempts allowed). Messages can only
be sent after a successful login.
The app shows `Welcome to QuickChat.` and asks how many messages the user
wants to enter. Only that many messages can be entered.
A numeric menu runs until the user quits:
1) Send Messages — a `for` loop collects each message's recipient and text.
2) Show recently sent messages — shows `Coming Soon.`
3) Quit
For each message the user then chooses to send it, disregard it
(press 0 to delete) or store it to send later.
Sent messages are displayed as Message ID, Message Hash, Recipient, Message,
and the total number of messages sent is shown when the loop finishes.
Design notes
Message ID — a random ten-digit ID built one digit at a time and kept as a
`String`, so IDs that start with zero are not lost.
Message hash — the first two digits of the ID, a colon, the message number
(the loop counter, starting at 0), a colon, then the first and last words of the
message in capitals with punctuation removed, e.g. `00:0:HITONIGHT`.
Recipient check — reuses the Part 1 cell phone checker.
JSON storage — stored messages are written to `storedMessages.json` using
Gson. Messages already in the file are read first, so storing a new message
never overwrites earlier ones.
Unit tests — each message hash is tested in a loop, and the JSON tests use
a temporary file so the real storage file is never touched.
How to run it
You'll need a JDK (17+) and Maven installed, or just NetBeans/IntelliJ.
```bash
# Run the unit tests
mvn test

# Run the console app
mvn compile exec:java
```
In NetBeans, open this folder as an existing Maven project, right-click the
`Test Packages` to run the tests, and right-click `Main.java` > Run File to
start the app.
References
Google, "Gson User Guide". Available at: https://google.github.io/gson/UserGuide.html
(accessed October 2026). Used for converting messages to and from JSON.
Oracle, "Pattern (Java Platform SE 8)", Oracle Help Center. Available at:
https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html.
Used for the regular expressions in Part 1.
