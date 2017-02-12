package com.github.hronom.rethinkdb.playground;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class SubscriberDelayed {
    public static void main(String[] args) throws InterruptedException {
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

        TreeSet<Long> values = new TreeSet<Long>();

        Cursor changeCursor = r
            .db("test_db")
            .table("test_table")
            .changes()
            .optArg("include_initial", true)
            .optArg("include_states", true)
            .optArg("include_types", true)
            .run(conn);
        for (Object change : changeCursor) {
            HashMap<String, Object> hashMap = (HashMap) change;
            String type = (String) hashMap.get("type");
            if ("state".equals(type)) {
                // todo
            } else if ("initial".equals(type)) {
                HashMap<String, Object> newVal = (HashMap) hashMap.get("new_val");
                Long id = (Long) newVal.get("id");
                values.add(id);
            } else if ("add".equals(type)) {
                HashMap<String, Object> newVal = (HashMap) hashMap.get("new_val");
                Long id = (Long) newVal.get("id");
                values.add(id);
            } else if ("remove".equals(type)) {
                HashMap<String, Object> newVal = (HashMap) hashMap.get("old_val");
                Long id = (Long) newVal.get("id");
                values.remove(id);
            } else if ("change".equals(type)){
                // todo
            }
            System.out.println(change);
            System.out.println(values);
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        }
    }
}
