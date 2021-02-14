package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    //<userId, <mealId, Meal>>
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(1, meal));
        MealsUtil.meals2.forEach(meal -> save(2, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            getOrCreateUserMeals(userId).put(meal.getId(), meal);
            return meal;
        }
        return getOrCreateUserMeals(userId)
                .computeIfPresent(meal.getId(), (i, m) -> meal);
    }

    public Map<Integer, Meal> getOrCreateUserMeals(int userId) {
        return repository.computeIfAbsent(userId, integer -> new ConcurrentHashMap<>());
    }

    @Override
    public boolean delete(int userId, int id) {
        Map<Integer, Meal> mealsMap = repository.get(userId);
        return mealsMap != null && mealsMap.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        Map<Integer, Meal> mealsMap = repository.get(userId);
        return mealsMap == null ? null : mealsMap.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getFilteredByDates(int userId, LocalDate startDate, LocalDate endDate) {
        return filterByPredicate(userId, meal -> DateTimeUtil.isBetweenClosed(meal.getDate(), startDate, endDate));
    }

    public List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> integerMealMap = repository.get(userId);
        return integerMealMap == null ? new ArrayList<>() : integerMealMap
                .values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

