package com.github.hronom.rethinkdb.playground.logic.bo;

import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ConsoleSpammerBo {
    public String id;
    public String spamWord;

    private final ConsoleSpammerDao consoleSpammerDao;

    private final Timer timer = new Timer();
    private final TimerTask timerTask;

    public ConsoleSpammerBo(ConsoleSpammerDao consoleSpammerDao) {
        this.consoleSpammerDao = consoleSpammerDao;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis() + " " + spamWord);
            }
        };
        timer.schedule(timerTask, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));
    }

    public void load() {

    }

    public void disable() {
        timerTask.cancel();
    }
}
