package com.leonardo.minecraft.core.api.database;

import java.util.Map;
import java.util.Set;

public interface CrudRepository<B, T> {

    void createTable();

    Set<T> findAll(Map<String, String> whereSearch, Integer offset, Integer limit);

    Set<T> findAll(Map<String, String> whereSearch, Integer limit);

    Set<T> findAll(Map<String, String> whereSearch);

    Set<T> findAll();

    void saveAll(Set<B> objects);

    void create(T object);

    T readById(B object);

    void update(T object);

    void deleteById(B object);

    void save(T object);


}
