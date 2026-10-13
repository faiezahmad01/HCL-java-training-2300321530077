package com.hcl.library.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class nMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> data = new HashMap<>();
    private final Function<T, ID> idGetter;

    public InMemoryRepository(Function<T, ID> idGetter) {
        this.idGetter = idGetter;
    }

    @Override
    public void save(T entity) {
        data.put(idGetter.apply(entity), entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean deleteById(ID id) {
        return data.remove(id) != null;
    }
}
