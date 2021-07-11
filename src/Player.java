public class Player {
    private String answer;
    private int score;

    public Player() {
        answer = "";
        score = 0;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswerFor(Player other) {
        String ans = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character key = StdDraw.nextKeyTyped();
                switch (key) {
                    case '1': //enter
                        other.setAnswer(ans);
                        return;
                    case '2': //delete
                        ans = "";
                        break;
                    default:
                        if (Character.isAlphabetic(key)) {
                            ans += key;
                        }
                }
            }
        }
    }

    public void incScore() {
        score += 1;
    }
}
