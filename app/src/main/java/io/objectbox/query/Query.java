package io.objectbox.query;

import java.util.List;
import java.util.function.Predicate;

public class Query<T> {
    private final List<T> source;
    private final List<Predicate<T>> preds;
    public Query(List<T> s, List<Predicate<T>> p) {
        source = s;
        preds = p;
    }
    public T findFirst() {
        for (T t : source) {
            boolean ok = true;
            for (Predicate<T> pr : preds) {
                if (!pr.test(t)) {
                    ok = false;
                    break;
                }
            }
            if (ok) return t;
        }
        return null;
    }
}