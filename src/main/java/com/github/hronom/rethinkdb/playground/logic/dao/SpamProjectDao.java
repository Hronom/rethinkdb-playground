package com.github.hronom.rethinkdb.playground.logic.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Map;

public class SpamProjectDao {
    private final String tableName = "spam_project";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RethinkDB r = RethinkDB.r;
    private final Connection conn;

    public SpamProjectDao() {
        conn = r.connection().hostname("localhost").port(28015).connect();

        ArrayList<String> dbList = r.dbList().run(conn);
        if (!dbList.contains("test_db")) {
            r.dbCreate("test_db").run(conn);
        }

        ArrayList<String> tableList = r.db("test_db").tableList().run(conn);
        if (!tableList.contains(tableName)) {
            r.db("test_db").tableCreate(tableName).run(conn);
        }
    }

    public void save(SpamProject spamProject) {
        r
            .db("test_db")
            .table(tableName)
            .insert(objectMapper.convertValue(spamProject, Map.class))
            .optArg("conflict", "replace")
            .run(conn);
    }

    public ArrayList<SpamProject> getAll() {
        ArrayList<SpamProject> spamProjects = new ArrayList<>();
        Cursor cursor = r.db("test_db").table(tableName).run(conn);
        for(Object change : cursor)
        {
            SpamProject spamProject = objectMapper.convertValue(change, SpamProject.class);
            spamProjects.add(spamProject);
        }
        return spamProjects;
    }
}
