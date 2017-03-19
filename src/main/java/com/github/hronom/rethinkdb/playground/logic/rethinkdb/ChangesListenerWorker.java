package com.github.hronom.rethinkdb.playground.logic.rethinkdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChangesListenerWorker<T> implements AutoCloseable {
    private final long timeout = TimeUnit.SECONDS.toMillis(3);
    private final RethinkDB r = RethinkDB.r;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicBoolean run = new AtomicBoolean(true);

    private final Thread listeningThread;

    public ChangesListenerWorker(
        Connection conn,
        String db,
        String tableName,
        ChangesListener<T> changesListener,
        Class<T> valueClass
    ) {
        Cursor changeCursor = r
            .db(db)
            .table(tableName)
            .changes()
            .optArg("include_initial", true)
            .optArg("include_states", true)
            .optArg("include_types", true)
            .run(conn);
        listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (run.get()) {
                    try {
                        Object change = changeCursor.next(timeout);
                        if (change != null) {
                            HashMap<String, Object> hashMap = (HashMap) change;
                            String type = (String) hashMap.get("type");
                            if ("state".equals(type)) {
                                String state = (String) hashMap.get("state");
                                if ("initializing".equals(state)) {
                                    changesListener.initializingState();
                                } else if ("ready".equals(state)) {
                                    changesListener.readyState();
                                }
                            } else if ("initial".equals(type)) {
                                Object newVal = hashMap.get("new_val");
                                T convertedNewVal = objectMapper.convertValue(newVal, valueClass);
                                changesListener.initial(convertedNewVal);
                            } else if ("add".equals(type)) {
                                Object newVal = hashMap.get("new_val");
                                T convertedNewVal = objectMapper.convertValue(newVal, valueClass);
                                changesListener.add(convertedNewVal);
                            } else if ("remove".equals(type)) {
                                Object oldVal = hashMap.get("old_val");
                                T convertedOldVal = objectMapper.convertValue(oldVal, valueClass);
                                changesListener.remove(convertedOldVal);
                            } else if ("change".equals(type)) {
                                Object oldVal = hashMap.get("old_val");
                                T convertedOldVal = objectMapper.convertValue(oldVal, valueClass);
                                Object newVal = hashMap.get("new_val");
                                T convertedNewVal = objectMapper.convertValue(newVal, valueClass);
                                changesListener.change(convertedOldVal, convertedNewVal);
                            }
                            System.out.println(change);
                        }
                    } catch (TimeoutException ignore) {
                    }
                }
            }
        });
        listeningThread.start();
    }

    @Override
    public void close() throws Exception {
        run.set(false);
        listeningThread.join();
    }
}
