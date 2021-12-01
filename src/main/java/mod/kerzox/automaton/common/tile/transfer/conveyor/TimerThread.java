package mod.kerzox.automaton.common.tile.transfer.conveyor;

import java.util.Timer;
import java.util.TimerTask;

class TimerThread {
    Timer timer;

    public TimerThread(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000); // schedule the task
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("You have a notification!");
            timer.cancel(); //Terminate the timer thread
        }
    }
}