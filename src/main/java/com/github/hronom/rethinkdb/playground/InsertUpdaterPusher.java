package com.github.hronom.rethinkdb.playground;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class InsertUpdaterPusher {
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

        final AtomicLong idGenerator = new AtomicLong(0);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long id = idGenerator.incrementAndGet();
                System.out.println(r.db("test_db").table("test_table")
                    .insert(
                        r
                            .hashMap("id", id)
                            .with("value", "Entry " + id))
                    .optArg("conflict", "update")
                    .run(conn));
                System.out.println("Pushed " + id);
            }
        };
        timer.schedule(timerTask, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    }
}
