package io.objectbox;

import io.objectbox.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Box<T> {
    private final Class<T> cls;
    private final Map<Class<?>, List<?>> store;
    Box(Class<T> c, Map<Class<?>, List<?>> s) {
        cls = c;
        store = s;
    }
    @SuppressWarnings("unchecked")
    private List<T> list() {
        return (List<T>) store.computeIfAbsent(cls, k -> new ArrayList<>());
    }
    public List<T> getAll() {
        return new ArrayList<>(list());
    }
    public void put(T t) {
        List<T> l = list();
        int idx = l.indexOf(t);
        if (idx >= 0) {
            l.set(idx, t);
        } else {
            l.add(t);
        }
    }
    public void put(List<T> ts) {
        for (T t : ts) {
            put(t);
        }
    }
    public void remove(T t) {
        list().remove(t);
    }
    public QueryBuilder<T> query() {
        return new QueryBuilder<>(list());
    }
}