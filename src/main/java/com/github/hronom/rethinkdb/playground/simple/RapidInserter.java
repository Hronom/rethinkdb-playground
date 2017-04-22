package com.github.hronom.rethinkdb.playground.simple;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.util.ArrayList;

public class RapidInserter {
    public static void main(String[] args) {
        final RethinkDB r = RethinkDB.r;
        final Connection conn = r.connection().hostname("localhost").port(28015).connect();

        ArrayList<String> dbList = r.dbList().run(conn);
        if (!dbList.contains("test_db")) {
            r.dbCreate("test_db").run(conn);
        }

        ArrayList<String> tableList = r.db("test_db").tableList().run(conn);
        if (!tableList.contains("test_table")) {
            r.db("test_db").tableCreate("test_table").run(conn);
        }

        for (int id = 0; id < Integer.MAX_VALUE; id++) {
            System.out.println(
                r
                    .db("test_db")
                    .table("test_table")
                    .insert(r.hashMap("id", id).with("value", "Entry " + id))
                    .optArg("conflict", "update").run(conn).toString());
            System.out.println("Pushed " + id);
        }
    }
}
