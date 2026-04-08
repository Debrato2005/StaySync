package com.staysync.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repository<T> {
    private final List<T> items = new ArrayList<>();

    public void add(T item)       { items.add(item); }
    public T get(int index)       { return items.get(index); }
    public boolean remove(T item) { return items.remove(item); }
    public int size()             { return items.size(); }
    public List<T> getAll()       { return Collections.unmodifiableList(items); }
}