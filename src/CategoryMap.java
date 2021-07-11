import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CategoryMap implements Serializable {
    private HashMap<String, List<String>> categories;
    private ArrayList<String> names;

    public CategoryMap(Random r) {
        categories = new HashMap<>();
        names = new ArrayList<>();
        //Add default sports category (maybe not)
        //add("SPORTS", "SPORTS.txt");
    }

    // Category Custom Word Bank
    public void deleteCategory() {
        String category = "";
        while (true) {
            Screen.delCatScreen(category);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1': //enter
                        if (category.length() > 0) { //think abt this
                            delete(category);
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

    private void delete(String category) {
        if (!categories.containsKey(category)) {
            System.out.println("Not an existing category.");
        } else {
            categories.remove(category);
            names.remove(category);
        }
    }

    public void addCategory() {
        Screen.addCategoryScreen();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //type
                        strAddCategory();
                        return;
                    case '2': //file
                        fileAddCategory();
                        return;
                }
            }
        }
    }

    private void fileAddCategory() {
        // file must exist inside Hangman/wordbank Folder, only txt supported, also when user enter exclude .txt, case sensitive filename
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
                            if (categories.containsKey(category)) {
                                System.out.println("Existing category.");
                                category = "";
                            } else {
                                categoryEntered = true;
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
                            fileName += key.toString().toUpperCase();
                        }
                }
            }
        }
    }

    private boolean addFromFile(String category, File f) {
        if (f.exists()) {
            List<String> words = FileUtil.readFile(f);
            if (words.size() == 0 || !goodWordBank(words)) {
                System.out.println("Invalid word bank."); //change
                return false;
            } else {
                categories.put(category, words);
                names.add(category);
                f.delete();
                return true;
            }
        } else {
            System.out.println("File does not exist."); //change later
            return false;
        }
    }

    private void strAddCategory() {
        String category = "";
        String word = "";
        ArrayList<String> words = new ArrayList<>();
        boolean categoryEntered = false;
        while (true) {
            Screen.strAddCatScreen(category, word);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1':
                        if (!categoryEntered && category.length() > 0) {
                            if (categories.containsKey(category)) {
                                System.out.println("Existing category.");
                                category = "";
                            } else {
                                categoryEntered = true;
                            }
                        } else if (word.length() > 0) {
                            addWord(words, word);
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
                        if (categoryEntered && words.size() > 0) {
                            categories.put(category, words);
                            names.add(category);
                        }
                        return;
                    default:
                        if (Character.isAlphabetic(key) || key == ' ') {
                            if (!categoryEntered) {
                                category += key.toString();
                            } else {
                                word += key.toString();
                            }
                            Screen.strAddCatScreen(category, word);
                        }
                }
            }
        }
    }

    public void modifyCategory() {
        Screen.modifyCatScreen();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //type
                        addWords();
                        return;
                    case '2': //file
                        delWords();
                        return;
                }
            }
        }
    }

    public void addWords() {
        String category = "";
        String word = "";
        boolean categoryEntered = false;
        while (true) {
            Screen.addWordScreen(category, word);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1':
                        if (!categoryEntered && category.length() > 0) {
                            if (categories.containsKey(category)) {
                                categoryEntered = true;
                            } else {
                                category = "";
                            }
                        } else if (word.length() > 0) {
                            addWord(categories.get(category), word);
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
                            Screen.addWordScreen(category, word);
                        }
                }
            }
        }
    }

    //is there a way to combine addWords and delWords (actually all of them are pretty similar in structure)
    public void delWords() {
        String category = "";
        String word = "";
        boolean categoryEntered = false;
        while (true) {
            Screen.delWordScreen(category, word);
            if (StdDraw.hasNextKeyTyped()) {
                Character key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case '1':
                        if (!categoryEntered && category.length() > 0) {
                            if (categories.containsKey(category)) {
                                categoryEntered = true;
                            } else {
                                category = "";
                            }
                        } else if (word.length() > 0) {
                            delWord(categories.get(category), word);
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
                            Screen.delWordScreen(category, word);
                        }
                }
            }
        }
    }

    private void delWord(List<String> lst, String word) {
        lst.remove(word);
    }

    private void addWord(List<String> lst, String word) {
        if (!lst.contains(word) && validWord(word)) {
            lst.add(word);
        }
    }

    private static boolean goodWordBank(List<String> words) {
        for (String word : words) {
            if (!validWord(word)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validWord(String word) {
        //input can have phrases, but no words more than 20 letters, and cannot have punctuations or numbers
        if (word.length() == 0) {
            return false;
        }

        for (String ind : word.split(" ")) {
            if (ind.length() > 20 || !HangmanGame.isAlpha(ind)) {
                return false;
            }
        }
        return true;
    }

    public String getCategory(int i) {
        return names.get(i);
    }

    public ArrayList<String> getWords(String name) {
        return new ArrayList<>(categories.get(name));
    }

    public int size() {
        return categories.size();
    }

}
