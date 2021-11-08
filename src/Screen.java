import java.awt.Color;
import java.awt.Font;
import java.util.*;

public class Screen {
    public final static int WIDTH = 700;
    public final static int HEIGHT = 720;
    public final static double POLE_X = WIDTH / 4 - 50;
    public final static double POLE_Y = HEIGHT - 100;
    public final static double MAN_X = POLE_X + 100;
    public final static double MAN_Y = POLE_Y - 35;

    /**
     * Initializes the screen.
     */
    public static void initialize() {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        StdDraw.setPenColor(Color.white);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Displays home screen.
     */
    public static void homeScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 50, "HANGMAN");
        drawOptions(HEIGHT - 350,
                "(1) Start game",
                "(2) Custom word bank",
                "(0) Quit");
        StdDraw.show();
    }

    /**
     * Displays Hangman game screen.
     * @param g
     * @param response
     */
    public static void gameScreen(HangmanGame g, String response) {
        StdDraw.clear(Color.black);
        drawCategoryLabel(g.getCurrCat());
        drawPole();
        drawMan(g.getWrong());
        StdDraw.setPenRadius();
        drawWords(g.getBrokenAns(), g.getCorrectSet());
        drawWrongSet(g.getWrongSet());
        drawResponse(response);
        StdDraw.show();
    }

    /**
     * Shows the current category.
     * @param category
     */
    public static void drawCategoryLabel(String category) {
        drawString(WIDTH / 2, HEIGHT - 20, 15, Font.BOLD, "Category: " + category.toUpperCase());
        StdDraw.line(0, HEIGHT - 40, WIDTH, HEIGHT - 40);
    }

    /**
     * Draws the hangman.
     * @param wrong
     */
    public static void drawMan(int wrong) {
        StdDraw.setPenRadius(0.003);
        if (wrong >= 1) {
            StdDraw.circle(MAN_X, MAN_Y - 15, 15);
        }
        if (wrong >= 2) {
            StdDraw.line(MAN_X, MAN_Y - 30, MAN_X, MAN_Y - 75);
        }
        if (wrong >= 3) {
            StdDraw.line(MAN_X, MAN_Y - 45, MAN_X - 20, MAN_Y - 65);
        }
        if (wrong >= 4) {
            StdDraw.line(MAN_X, MAN_Y - 45, MAN_X + 20, MAN_Y - 65);
        }
        if (wrong >= 5) {
            StdDraw.line(MAN_X, MAN_Y - 75, MAN_X - 25, MAN_Y - 115);
        }
        if (wrong >= 6) {
            StdDraw.line(MAN_X, MAN_Y - 75, MAN_X + 25, MAN_Y - 115);
        }
    }

    /**
     * Draws the pole that hangs the man.
     */
    public static void drawPole() {
        StdDraw.setPenRadius(0.005);
        StdDraw.line(POLE_X + 100, POLE_Y, POLE_X + 100, POLE_Y - 35);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(POLE_X, POLE_Y, POLE_X + 100, POLE_Y);
        StdDraw.line(POLE_X, POLE_Y, POLE_X, POLE_Y - 200);
        StdDraw.line(POLE_X - 50, POLE_Y - 200, POLE_X + 50, POLE_Y - 200);
    }

    /**
     * Draws the underscores for the answer and the correctly guessed letters.
     * @param brokenAnswer
     * @param correctSet
     */
    public static void drawWords(ArrayList<String> brokenAnswer, HashSet<String> correctSet) {
        for (int i = 0; i < brokenAnswer.size(); i++) {
            String partialAns = brokenAnswer.get(i);
            int x = WIDTH / 2 - (partialAns.length() * 30) / 2;
            int y = HEIGHT - 450 - (40 * i);
            drawEachLine(partialAns, correctSet, x, y);
        }
    }

    /**
     * Draws the underscores for partial answer and the correctly guessed letters from left to right,
     * starting at coordinate (x, y)
     * @param partialAns
     * @param correctSet
     * @param x
     * @param y
     */
    public static void drawEachLine(String partialAns, HashSet<String> correctSet, int x, int y) {
        for (int i = 0; i < partialAns.length(); i++) {
            String character = Character.toString(partialAns.charAt(i));
            if (!character.equals(" ")) {
                StdDraw.line(x, y, x + 20, y);
                if (correctSet.contains(character)) {
                    drawString(x + 10, y + 15, 30, Font.PLAIN, character);
                }
            }
            x += 30;
        }
    }

    /**
     * Draws the response for the guessed letter.
     * @param word
     */
    public static void drawResponse(String word) {
        drawString(WIDTH / 2, 60, 15, Font.PLAIN, word);
    }

    /**
     * Draws incorrectly guessed letters.
     * @param wrongSet
     */
    public static void drawWrongSet(LinkedHashSet<String> wrongSet) {
        StdDraw.square(3 * WIDTH / 4, HEIGHT - 200, 100);
        int x = 3 * WIDTH / 4 - 80;
        int y = HEIGHT - 215 + 80;
        for (String character : wrongSet) {
            drawString(x, y, 20, Font.PLAIN, character);
            x += 25;
            if (x > 3 * WIDTH / 4 + 80) {
                x = 3 * WIDTH / 4 - 80;
                y -= 40;
            }
        }
    }

    /**
     * Displays reviving chance screen.
     */
    public static void getReviveScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "You lost :(" , "Would you like to win a reviving chance?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        StdDraw.show();
    }

    /**
     * Displays reviving game instruction screen.
     * @param time
     */
    public static void reviveIntroScreen(int time) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Answer the math question in " + time + " seconds.", "Good luck!");
        drawOptions(HEIGHT - 400, "(1) Start");
        StdDraw.show();
    }

    /**
     * Displays reviving game screen with the math question, remaining time, and the inputted answer.
     * @param operator
     * @param op1
     * @param op2
     * @param ans
     * @param time
     */
    public static void reviveGameScreen(String operator, int op1, int op2, String ans, int time) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Remaining Time: " + time);
        drawString(WIDTH / 2, HEIGHT - 350, 30, Font.BOLD, op1 + " " + operator + " " + op2 + " = " + ans);
        StdDraw.show();
    }

    /**
     * Displays successfully revived screen.
     */
    public static void reviveScreen() {
        StdDraw.clear(Color.black);
        drawString(WIDTH / 2, HEIGHT / 2, 30, Font.BOLD, "Congratulation! You are revived!");
        StdDraw.show();
    }

    /**
     * Displays game over screen.
     */
    public static void gameOverScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Game over.", "Return to home screen?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        StdDraw.show();
    }

    /**
     * Displays the screen for passing the round (or getting the answer).
     * If passed high score, then also displays a message.
     * @param wins
     * @param brokeHighScore
     */
    public static void passScreen(int wins, boolean brokeHighScore) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"You passed " + wins + " rounds!", "Continue playing?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        if (brokeHighScore) {
            drawString(WIDTH / 2, HEIGHT / 2 - 200, 20, Font.ITALIC, "Woo hoo! You broke your high score!");
        }
        StdDraw.show();
    }

    /**
     * Displays winning screen.
     */
    public static void winScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"You won!", "You answered all possible words correctly!", "Returning to home screen...");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    /**
     * Displays all categories.
     * @param categories
     */
    public static void categoryScreen(CategoryMap categories) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"Categories");
        if (categories.size() <= 5) {
            oneCol(categories);
        } else {
            twoCol(categories);
        }
        StdDraw.show();
    }

    /**
     * Lists categories in one column.
     * @param categories
     */
    private static void oneCol(CategoryMap categories) {
        listCategories(1, categories.size(), WIDTH / 2, categories);
    }

    /**
     * List categories in two columns.
     * @param categories
     */
    private static void twoCol(CategoryMap categories) {
        listCategories(1, 5, WIDTH / 4, categories);
        listCategories(6, categories.size(), 3 * WIDTH / 4, categories);
    }

    /**
     * List categories from start to end index from top to bottom, starting at width = x.
     * @param start
     * @param end
     * @param x
     * @param categories
     */
    private static void listCategories(int start, int end, int x, CategoryMap categories) {
        int y = HEIGHT - 350;
        for (int i = start; i <= end; i++) {
            drawString(x, y, 20, Font.PLAIN,"(" + i + ") " + categories.getCategory(i).toUpperCase());
            y -= 50;
        }
    }

    /**
     * Displays screen for no categories.
     */
    public static void noCategoryScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"Categories");
        int y = HEIGHT - 350;
        drawString(WIDTH / 2, y, 20, Font.PLAIN,"No categories available");
        drawString(WIDTH / 2, y - 50, 20, Font.PLAIN,"Returning to home screen...");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    /**
     * Displays custom word bank menu.
     */
    public static void customScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Custom Word Bank");
        drawOptions(HEIGHT - 350, "(1) Add", "(2) Delete", "(3) Modify");
        StdDraw.show();
    }

    /**
     * Displays add category menu.
     */
    public static void addCategoryScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Add Category");
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 20));
        drawOptions(HEIGHT - 350, "(1) Type", "(2) File");
        StdDraw.show();
    }

    /**
     * Displays screen for reaching maximum number of categories.
     */
    public static void maxCategoryScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Add Category");
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 20));
        int y = HEIGHT - 350;
        drawString(WIDTH / 2, y, 20, Font.PLAIN,"Maximum number of categories reached");
        drawString(WIDTH / 2, y - 50, 20, Font.PLAIN,"Returning to custom word bank menu...");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    /**
     * Displays screen for adding category via manual typing.
     * @param category
     * @param word
     */
    public static void strAddCatScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Add Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete");
        StdDraw.show();
    }

    /**
     * Displays screen for adding category via file.
     * @param category
     * @param fileName
     */
    public static void fileAddCatScreen(String category, String fileName) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Add Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "File Name: " + fileName);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete");
        StdDraw.show();
    }

    /**
     * Displays screen for deleting category.
     * @param category
     */
    public static void delCatScreen(String category) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Delete Category");
        drawOptions(HEIGHT - 300, "Category: " + category);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete");
        StdDraw.show();
    }

    /**
     * Displays modifying categories menu.
     */
    public static void modifyCatScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Modify Category");
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 20));
        drawOptions(HEIGHT - 350, "(1) Add", "(2) Remove");
        StdDraw.show();
    }

    /**
     * Displays screen for adding word to category.
     * @param category
     * @param word
     */
    public static void addWordScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Modify Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete");
        StdDraw.show();
    }

    /**
     * Displays screen for removing word from category.
     * @param category
     * @param word
     */
    public static void delWordScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Modify Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete");
        StdDraw.show();
    }

    /**
     * Displays error message.
     * @param message
     */
    public static void errorMessage(String message) {
        drawString(WIDTH / 2, HEIGHT - 600, 20, Font.ITALIC, message);
        StdDraw.show();
        StdDraw.pause(2000);
    }

    /**
     * Draws title in the middle at height = y with titleSize.
     * @param y
     * @param titleSize
     * @param title
     */
    public static void drawTitle(int y, int titleSize, String ...title) {
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, titleSize));
        drawString(WIDTH / 2, y, titleSize, Font.BOLD, title);
    }

    /**
     * Draws all options in the middle starting at height = y.
     * @param y
     * @param options
     */
    public static void drawOptions(int y, String ...options) {
        drawString(WIDTH / 2, y, 20, Font.PLAIN, options);
    }

    /**
     * Draws strings with style starting at coordinate (x, y).
     * @param x
     * @param y
     * @param size
     * @param style
     * @param strings
     */
    public static void drawString(int x, int y, int size, int style, String ...strings) {
        StdDraw.setFont(new Font("Helvetica", style, size));
        for (String string : strings) {
            StdDraw.text(x, y, string);
            y -= 50;
        }
    }
}
