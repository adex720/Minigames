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

    public void add(Task task, int delay) {
        TIMERS.add(new TimerObject(task, delay));
    }

    public void stop() {
        TIMERS.forEach(TimerObject::cancel);
    }

    private static class TimerObject {

        private final Timer timer;
        private final Task task;
        private final int delay;

        private TimerObject(Task task, int delay) {
            timer = new Timer();
            this.task = task;
            this.delay = delay;

            start();
        }

        private void start() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            }, delay, delay);
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
