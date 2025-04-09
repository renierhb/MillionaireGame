// MillionaireGame.java
import java.io.*;
import java.util.*;

public class MillionaireGame {
    private static final String QUESTION_FILE = "questions.txt";
    private static final int TOTAL_QUESTIONS = 10;
    private static final int[] PRIZES = {100, 500, 1000, 2000, 5000, 10000, 50000, 100000, 250000, 1000000};
    private static final Set<Integer> SAVE_STEPS = Set.of(2, 6); // index 2 = $1,000, index 6 = $50,000

    private List<Question> questions;
    private boolean usedFiftyFifty = false;
    private boolean usedPhoneAFriend = false;

    public static void main(String[] ignoredArgs) throws IOException {
        new MillionaireGame().start();
    }

    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);
        questions = loadQuestions();

        System.out.println("\n=============================================");
        System.out.println("💰 Welcome to Who Wants to Be a Millionaire! 💰");
        System.out.println("===============================================\n");

        System.out.print("Choose difficulty: easy (e), medium (m), or hard (h): ");
        String input = scanner.nextLine().trim().toLowerCase();
        String level;
        switch (input) {
            case "e", "easy" -> level = "easy";
            case "m", "medium" -> level = "medium";
            case "h", "hard" -> level = "hard";
            default -> {
                System.out.println("Invalid difficulty. Defaulting to 'easy'.");
                level = "easy";
            }
        }

        List<Question> gameQuestions = getQuestionsByLevel(level);
        Collections.shuffle(gameQuestions);
        gameQuestions = gameQuestions.subList(0, TOTAL_QUESTIONS);

        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            Question q = gameQuestions.get(i);
            System.out.println("\nQuestion " + (i + 1) + " for $" + PRIZES[i]);
            askQuestion(q);

            String choice;

            while (true) {
                choice = scanner.nextLine().trim().toUpperCase();

                if (choice.equals("5050") && !usedFiftyFifty) {
                    usedFiftyFifty = true;
                    showFiftyFifty(q);
                } else if (choice.equals("CALL") && !usedPhoneAFriend) {
                    usedPhoneAFriend = true;
                    showPhoneAFriend(q);
                } else if (choice.equals("EXIT")) {
                    System.out.println("You walk away with $" + (i == 0 ? 0 : PRIZES[i - 1]));
                    return;
                } else if (choice.matches("[ABCD]")) {
                    break; // valid answer selected
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, D, 5050, CALL, or EXIT:");
                }
            }

            if (choice.charAt(0) != q.getCorrectAnswer()) {
                System.out.println("Wrong answer! You leave with $0.");
                System.out.println("Would you like to start again or exit? (S/E):");
                while (true) {
                    String again = scanner.nextLine().trim().toLowerCase();
                    if (again.equals("s") || again.equals("start")) {
                        start(); // restart the game
                        return;
                    } else if (again.equals("e") || again.equals("exit")) {
                        System.out.println("Thanks for playing!");
                        return;
                    } else {
                        System.out.println("Invalid input. Please type 'start' (S) or 'exit' (E):");
                    }
                }
            }

            if (SAVE_STEPS.contains(i)) {
                System.out.println("You reached a safe point! You now have $" + PRIZES[i] +
                        ". If you keep going and get it wrong, you'll walk away with nothing.");
                System.out.println("Do you want to continue or exit? (C/E):");

                while (true) {
                    String decision = scanner.nextLine().trim().toLowerCase();
                    if (decision.equals("exit") || decision.equals("e")) {
                        System.out.println("You leave with $" + PRIZES[i]);
                        return;
                    } else if (decision.equals("continue") || decision.equals("c")) {
                        break; // Proceed with game
                    } else {
                        System.out.println("Invalid input. Please type 'continue', 'c', 'exit', or 'e':");
                    }
                }
            }
        }
        System.out.println("\n🎉🎉🎉 CONGRATULATIONS!!! 🎉🎉🎉");
        System.out.println("You are now a MILLIONAIRE!\n");

        System.out.println("""
                
                __________              .__                 ___ ___              ___.          \s
                \\______   \\ ____   ____ |__| ___________   /   |   \\   __________\\_ |__ _____  \s
                 |       _// __ \\ /    \\|  |/ __ \\_  __ \\ /    ~    \\_/ __ \\_  __ \\ __ \\\\__  \\ \s
                 |    |   \\  ___/|   |  \\  \\  ___/|  | \\/ \\    Y    /\\  ___/|  | \\/ \\_\\ \\/ __ \\_
                 |____|_  /\\___  >___|  /__|\\___  >__|     \\___|_  /  \\___  >__|  |___  (____  /
                        \\/     \\/     \\/        \\/               \\/       \\/          \\/     \\/\s
                
""");

        System.out.println("💵💵💵 YOU WON $1,000,000! 💵💵💵");
        System.out.println("Thank you for playing Who Wants to Be a Millionaire!\n");

    }

    private void askQuestion(Question q) {
        System.out.println("Q: " + q.getQuestion() + "\n");

        // Format as 2 per line: A/B and C/D
        String[] options = q.getOptions();
        System.out.printf("%-3s %-20s %-3s %s%n", "A:", options[0], "B:", options[1]);
        System.out.printf("%-3s %-20s %-3s %s%n", "C:", options[2], "D:", options[3]);

        if (!usedFiftyFifty) System.out.println("\nType '5050' to use 50/50 lifeline.");
        if (!usedPhoneAFriend) System.out.println("Type 'CALL' to phone a friend.");
        System.out.println("Or choose your answer (A/B/C/D) or type 'EXIT' to leave.");
    }

    private void showFiftyFifty(Question q) {
        System.out.println("50/50 activated!");
        List<Character> wrongAnswers = new ArrayList<>();
        char[] options = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            if (options[i] != q.getCorrectAnswer()) wrongAnswers.add(options[i]);
        }
        Collections.shuffle(wrongAnswers);
        wrongAnswers = wrongAnswers.subList(0, 2);
        System.out.println("Remaining options:");
        for (char opt : options) {
            if (opt == q.getCorrectAnswer() || !wrongAnswers.contains(opt)) {
                System.out.println(opt + ": " + q.getOption(opt));
            }
        }
    }

    private void showPhoneAFriend(Question q) {
        System.out.println("Calling your friend...");
        List<Character> possibleAnswers = new ArrayList<>();
        possibleAnswers.add(q.getCorrectAnswer());

        char[] options = {'A', 'B', 'C', 'D'};
        for (char opt : options) {
            if (opt != q.getCorrectAnswer()) {
                possibleAnswers.add(opt);
            }
        }
        Collections.shuffle(possibleAnswers);
        Character suggested = possibleAnswers.get(new Random().nextInt(2));
        System.out.println("Your friend suggests: " + suggested);
    }

    private List<Question> getQuestionsByLevel(String level) {
        List<Question> list = new ArrayList<>();
        for (Question q : questions) {
            if (q.getLevel().equalsIgnoreCase(level)) {
                list.add(q);
            }
        }
        return list;
    }

    private List<Question> loadQuestions() throws IOException {
        List<Question> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(QUESTION_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("LEVEL:")) {
                String level = line.split(":")[1].trim();
                String question = reader.readLine().split(":")[1].trim();
                String[] options = new String[4];
                for (int i = 0; i < 4; i++) {
                    options[i] = reader.readLine().split(":")[1].trim();
                }
                char answer = reader.readLine().split(":")[1].trim().charAt(0);
                reader.readLine(); // blank line
                result.add(new Question(level, question, options, answer));
            }
        }
        reader.close();
        return result;
    }
}

// Question.java
class Question {
    private final String level;
    private final String question;
    private final String[] options;
    private final char correctAnswer;

    public Question(String level, String question, String[] options, char correctAnswer) {
        this.level = level;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getLevel() { return level; }
    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public char getCorrectAnswer() { return correctAnswer; }

    public String getOption(char letter) {
        return switch (letter) {
            case 'A' -> options[0];
            case 'B' -> options[1];
            case 'C' -> options[2];
            case 'D' -> options[3];
            default -> "";
        };
    }
}
