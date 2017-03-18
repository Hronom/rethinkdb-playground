package com.github.hronom.rethinkdb.playground.logic.bo;

import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpamProjectDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroup;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroupDao;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class SpamProjectBo {
    public String id;
    public String projectName;

    private final SpamProjectDao spamProjectDao;
    private final SpammersGroupDao spammersGroupDao;
    private final ConsoleSpammerDao consoleSpammerDao;

    private final ConcurrentHashMap<String, SpammersGroupBo>
        spammersGroupBoById
        = new ConcurrentHashMap<>();
    private final LinkedList<SpammersGroupBo> spammersGroupBos = new LinkedList<>();

    public SpamProjectBo(
        SpamProjectDao spamProjectDao,
        SpammersGroupDao spammersGroupDao,
        ConsoleSpammerDao consoleSpammerDao
    ) {
        this.spamProjectDao = spamProjectDao;
        this.spammersGroupDao = spammersGroupDao;
        this.consoleSpammerDao = consoleSpammerDao;
    }

    public void load() {
        for (SpammersGroup spammersGroup : spammersGroupDao.getSpammersGroupsForProjectId(id)) {
            SpammersGroupBo spammersGroupBo = new SpammersGroupBo(
                spammersGroupDao,
                consoleSpammerDao
            );
            spammersGroupBo.id = spammersGroup.id;
            spammersGroupBo.groupName = spammersGroup.groupName;
            System.out.println(" " + spammersGroupBo.id + " - " + spammersGroupBo.groupName);
            spammersGroupBo.load();
            spammersGroupBoById.put(spammersGroupBo.id, spammersGroupBo);
            spammersGroupBos.add(spammersGroupBo);
        }
    }

    public SpammersGroupBo getSpammersGroupBoById(String id) {
        return spammersGroupBoById.get(id);
    }
}
