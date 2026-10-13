package com.hcl.library.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class SearchCache {
    private static final int MAX_SIZE = 5;

    private final Map<String, String> cache = new LinkedHashMap<String, String>(MAX_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_SIZE;
        }
    };

    public void put(String title, String result) {
        cache.put(title.toLowerCase(), result);
    }

    public String get(String title) {
        return cache.get(title.toLowerCase());
    }

    public int size() {
        return cache.size();
    }
}
