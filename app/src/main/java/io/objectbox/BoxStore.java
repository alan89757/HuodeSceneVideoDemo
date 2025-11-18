package io.objectbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxStore {
    private static final Map<Class<?>, List<?>> store = new HashMap<>();
    public <T> Box<T> boxFor(Class<T> cls) {
        return new Box<>(cls, store);
    }
}