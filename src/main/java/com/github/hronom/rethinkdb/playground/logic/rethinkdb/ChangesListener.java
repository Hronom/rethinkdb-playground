package com.github.hronom.rethinkdb.playground.logic.rethinkdb;

public interface ChangesListener<T> {
    void initializingState();
    void readyState();
    void initial(T object);
    void add(T object);
    void remove(T object);
    void change(T oldObject, T newObject);
}
