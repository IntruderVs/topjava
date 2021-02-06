package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    void delete(long id);

    List<Meal> getAll();

    Meal getById(long id);

    Meal update(Meal meal);

    Meal add(Meal meal);
}
