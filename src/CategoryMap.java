import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class CategoryMap implements Serializable {
    private HashMap<String, List<String>> categories;
    private ArrayList<String> names;

    public CategoryMap(Random r) {
        categories = new HashMap<>();
        names = new ArrayList<>();
        //Add default sports category
        File f = new File(FileUtil.join("wordbank", "SPORTS.txt"));
        addFromFile("SPORTS", f);
    }

    /**
     * Displays screen for delete category and delete category after user presses "1"
     */
    public void deleteCategory() {
        String category = "";
        while (true) {
            Screen.delCatScreen(category);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1': //enter
                        if (category.length() > 0) {
                            remove(category);
                        }
                        category = "";
                        break;
                    case '2': //delete
                        category = "";
                        break;
                    case '0': //home screen
                        return;
                    default:
                        if (Character.isAlphabetic(key) || key == ' ') {
                            category += key.toString();
                        }
                }
            }
        }
    }

    /**
     * Displays add category screen with two options: add via manual type or via file.
     */
    public void addCategory() {
        while (true) {
            Screen.addCategoryScreen();
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                if (categories.size() == 9) {
                    Screen.maxCategoryScreen();
                    return;
                }
                switch (key) {
                    case '1': //type
                        strAddCategory();
                        break;
                    case '2': //file
                        fileAddCategory();
                        break;
                    case '0':
                        return;
                }
            }
        }
    }

    /**
     * Adds category via file
     */
    private void fileAddCategory() {
        String category = "";
        String fileName = "";
        boolean categoryEntered = false;
        while (true) {
            Screen.fileAddCatScreen(category, fileName);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //enter
                        if (!categoryEntered && category.length() > 0) {
                            if (validCat(category)) {
                                categoryEntered = true;
                            } else {
                                category = "";
                            }
                        } else if (fileName.length() > 0){
                            File f = new File(FileUtil.join("wordbank", fileName));
                            if (addFromFile(category, f)) {
                                category = "";
                                categoryEntered = false;
                            }
                            fileName = "";
                        }
                        break;
                    case '2': //delete
                        if (!categoryEntered) {
                            category = "";
                        } else {
                            fileName = "";
                        }
                        break;
                    case '0': //home screen
                        return;
                    default:
                        if (!categoryEntered) {
                            if (Character.isAlphabetic(key) || key == ' ') {
                                category += key.toString().toUpperCase();
                            }
                        } else {
                            fileName += key.toString();
                        }
                }
            }
        }
    }

    /**
     * Adds words from file to category.
     * @param category
     * @param f
     * @return
     */
    private boolean addFromFile(String category, File f) {
        if (f.exists()) {
            List<String> words = FileUtil.readFile(f);
            if (words.size() == 0 || !goodWordBank(words)) {
                Screen.errorMessage("Invalid word bank");
                return false;
            } else {
                add(category, words);
                f.delete();
                return true;
            }
        } else {
            Screen.errorMessage("File does not exist.");
            return false;
        }
    }

    /**
     * Adds category via manual type.
     */
    private void strAddCategory() {
        String category = "";
        String word = "";
        boolean categoryEntered = false;
        while (true) {
            Screen.strAddCatScreen(category, word);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1':
                        if (!categoryEntered && category.length() > 0) {
                            if (validCat(category)) {
                                categoryEntered = true;
                            } else {
                                category = "";
                            }
                        } else if (word.length() > 0) {
                            addWord(category, word);
                            word = "";
                        }
                        break;
                    case '2':
                        if (!categoryEntered) {
                            category = "";
                        } else {
                            word = "";
                        }
                        break;
                    case '0':
                        return;
                    default:
                        if (Character.isAlphabetic(key) || key == ' ') {
                            if (!categoryEntered) {
                                category += key.toString();
                            } else {
                                word += key.toString();
                            }
                        }
                }
            }
        }
    }

    /**
     * Returns true if category is at most 20 characters and does not exist yet.
     * @param category
     */
    private boolean validCat(String category) {
        if (category.length() > 20) {
            Screen.errorMessage("Category name is over 20 characters.");
            return false;
        }
        else if (categories.containsKey(category)) {
            Screen.errorMessage("Existing category.");
            return false;
        }
        return true;
    }

    /**
     * Displays menu for modifying category: add words or delete words.
     */
    public void modifyCategory() {
        while (true) {
            Screen.modifyCatScreen();
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //type
                        addWords();
                        break;
                    case '2': //file
                        delWords();
                        break;
                    case '0':
                        return;
                }
            }
        }
    }

    /**
     * Add words to category.
     */
    public void addWords() {
        modifyWords(true, false);
    }

    /**
     * Delete words from category
     */
    private void delWords() {
        modifyWords(false, true);
    }

    /**
     * Serves as a template for modifying words. If add is true, then use this to add words to category. If del is true,
     * then use this to delete words.
     */
    public void modifyWords(boolean add, boolean del) {
        BiConsumer<String, String> displayScreen;
        BiConsumer<String, String> modify;
        if (add) {
            displayScreen = Screen::addWordScreen;
            modify = this::addWord;
        } else {
            displayScreen = Screen::delWordScreen;
            modify = this::delWord;
        }
        String category = "";
        String word = "";
        boolean categoryEntered = false;
        while (true) {
            displayScreen.accept(category, word);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1':
                        if (!categoryEntered && category.length() > 0) {
                            if (categories.containsKey(category)) {
                                categoryEntered = true;
                            } else {
                                Screen.errorMessage("Not an existing category");
                                category = "";
                            }
                        } else if (word.length() > 0) {
                            modify.accept(category, word);
                            word = "";
                        }
                        break;
                    case '2':
                        if (!categoryEntered) {
                            category = "";
                        } else {
                            word = "";
                        }
                        break;
                    case '0':
                        return;
                    default:
                        if (Character.isAlphabetic(key) || key == ' ') {
                            if (!categoryEntered) {
                                category += key.toString();
                            } else {
                                word += key.toString();
                            }
                        }
                }
            }
        }
    }

    /**
     * Deletes word from category.
     * @param category
     * @param word
     */
    private void delWord(String category, String word) {
        List<String> lst = categories.get(category);
        if (!lst.remove(word)) {
            Screen.errorMessage("Not an existing word");
        }
        if (lst.size() == 0) {
            remove(category);
            Screen.errorMessage("Category removed because it contains no words");
        }
    }

    /**
     * Adds word to list.
     */
    private void addWord(String category, String word) {
        if (!categories.containsKey(category)) {
            add(category, new ArrayList<>());
        }

        List<String> lst = categories.get(category);

        if (!lst.contains(word)) {
            if (validWord(word)) {
                lst.add(word);
            } else {
                Screen.errorMessage("Invalid word");
            }
        }
    }

    /**
     * Returns true if all words in word bank are valid.
     * @param words
     * @return
     */
    private static boolean goodWordBank(List<String> words) {
        for (String word : words) {
            if (!validWord(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if string is at most 105 characters, contains only alphabets, and each word is at most 20 characters.
     * @param string
     * @return
     */
    private static boolean validWord(String string) {
        if (string.length() == 0 || string.length() > 105) {
            return false;
        }
        String[] words = string.split(" ");
        if (words.length == 0) {
            return false;
        }
        for (String ind : words) {
            if (ind.length() > 20 || !HangmanGame.isAlpha(ind)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns category name at index i.
     * @param i
     */
    public String getCategory(int i) {
        return names.get(i - 1);
    }

    /**
     * Returns the list of words for category.
     * @param category
     * @return
     */
    public ArrayList<String> getWords(String category) {
        return new ArrayList<>(categories.get(category));
    }

    /**
     * Returns number of categories.
     * @return
     */
    public int size() {
        return categories.size();
    }

    /**
     * Adds category with its word list.
     * @param category
     * @param words
     */
    public void add(String category, List<String> words) {
        categories.put(category, words);
        names.add(category);
    }

    /**
     * Remove category.
     * @param category
     */
    public void remove(String category) {
        if (!categories.containsKey(category)) {
            Screen.errorMessage("Not an existing category");
        } else {
            categories.remove(category);
            names.remove(category);
        }
    }
}
