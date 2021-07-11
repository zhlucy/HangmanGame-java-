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

    public static void showHome() {
        while (true) {
            Screen.homeScreen();
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1':
                        HangmanGame newGame = new HangmanGame();
                        if (categories.size() == 0) {
                            System.out.println("No categories"); //add a screen for that
                        } else {
                            newGame.chooseCategory();
                            newGame.game();
                        }
                        break;
                    case '2':
                        customWordBank();
                        break;
                    case '3':
                        break;
                    case '0':
                        quit();
                }
            }
        }
    }

    //Custom Word Bank

    public static void customWordBank() {
        //allow them to go back to home screen
        //all of them need to show action is successful or not
        Screen.customScreen();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //add
                        categories.addCategory();
                        return;
                    case '2': //delete
                        categories.deleteCategory();
                        return;
                    case '3': //modify
                        categories.modifyCategory();
                        return;
                }
            }
        }
    }

    //Game Setup

    public static void run() {
        setUpFiles(); //must come before categorymap
        setCategoryMap();
        Screen.initialize();
        showHome();
    }

    public static void setUpFiles() {
        FileUtil.makeDir("wordbank");
        setUpHighScoreFile();
        //maybe figure out a way to store default word banks online?
    }

    private static void setUpHighScoreFile() {
        if (!HIGH_SCORE.exists()) {
            FileUtil.writeFile(HIGH_SCORE, "0");
        }
    }

    private static void setCategoryMap() {
        File f = new File("categories");
        if (!f.exists()) {
            HangmanGame.categories = new CategoryMap(rand);
        } else {
            categories = FileUtil.readObject(f, CategoryMap.class);
        }
    }

    public void chooseCategory() {
        Screen.categoryScreen(categories);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character in = StdDraw.nextKeyTyped();
                if (Character.isDigit(in)) {
                    int num = Character.getNumericValue(in);
                    if (num < categories.size()) {
                        currCat = categories.getCategory(num);
                        currList = categories.getWords(currCat);
                        chooseAnswer();
                        break;
                    }
                }
            }
        }
    }

    public void chooseAnswer() {
        int i = rand.nextInt(currList.size());
        String answer = currList.get(i);
        currList.remove(i);
        makeAnswerArray(answer);
        uniqueLetters = getUniqueLetterNum(answer);
    }

    public void makeAnswerArray(String answer) {
        //input can have phrases, but no words more than 20 letters, and cannot have punctuations or numbers
        String[] splitAnswer = answer.split(" ");
        int start = 0;
        int end = 0;
        int len = 0;
        while (end < splitAnswer.length) {
            len += (splitAnswer[end].length() + 1) * 30;
            if (len > Screen.WIDTH) {
                addToArray(splitAnswer, start, end - 1);
                start = end;
                len = 0;
            }
            end += 1;
        }
        if (start < splitAnswer.length && end - 1 < splitAnswer.length) {
            addToArray(splitAnswer, start, end - 1);
        }
    }

    //Basic Game Logic

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

    private boolean hasNextWord() {
        return currList.size() > 0;
    }

    public void newRound() {
        correctSet = new HashSet<>();
        wrongSet = new LinkedHashSet<>();
        brokenAnswer = new ArrayList<>();
        chooseAnswer();
        gameOver = false;
        game();
    }

    public boolean checkHighScore() {
        if (wins > highScore) {
            highScore = wins;
            FileUtil.writeFile(HIGH_SCORE, highScore + "");
            return true;
        }
        return false;
    }

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

    public static void quit() {
        FileUtil.save(categories, "categories");
        System.exit(0);
    }

    //Revive

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
                    playRevGame(operator, op1, op2, time);
                    return;
                }
            }
        }
    }

    public void playRevGame(String operator, int op1, int op2, int time) {
        String ans = "";
        int correctAns = getCorrectAns(operator, op1, op2);
        CountDownTimer timer = new CountDownTimer(time, this); // maybe clean it up
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

    public static int getCorrectAns(String operator, int op1, int op2) {
        if (operator.equals("+")) {
            return op1 + op2;
        } else {
            return op1 * op2;
        }
    }

    public void revived() {
        Screen.reviveScreen();
        wrong = 0;
        gameOver = false;
        StdDraw.pause(1200);
        Screen.gameScreen(this, "");
    }

    //Other

    public boolean contain(String character) {
        for (String part : brokenAnswer) {
            if (part.contains(character)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlpha(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isAlphabetic(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void countDown() {
        remainTime -= 1;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public int getWrong() {
        return wrong;
    }

    public HashSet<String> getCorrectSet() {
        return correctSet;
    }

    public LinkedHashSet<String> getWrongSet() {
        return wrongSet;
    }

    public ArrayList<String> getBrokenAns() {
        return brokenAnswer;
    }

    public String getCurrCat() {
        return currCat;
    }
}
