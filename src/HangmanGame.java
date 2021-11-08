import java.io.File;
import java.util.*;

public class HangmanGame {
    private int wrong;
    private String currCat;
    private ArrayList<String> currList;
    private ArrayList<String> brokenAnswer;
    private HashSet<String> correctSet;
    private LinkedHashSet<String> wrongSet;
    private int uniqueLetters;
    private int wins;
    private boolean gameOver;
    private int remainTime = 0;
    private static CategoryMap categories;
    private static Random rand = new Random();
    public static int highScore = 0;
    final private static File HIGH_SCORE = new File("highScore.txt");

    public HangmanGame() {
        wrong = 0;
        currCat = "";
        currList = new ArrayList<>();
        brokenAnswer = new ArrayList<>();
        correctSet = new HashSet<>();
        wrongSet = new LinkedHashSet<>();
        uniqueLetters = 0;
        wins = 0;
        gameOver = false;
        highScore = Integer.parseInt(FileUtil.readFile(HIGH_SCORE).get(0));
    }

    //Home Screen
    /**
     * Displays the home screen with 3 possible actions: start game, custom word bank, or quit.
     */
    public static void showHome() {
        while (true) {
            Screen.homeScreen();
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1':
                        HangmanGame newGame = new HangmanGame();
                        if (categories.size() == 0) {
                            Screen.noCategoryScreen();
                        } else {
                            newGame.chooseCategory();
                        }
                        break;
                    case '2':
                        customWordBank();
                        break;
                    case '0':
                        quit();
                }
            }
        }
    }

    //Custom Word Bank
    /**
     * Displays the custom word bank screen with 3 possible actions: add, delete, or modify category.
     */
    public static void customWordBank() {
        while (true) {
            Screen.customScreen();
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //add
                        categories.addCategory();
                        break;
                    case '2': //delete
                        categories.deleteCategory();
                        break;
                    case '3': //modify
                        categories.modifyCategory();
                        break;
                    case '0':
                        return;
                }
            }
        }
    }

    //Game Setup

    /**
     * Sets up the files, CategoryMap, and the screen, and then runs the game.
     */
    public static void run() {
        setUpFiles();
        setCategoryMap();
        Screen.initialize();
        showHome();
    }

    /**
     * Creates the wordbank folder and the highscore file.
     */
    public static void setUpFiles() {
        FileUtil.makeDir("wordbank");
        setUpHighScoreFile();
    }

    /**
     * Creates the highscore file and initialize it with "0" if does not exist.
     */
    private static void setUpHighScoreFile() {
        if (!HIGH_SCORE.exists()) {
            FileUtil.writeFile(HIGH_SCORE, "0");
        }
    }

    /**
     * Creates the categories file if does not exist.
     * A CategoryMap object is serialized into the file.
     */
    private static void setCategoryMap() {
        File f = new File("categories");
        if (!f.exists()) {
            HangmanGame.categories = new CategoryMap(rand);
        } else {
            categories = FileUtil.readObject(f, CategoryMap.class);
        }
    }

    /**
     * Displays the screen with categories listed.
     * If the user selects a category, then the game starts.
     */
    public void chooseCategory() {
        Screen.categoryScreen(categories);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character in = StdDraw.nextKeyTyped();
                if (Character.isDigit(in)) {
                    int num = Character.getNumericValue(in);
                    if (num == 0) {
                        break;
                    }
                    if (num <= categories.size()) {
                        currCat = categories.getCategory(num);
                        currList = categories.getWords(currCat);
                        String answer = chooseAnswer();
                        makeAnswerArray(answer);
                        uniqueLetters = getUniqueLetterNum(answer);
                        game();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Chooses an answer randomly from the wordbank and removes it from being chosen again during this session of the game.
     */
    public String chooseAnswer() {
        int i = rand.nextInt(currList.size());
        String answer = currList.get(i);
        currList.remove(i);
        return answer;
    }

    /**
     * Split answer into a list of strings that can be displayed onto the screen.
     * If the screen is 10 characters wide, the answer "ice cream cake" is split into "ice cream" and "cake".
     * When displayed on screen, "ice cream" is one line and "cake" is another line.
     * @param answer
     */
    public void makeAnswerArray(String answer) {
        String[] splitAnswer = answer.split(" ");
        int start = 0;
        int end = 0;
        int totalLen = 0;
        int wordLen = 0;
        while (end < splitAnswer.length) {
            wordLen = (splitAnswer[end].length() + 1) * 30;
            totalLen += wordLen;
            if (totalLen > Screen.WIDTH) {
                addToArray(splitAnswer, start, end - 1);
                start = end;
                totalLen = wordLen;
            }
            end += 1;
        }
        if (start < splitAnswer.length && end - 1 < splitAnswer.length) {
            addToArray(splitAnswer, start, end - 1);
        }
    }

    //Basic Game Logic

    /**
     * Displays the game screen and plays the Hangman game.
     */
    public void game() {
        Screen.gameScreen(this, "");
        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                String character = Character.toString(StdDraw.nextKeyTyped()).toUpperCase();
                if (isAlpha(character)) {
                    String response = getResult(character);
                    Screen.gameScreen(this, response);
                    checkGameOver();
                }
            }
        }
    }

    /**
     * Returns how many unique letters are in answer.
     * @param answer
     */
    public int getUniqueLetterNum(String answer) {
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i < answer.length(); i++) {
            Character c = answer.charAt(i);
            if (c != ' ') {
                set.add(c);
            }
        }
        return set.size();
    }

    /**
     * Combines strings from start to end index with spaces and adds it to the array brokenAnswer,
     * which is used to display answer onto the screen.
     * @param splitAnswer
     * @param start
     * @param end
     */
    public void addToArray(String[] splitAnswer, int start, int end) {
        String joined = "";
        for (int i = start; i <= end; i++) {
            if (i == end) {
                joined += splitAnswer[i].toUpperCase();
            } else {
                joined += splitAnswer[i].toUpperCase() + " ";
            }
        }
        brokenAnswer.add(joined);
    }

    /**
     * If the user guessed six wrong letters, then prompt reviving chance.
     * If the user got the answer, then prompt next round.
     */
    public void checkGameOver() {
        if (wrong == 6) {
            gameOver = true;
            StdDraw.pause(500);
            getRevive();
        } else if (correctSet.size() == uniqueLetters) {
            gameOver = true;
            wins += 1;
            Screen.passScreen(wins, checkHighScore());
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                    switch (key) {
                        case '1': //yes
                            if (hasNextWord()) {
                                newRound();
                            } else {
                                Screen.winScreen();
                            }
                            return;
                        case '2': //no
                            gameOver = true;
                            gameOver();
                            return;
                    }
                }
            }
        }
    }

    /**
     * Returns true if the word bank still has unchosen answers remaining during this session of the game.
     * Else, return false.
     */
    private boolean hasNextWord() {
        return currList.size() > 0;
    }

    /**
     * Start a new round (ie. choose a different answer in the same category).
     */
    public void newRound() {
        correctSet = new HashSet<>();
        wrongSet = new LinkedHashSet<>();
        brokenAnswer = new ArrayList<>();
        String answer = chooseAnswer();
        makeAnswerArray(answer);
        uniqueLetters = getUniqueLetterNum(answer);
        gameOver = false;
        game();
    }

    /**
     * Returns true if passed high score. Else, return false.
     */
    public boolean checkHighScore() {
        if (wins > highScore) {
            highScore = wins;
            FileUtil.writeFile(HIGH_SCORE, highScore + "");
            return true;
        }
        return false;
    }

    /**
     * Displays game over screen with two possible actions: return to home screen or close program
     */
    public void gameOver() {
        Screen.gameOverScreen();
        wins = 0;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1': //yes
                        showHome();
                        return;
                    case '2': //no
                        quit();
                }
            }
        }
    }

    /**
     * Returns the appropriate message for the letter the user chose.
     * @param character
     */
    public String getResult(String character) {
        if (correctSet.contains(character) || wrongSet.contains(character)) {
            return "You already used this letter. Try another one!";
        } else if (contain(character)) {
            correctSet.add(character);
            return "Wow! Keep it up!";
        } else {
            wrongSet.add(character);
            wrong += 1;
            return "Uh oh! Try another letter.";
        }
    }

    /**
     * Saves the categories and closes the program.
     */
    public static void quit() {
        FileUtil.save(categories, "categories");
        System.exit(0);
    }

    //Revive

    /**
     * Displays the revive chance screen with two possible actions: play the reviving game or end the game.
     */
    public void getRevive() {
        Screen.getReviveScreen();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1': //yes
                        reviveGame();
                        return;
                    case '2': //no
                        gameOver = true;
                        gameOver();
                        return;
                }
            }
        }
    }

    /**
     * Randomly chooses a math question for the reviving game and prompts the user to start the game.
     */
    public void reviveGame() {
        int time = rand.nextInt(21) + 10;
        remainTime = time + 1;
        Screen.reviveIntroScreen(time);
        int choice = rand.nextInt(2);
        int op1;
        int op2;
        String operator;
        if (choice == 0) { //add
            op1 = rand.nextInt(1000);
            op2 = rand.nextInt(1000);
            operator = "+";
        } else { //multiply
            op1 = rand.nextInt(100);
            op2 = rand.nextInt(10);
            operator = "x";
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == '1') { //start
                    playRevGame(operator, op1, op2);
                    return;
                }
            }
        }
    }

    /**
     * Displays the reviving game screen with the math question.
     * If the user answers the math question correctly within the time limit, then reset the lives.
     * Else, game over.
     * @param operator
     * @param op1
     * @param op2
     */
    public void playRevGame(String operator, int op1, int op2) {
        String ans = "";
        int correctAns = getCorrectAns(operator, op1, op2);
        CountDownTimer timer = new CountDownTimer(this);
        Screen.reviveGameScreen(operator, op1, op2, ans, remainTime);
        while (remainTime > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                Character character = StdDraw.nextKeyTyped();
                if (Character.isDigit(character)) {
                    ans += Character.toString(character);
                    if (Integer.parseInt(ans) == correctAns) {
                        revived();
                        return;
                    } else if (ans.length() >= String.valueOf(correctAns).length()) {
                        ans = "";
                    }
                }
            } else {
                Screen.reviveGameScreen(operator, op1, op2, ans, remainTime);
            }
        }
        gameOver();
    }

    /**
     * Get the correct answer of (op1 operator op2).
     * There are only two possible operators: + or *.
     * @param operator
     * @param op1
     * @param op2
     */
    public static int getCorrectAns(String operator, int op1, int op2) {
        if (operator.equals("+")) {
            return op1 + op2;
        } else {
            return op1 * op2;
        }
    }

    /**
     * Displays the screen for successfully revived and resets lives.
     */
    public void revived() {
        Screen.reviveScreen();
        wrong = 0;
        gameOver = false;
        StdDraw.pause(1200);
        Screen.gameScreen(this, "");
    }

    //Other

    /**
     * Returns true if character is in answer.
     * @param character
     */
    public boolean contain(String character) {
        for (String part : brokenAnswer) {
            if (part.contains(character)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if word only contains alphabets.
     * @param word
     */
    public static boolean isAlpha(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isAlphabetic(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Decrements remaining time for reviving game.
     */
    public void countDown() {
        remainTime -= 1;
    }

    /**
     * Gets the remaining time for reviving game.
     */
    public int getRemainTime() {
        return remainTime;
    }

    /**
     * Gets the number of incorrectly guessed letters (or how many lives lost)
     */
    public int getWrong() {
        return wrong;
    }

    /**
     * Gets the set of correctly guessed letters
     */
    public HashSet<String> getCorrectSet() {
        return correctSet;
    }

    /**
     * Gets the set of incorrectly guessed letters
     */
    public LinkedHashSet<String> getWrongSet() {
        return wrongSet;
    }

    /**
     * Gets a list that stores the splitted answer string.
     */
    public ArrayList<String> getBrokenAns() {
        return brokenAnswer;
    }

    /**
     * Gets current category.
     */
    public String getCurrCat() {
        return currCat;
    }
}
