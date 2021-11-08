import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {
    Timer timer;
    HangmanGame game;

    public CountDownTimer(HangmanGame g) {
        game = g;
        timer = new Timer();
        timer.schedule(new DisplayRemainSec(g), 0, 1000);
    }

    private class DisplayRemainSec extends TimerTask {
        HangmanGame game;

        public DisplayRemainSec(HangmanGame g) {
            game = g;
        }
        public void run() {
            game.countDown();
            if (game.getRemainTime() == 0) {
                timer.cancel();
            }
        }
    }
}
