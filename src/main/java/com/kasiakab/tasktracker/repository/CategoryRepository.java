package com.kasiakab.tasktracker.repository;

import com.kasiakab.tasktracker.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByName(String name);

    List<Category> findByColor(String color);

    boolean existsByName(String name);
}
