package com.github.hronom.rethinkdb.playground;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        final RethinkDB r = RethinkDB.r;
        final Connection conn = r.connection().hostname("172.17.0.2").port(28015).connect();

        ArrayList<String> dbList = r.dbList().run(conn);
        if (!dbList.contains("superheroes")) {
            r.dbCreate("superheroes").run(conn);
        }

        ArrayList<String> tableList = r.db("superheroes").tableList().run(conn);
        if (!tableList.contains("dc_universe")) {
            r.db("superheroes").tableCreate("dc_universe").run(conn);
        }

        final Random random = new Random();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                r.db("superheroes").table("dc_universe")
                    .insert(
                        r
                            .hashMap("id", random.nextInt())
                            .with("title", "Lorem ipsum " + random.nextInt())
                            .with("content", "Dolor sit amet"))
                    .run(conn);
            }
        };
        timer.schedule(timerTask, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(3));

        Cursor changeCursor = r
            .db("superheroes")
            .table("dc_universe")
            .changes()
            .optArg("include_initial", true)
            .optArg("include_states", true)
            .run(conn);
        for (Object change : changeCursor) {
            System.out.println(change);
        }
    }
}
