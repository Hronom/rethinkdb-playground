package com.github.hronom.rethinkdb.playground.logic.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Map;

public class SpammersGroupDao {
    private final String tableName = "spammers_group";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RethinkDB r = RethinkDB.r;
    private final Connection conn;

    public SpammersGroupDao() {
        conn = r.connection().hostname("localhost").port(28015).connect();

        ArrayList<String> dbList = r.dbList().run(conn);
        if (!dbList.contains("test_db")) {
            r.dbCreate("test_db").run(conn);
        }

        ArrayList<String> tableList = r.db("test_db").tableList().run(conn);
        if (!tableList.contains(tableName)) {
            r.db("test_db").tableCreate(tableName).run(conn);
            r.db("test_db").table(tableName).indexCreate("projectId").run(conn);
            r.db("test_db").table(tableName).indexWait().run(conn);
        }
    }

    public void save(SpammersGroup spammersGroup) {
        r
            .db("test_db")
            .table(tableName)
            .insert(objectMapper.convertValue(spammersGroup, Map.class))
            .optArg("conflict", "replace")
            .run(conn);
    }

    public ArrayList<SpammersGroup> getSpammersGroupsForProjectId(String projectId) {
        ArrayList<SpammersGroup> spammersGroups = new ArrayList<>();
        Cursor cursor = r.db("test_db").table(tableName).getAll(projectId).optArg("index", "projectId").run(conn);
        for (Object change : cursor) {
            SpammersGroup spammersGroup = objectMapper.convertValue(change, SpammersGroup.class);
            spammersGroups.add(spammersGroup);
        }
        return spammersGroups;
    }
}
