package com.github.hronom.rethinkdb.playground.logic;

import com.github.hronom.rethinkdb.playground.logic.bo.SpamProjectBo;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpamProject;
import com.github.hronom.rethinkdb.playground.logic.dao.SpamProjectDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroupDao;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class MainManager {
    private final SpamProjectDao spamProjectDao;
    private final SpammersGroupDao spammersGroupDao;
    private final ConsoleSpammerDao consoleSpammerDao;

    private final ConcurrentHashMap<String, SpamProjectBo>
        spamProjectBoById
        = new ConcurrentHashMap<>();
    private final LinkedList<SpamProjectBo> spamProjectBos = new LinkedList<>();

    public MainManager(
        SpamProjectDao spamProjectDao,
        SpammersGroupDao spammersGroupDao,
        ConsoleSpammerDao consoleSpammerDao
    ) {
        this.spamProjectDao = spamProjectDao;
        this.spammersGroupDao = spammersGroupDao;
        this.consoleSpammerDao = consoleSpammerDao;
    }

    public void load() {
        for (SpamProject spamProject : spamProjectDao.getAll()) {
            SpamProjectBo spamProjectBo = new SpamProjectBo(
                spamProjectDao,
                spammersGroupDao,
                consoleSpammerDao
            );
            spamProjectBo.id = spamProject.id;
            spamProjectBo.projectName = spamProject.projectName;
            spamProjectBo.load();
            spamProjectBos.add(spamProjectBo);
            spamProjectBoById.put(spamProjectBo.id, spamProjectBo);
            System.out.println(spamProject.id + " - " + spamProject.projectName);
        }
    }

    public SpamProjectBo getSpamProjectBo(String id) {
        return spamProjectBoById.get(id);
    }
}
