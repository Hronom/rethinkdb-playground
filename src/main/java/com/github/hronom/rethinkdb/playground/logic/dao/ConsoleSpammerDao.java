package com.github.hronom.rethinkdb.playground.logic.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hronom.rethinkdb.playground.logic.rethinkdb.ChangesListener;
import com.github.hronom.rethinkdb.playground.logic.rethinkdb.ChangesListenerWorker;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsoleSpammerDao {
    private final String tableName = "console_spammer";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArrayList<ChangesListenerWorker<ConsoleSpammer>>
        changesListenerWorkers
        = new CopyOnWriteArrayList<>();

    private final RethinkDB r = RethinkDB.r;
    private final Connection conn;

    public ConsoleSpammerDao() {
        conn = r.connection().hostname("localhost").port(28015).connect();

        ArrayList<String> dbList = r.dbList().run(conn);
        if (!dbList.contains("test_db")) {
            r.dbCreate("test_db").run(conn);
        }

        ArrayList<String> tableList = r.db("test_db").tableList().run(conn);
        if (!tableList.contains(tableName)) {
            r.db("test_db").tableCreate(tableName).run(conn);
            r.db("test_db").table(tableName).indexCreate("groupId").run(conn);
            r.db("test_db").table(tableName).indexWait().run(conn);
        }
    }

    public void subscribe(ChangesListener<ConsoleSpammer> changesListener) {
        ChangesListenerWorker<ConsoleSpammer>
            changesListenerWorker
            = new ChangesListenerWorker<>(
            conn,
            "test_db",
            tableName,
            changesListener,
            ConsoleSpammer.class
        );
        changesListenerWorkers.add(changesListenerWorker);
    }

    public void save(ConsoleSpammer consoleSpammer) {
        r
            .db("test_db")
            .table(tableName)
            .insert(objectMapper.convertValue(consoleSpammer, Map.class))
            .optArg("conflict", "replace")
            .run(conn);
    }

    public ArrayList<ConsoleSpammer> getConsoleSpammersForGroupId(String groupId) {
        ArrayList<ConsoleSpammer> consoleSpammers = new ArrayList<>();
        Cursor cursor = r.db("test_db").table(tableName).getAll(groupId).optArg("index", "groupId").run(conn);
        for (Object change : cursor) {
            ConsoleSpammer consoleSpammer = objectMapper.convertValue(change, ConsoleSpammer.class);
            consoleSpammers.add(consoleSpammer);
        }
        return consoleSpammers;
    }
}
