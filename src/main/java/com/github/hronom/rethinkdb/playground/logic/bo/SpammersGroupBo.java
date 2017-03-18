package com.github.hronom.rethinkdb.playground.logic.bo;

import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammer;
import com.github.hronom.rethinkdb.playground.logic.dao.ConsoleSpammerDao;
import com.github.hronom.rethinkdb.playground.logic.dao.SpammersGroupDao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SpammersGroupBo {
    public String id;
    public String groupName;

    private final SpammersGroupDao spammersGroupDao;
    private final ConsoleSpammerDao consoleSpammerDao;

    private final ConcurrentHashMap<String, ConsoleSpammerBo>
        consoleSpammerBoById
        = new ConcurrentHashMap<>();
    private final ArrayList<ConsoleSpammerBo> consoleSpammerBos = new ArrayList<>();

    public SpammersGroupBo(SpammersGroupDao spammersGroupDao, ConsoleSpammerDao consoleSpammerDao) {
        this.spammersGroupDao = spammersGroupDao;
        this.consoleSpammerDao = consoleSpammerDao;
    }

    public void load() {
        for (ConsoleSpammer consoleSpammer : consoleSpammerDao.getConsoleSpammersForGroupId(id)) {
            ConsoleSpammerBo consoleSpammerBo = new ConsoleSpammerBo(consoleSpammerDao);
            consoleSpammerBo.id = consoleSpammer.id;
            consoleSpammerBo.spamWord = consoleSpammer.spamWord;
            System.out.println("  " + consoleSpammerBo.id + " - " + consoleSpammerBo.spamWord);
            consoleSpammerBo.load();
            consoleSpammerBoById.put(consoleSpammerBo.id, consoleSpammerBo);
            consoleSpammerBos.add(consoleSpammerBo);
        }
    }

    public ConsoleSpammerBo getConsoleSpammerBo(String id){
        return consoleSpammerBoById.get(id);
    }
}
