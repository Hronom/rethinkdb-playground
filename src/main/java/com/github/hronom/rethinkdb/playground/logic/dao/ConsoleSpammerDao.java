package com.github.hronom.rethinkdb.playground.logic.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Map;

public class ConsoleSpammerDao {
    private final String tableName = "console_spammer";

    private final ObjectMapper objectMapper = new ObjectMapper();
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
        }
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
