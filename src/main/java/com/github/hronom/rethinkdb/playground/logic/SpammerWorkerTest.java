package com.github.hronom.rethinkdb.playground.logic;

import com.github.hronom.rethinkdb.playground.logic.rethinkdb.ChangesListener;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammer;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerStatus;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerStatusDao;

public class SpammerWorkerTest {
    public static void main(String[] args) throws InterruptedException {
        ConsoleSpammerStatusDao consoleSpammerStatusDao = new ConsoleSpammerStatusDao();

        ConsoleSpammerDao consoleSpammerDao = new ConsoleSpammerDao();
        consoleSpammerDao.subscribe(new ChangesListener<ConsoleSpammer>() {
            @Override
            public void initializingState() {

            }

            @Override
            public void readyState() {

            }

            @Override
            public void initial(ConsoleSpammer object) {
                ConsoleSpammerStatus consoleSpammerStatus = new ConsoleSpammerStatus();
                consoleSpammerStatus.id = object.id;
                consoleSpammerStatus.groupId = object.groupId;
                consoleSpammerStatus.status = "accepted";
                consoleSpammerStatusDao.save(consoleSpammerStatus);
            }

            @Override
            public void add(ConsoleSpammer object) {
                ConsoleSpammerStatus consoleSpammerStatus = new ConsoleSpammerStatus();
                consoleSpammerStatus.id = object.id;
                consoleSpammerStatus.groupId = object.groupId;
                consoleSpammerStatus.status = "accepted";
                consoleSpammerStatusDao.save(consoleSpammerStatus);
            }

            @Override
            public void remove(ConsoleSpammer object) {

            }

            @Override
            public void change(
                ConsoleSpammer oldObject, ConsoleSpammer newObject
            ) {

            }
        });
    }
}

