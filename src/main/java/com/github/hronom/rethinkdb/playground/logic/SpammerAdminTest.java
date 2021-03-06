package com.github.hronom.rethinkdb.playground.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hronom.rethinkdb.playground.logic.bo.ConsoleSpammerBo;
import com.github.hronom.rethinkdb.playground.logic.bo.SpamProjectBo;
import com.github.hronom.rethinkdb.playground.logic.bo.SpammersGroupBo;
import com.github.hronom.rethinkdb.playground.logic.rethinkdb.ChangesListener;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammer;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerStatus;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerStatusDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpamProject;
import com.github.hronom.rethinkdb.playground.logic.dao.SpamProjectDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroup;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroupDao;

import java.util.concurrent.TimeUnit;

public class SpammerAdminTest {
    public static void main(String[] args) throws InterruptedException {
        ObjectMapper debugOm = new ObjectMapper();

        SpamProjectDao spamProjectDao = new SpamProjectDao();
        spamProjectDao.subscribe(new ChangesListener<SpamProject>() {
            @Override
            public void initializingState() {

            }

            @Override
            public void readyState() {

            }

            @Override
            public void initial(SpamProject object) {

            }

            @Override
            public void add(SpamProject object) {

            }

            @Override
            public void remove(SpamProject object) {

            }

            @Override
            public void change(
                SpamProject oldObject, SpamProject newObject
            ) {

            }
        });
        {
            SpamProject spamProject = new SpamProject();
            spamProject.id = "1";
            spamProject.projectName = "test name 1";
            spamProjectDao.save(spamProject);
        }
        {
            SpamProject spamProject = new SpamProject();
            spamProject.id = "2";
            spamProject.projectName = "test name 2";
            spamProjectDao.save(spamProject);
        }

        SpammersGroupDao spammersGroupDao = new SpammersGroupDao();
        {
            SpammersGroup spammersGroup = new SpammersGroup();
            spammersGroup.id = "1";
            spammersGroup.projectId = "1";
            spammersGroup.groupName = "test name 1";
            spammersGroupDao.save(spammersGroup);
        }
        {
            SpammersGroup spammersGroup = new SpammersGroup();
            spammersGroup.id = "2";
            spammersGroup.projectId = "2";
            spammersGroup.groupName = "test name 2";
            spammersGroupDao.save(spammersGroup);
        }

        ConsoleSpammerDao consoleSpammerDao = new ConsoleSpammerDao();
        {
            ConsoleSpammer consoleSpammer = new ConsoleSpammer();
            consoleSpammer.id = "1";
            consoleSpammer.groupId = "1";
            consoleSpammer.spamWord = "test name 1";
            consoleSpammerDao.save(consoleSpammer);
        }
        {
            ConsoleSpammer consoleSpammer = new ConsoleSpammer();
            consoleSpammer.id = "2";
            consoleSpammer.groupId = "2";
            consoleSpammer.spamWord = "test name 2";
            consoleSpammerDao.save(consoleSpammer);
        }

        ConsoleSpammerStatusDao consoleSpammerStatusDao = new ConsoleSpammerStatusDao();
        consoleSpammerStatusDao.subscribe(new ChangesListener<ConsoleSpammerStatus>() {
            @Override
            public void initializingState() {

            }

            @Override
            public void readyState() {

            }

            @Override
            public void initial(ConsoleSpammerStatus object) {
                try {
                    System.out.println(debugOm.writeValueAsString(object));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void add(ConsoleSpammerStatus object) {
                try {
                    System.out.println(debugOm.writeValueAsString(object));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void remove(ConsoleSpammerStatus object) {
                try {
                    System.out.println(debugOm.writeValueAsString(object));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void change(
                ConsoleSpammerStatus oldObject, ConsoleSpammerStatus newObject
            ) {
                try {
                    System.out.println(debugOm.writeValueAsString(oldObject));
                    System.out.println(debugOm.writeValueAsString(newObject));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        MainManager mainManager = new MainManager(
            spamProjectDao,
            spammersGroupDao,
            consoleSpammerDao
        );
        mainManager.load();
        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        System.out.println("After wait...");
        {
            SpamProjectBo spamProjectBo = mainManager.getSpamProjectBo(String.valueOf(1));
            SpammersGroupBo spammersGroupBo = spamProjectBo.getSpammersGroupBoById(String.valueOf(1));
            ConsoleSpammerBo consoleSpammerBo = spammersGroupBo.getConsoleSpammerBo(String.valueOf(1));
            consoleSpammerBo.disable();
        }
        System.out.println("After disable");
    }
}
