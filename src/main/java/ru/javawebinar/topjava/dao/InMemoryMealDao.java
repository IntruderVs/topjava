package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMealDao implements MealDao {

    private final AtomicLong idCounter = new AtomicLong();
    private final Map<Long, Meal> mapMeals = new ConcurrentHashMap<>();

    {
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 1), "Полуночный перекус", 100));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        add(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public void delete(long id) {
        mapMeals.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(mapMeals.values());
    }

    @Override
    public Meal getById(long id) {
        return mapMeals.get(id);
    }

    @Override
    public Meal update(Meal meal) {
        if (meal.getId() == null) {
            return null;
        }
        return mapMeals.computeIfPresent(meal.getId(), (l, m) -> meal);
    }

    @Override
    public Meal add(Meal meal) {
        if (meal.getId() != null) {
            return null;
        }
        meal.setId(idCounter.getAndIncrement());
        mapMeals.put(meal.getId(), meal);
        return meal;
    }
}
