package io.github.adex720.minigames.gameplay.manager.timer;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.Manager;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager extends Manager {

    private final Set<TimerObject> TIMERS;

    public TimerManager(MinigamesBot bot) {
        super(bot, "timer-manager");
        TIMERS = new HashSet<>();
    }

    public void add(Task task, int delay, boolean repeat) {
        TIMERS.add(new TimerObject(task, delay, repeat));
    }

    public void stop() {
        TIMERS.forEach(TimerObject::cancel);
    }

    private static class TimerObject {

        private final Timer timer;
        private final Task task;
        private final int delay;
        private final boolean repeat;

        private TimerObject(Task task, int delay, boolean repeat) {
            timer = new Timer();
            this.task = task;
            this.delay = delay;
            this.repeat = repeat;

            start();
        }

        private void start() {
            if (repeat) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        task.run();
                    }
                }, delay, delay);
            } else {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        task.run();
                    }
                }, delay);
            }
        }

        public void cancel() {
            timer.cancel();
        }
    }


    @FunctionalInterface
    public interface Task {
        void run();
    }

}
