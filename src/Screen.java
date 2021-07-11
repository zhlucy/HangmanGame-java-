import java.awt.Color;
import java.awt.Font;
import java.util.*;

public class Screen {
    public final static int WIDTH = 700;
    public final static int HEIGHT = 700;
    public final static double POLE_X = WIDTH / 4 - 50;
    public final static double POLE_Y = HEIGHT - 100;
    public final static double MAN_X = POLE_X + 100;
    public final static double MAN_Y = POLE_Y - 35;

    public static void initialize() {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        StdDraw.setPenColor(Color.white);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
    }

    public static void homeScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 50, "HANGMAN");
        drawOptions(HEIGHT - 350,
                "(1) Start game",
                "(2) Custom word bank",
                "(3) Two player mode",
                "(0) Quit");
        StdDraw.show();
    }

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

    public static void drawCategoryLabel(String category) {
        drawString(WIDTH / 2, HEIGHT - 20, 15, Font.BOLD, "Category: " + category.toUpperCase());
        StdDraw.line(0, HEIGHT - 40, WIDTH, HEIGHT - 40);
    }

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

    public static void drawPole() {
        StdDraw.setPenRadius(0.005);
        StdDraw.line(POLE_X + 100, POLE_Y, POLE_X + 100, POLE_Y - 35);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(POLE_X, POLE_Y, POLE_X + 100, POLE_Y);
        StdDraw.line(POLE_X, POLE_Y, POLE_X, POLE_Y - 200);
        StdDraw.line(POLE_X - 50, POLE_Y - 200, POLE_X + 50, POLE_Y - 200);
    }

    public static void drawWords(ArrayList<String> brokenAnswer, HashSet<String> correctSet) {
        for (int i = 0; i < brokenAnswer.size(); i++) {
            String partialAns = brokenAnswer.get(i);
            int x = WIDTH / 2 - (partialAns.length() * 30) / 2;
            int y = HEIGHT - 450 - (40 * i);
            drawEachLine(partialAns, correctSet, x, y);
        }
    }

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

    public static void drawResponse(String word) {
        drawString(WIDTH / 2, HEIGHT / 5, 20, Font.PLAIN, word);
    }

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

    public static void getReviveScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "You lost :(" , "Would you like to win a reviving chance?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        StdDraw.show();
    }

    public static void reviveIntroScreen(int time) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Answer the math question in " + time + " seconds.", "Good luck!");
        drawOptions(HEIGHT - 400, "(1) Start");
        StdDraw.show();
    }

    public static void reviveGameScreen(String operator, int op1, int op2, String ans, int time) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Remaining Time: " + time);
        drawString(WIDTH / 2, HEIGHT - 350, 30, Font.BOLD, op1 + " " + operator + " " + op2 + " = " + ans);
        StdDraw.show();
    }

    public static void reviveScreen() {
        StdDraw.clear(Color.black);
        drawString(WIDTH / 2, HEIGHT / 2, 30, Font.BOLD, "Congratulation! You are revived!");
        StdDraw.show();
    }

    public static void gameOverScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Game over.", "Return to home screen?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        StdDraw.show();
    }

    public static void passScreen(int wins, boolean brokeHighScore) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"You passed " + wins + " rounds!", "Continue playing?");
        drawOptions(HEIGHT - 400, "(1) Yes", "(2) No");
        if (brokeHighScore) {
            drawString(WIDTH / 2, HEIGHT / 2 - 200, 20, Font.ITALIC, "Woo hoo! You broke your high score!");
        }
        StdDraw.show();
    }

    public static void winScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"You won!", "You answered all possible words correctly!", "Returning to home screen...");
        StdDraw.show();
        StdDraw.pause(3000);
    }

    public static void categoryScreen(CategoryMap categories) {
        //probably need to set restriction on how many categories will show up
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30,"Categories");
        int y = HEIGHT - 350;
        for (int i = 0; i < categories.size(); i++) {
            drawString(WIDTH / 2, y, 20, Font.PLAIN,"(" + i + ") " + categories.getCategory(i).toUpperCase());
            y -= 50;
        }
        StdDraw.show();
    }

    public static void customScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Custom Word Bank");
        drawOptions(HEIGHT - 350, "(1) Add", "(2) Delete", "(3) Modify");
        StdDraw.show();
    }

    public static void addCategoryScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Add Category");
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 20));
        drawOptions(HEIGHT - 350, "(1) Type", "(2) File");
        StdDraw.show();
    }

    public static void strAddCatScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Add Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete", "(0) Return to home screen");
        StdDraw.show();
    }

    public static void fileAddCatScreen(String category, String fileName) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Add Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "File Name: " + fileName);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete", "(0) Return to home screen");
        StdDraw.show();
    }

    public static void delCatScreen(String category) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Delete Category");
        drawOptions(HEIGHT - 300, "Category: " + category);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete", "(0) Return to home screen");
        StdDraw.show();
    }

    public static void modifyCatScreen() {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 250, 30, "Modify Category");
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 20));
        drawOptions(HEIGHT - 350, "(1) Add", "(2) Remove");
        StdDraw.show();
    }

    public static void addWordScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Modify Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete", "(0) Return to home screen");
        StdDraw.show();
    }

    public static void delWordScreen(String category, String word) {
        StdDraw.clear(Color.black);
        drawTitle(HEIGHT - 200, 30, "Modify Category");
        drawOptions(HEIGHT - 300, "Category: " + category, "Word: " + word);
        drawOptions(HEIGHT - 450,"(1) Enter", "(2) Delete", "(0) Return to home screen");
        StdDraw.show();
    }

    public static void drawTitle(int y, int titleSize, String ...title) {
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, titleSize));
        drawString(WIDTH / 2, y, titleSize, Font.BOLD, title);
    }

    public static void drawOptions(int y, String ...options) {
        drawString(WIDTH / 2, y, 20, Font.PLAIN, options);
    }

    public static void drawString(int x, int y, int size, int style, String ...strings) {
        StdDraw.setFont(new Font("Helvetica", style, size));
        for (String string : strings) {
            StdDraw.text(x, y, string);
            y -= 50;
        }
    }
}
