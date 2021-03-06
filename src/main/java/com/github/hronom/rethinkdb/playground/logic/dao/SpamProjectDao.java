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

public class SpamProjectDao {
    private final String tableName = "spam_project";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CopyOnWriteArrayList<ChangesListenerWorker<SpamProject>>
        changesListenerWorkers
        = new CopyOnWriteArrayList<>();

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
            r.db("test_db").table(tableName).indexWait().run(conn);
        }
    }

    public void subscribe(ChangesListener<SpamProject> changesListener) {
        ChangesListenerWorker<SpamProject>
            changesListenerWorker
            = new ChangesListenerWorker<>(
            conn,
            "test_db",
            tableName,
            changesListener,
            SpamProject.class
        );
        changesListenerWorkers.add(changesListenerWorker);
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
