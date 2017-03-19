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

public class ConsoleSpammerStatusDao implements AutoCloseable {
    private final String tableName = "console_spammer_status";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArrayList<ChangesListenerWorker<ConsoleSpammerStatus>>
        changesListenerWorkers
        = new CopyOnWriteArrayList<>();

    private final RethinkDB r = RethinkDB.r;
    private final Connection conn;

    public ConsoleSpammerStatusDao() {
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

    @Override
    public void close() throws Exception {
        for (ChangesListenerWorker<ConsoleSpammerStatus> changesListenerWorker : changesListenerWorkers) {
            changesListenerWorker.close();
        }
    }

    public void subscribe(ChangesListener<ConsoleSpammerStatus> changesListener) {
        ChangesListenerWorker<ConsoleSpammerStatus>
            changesListenerWorker
            = new ChangesListenerWorker<>(
            conn,
            "test_db",
            tableName,
            changesListener,
            ConsoleSpammerStatus.class
        );
        changesListenerWorkers.add(changesListenerWorker);
    }

    public void save(ConsoleSpammerStatus consoleSpammerStatus) {
        r
            .db("test_db")
            .table(tableName)
            .insert(objectMapper.convertValue(consoleSpammerStatus, Map.class))
            .optArg("conflict", "replace")
            .run(conn);
    }

    public ArrayList<ConsoleSpammerStatus> getConsoleSpammerStatusesForGroupId(String groupId) {
        ArrayList<ConsoleSpammerStatus> consoleSpammerStatuses = new ArrayList<>();
        Cursor cursor = r.db("test_db").table(tableName).getAll(groupId).optArg("index", "groupId").run(conn);
        for (Object change : cursor) {
            ConsoleSpammerStatus consoleSpammerStatus = objectMapper.convertValue(change, ConsoleSpammerStatus.class);
            consoleSpammerStatuses.add(consoleSpammerStatus);
        }
        return consoleSpammerStatuses;
    }
}
