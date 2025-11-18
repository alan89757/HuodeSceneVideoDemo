package io.objectbox.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QueryBuilder<T> {
    private final List<T> source;
    private final List<Predicate<T>> preds = new ArrayList<>();
    public QueryBuilder(List<T> s) {
        source = s;
    }
    public QueryBuilder<T> equal(Object prop, Object value) {
        String name = prop instanceof String ? (String) prop : null;
        if (name != null) {
            preds.add(t -> {
                try {
                    Field f = t.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object v = f.get(t);
                    return value == null ? v == null : value.equals(v);
                } catch (Exception e) {
                    return false;
                }
            });
        }
        return this;
    }
    public Query<T> build() {
        return new Query<>(source, preds);
    }
}