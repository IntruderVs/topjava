package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByOneCycle(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDays = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        meals.forEach(meal -> {
            caloriesPerDays.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(convertToUserMealWithExcess(meal, false));
            }
        });
        result.forEach(mealWithExcess -> mealWithExcess.setExcess(caloriesPerDay < caloriesPerDays.get(mealWithExcess.getLocalDate())));
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDays = meals.stream()
                .collect(Collectors.toMap(meal -> meal.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> convertToUserMealWithExcess(meal, caloriesPerDay < caloriesPerDays.get(meal.getDateTime().toLocalDate())))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class QueueMealWithExcess {
            private int count;
            private UserMealWithExcess userMealWithExcess;
            private QueueMealWithExcess before;

            public QueueMealWithExcess(int count) {
                this.count = count;
            }

            public void decreaseCount(int i) {
                this.count -= i;
                update(count < 0);
            }

            private void update(boolean ex) {
                if (ex) {
                    this.userMealWithExcess.setExcess(true);
                    if (before != null) {
                        before.update(true);
                    }
                    before = null;
                }
            }

            public QueueMealWithExcess add(UserMealWithExcess userMealWithExcess) {
                if (this.userMealWithExcess == null) {
                    this.userMealWithExcess = userMealWithExcess;
                    return this;
                }
                QueueMealWithExcess queueMealWithExcess = new QueueMealWithExcess(count);
                queueMealWithExcess.before = this;
                queueMealWithExcess.userMealWithExcess = userMealWithExcess;
                return queueMealWithExcess;
            }
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDate, QueueMealWithExcess> caloriesPerDays = new HashMap<>();
        meals.forEach(meal -> {
            QueueMealWithExcess queueMealWithExcess = caloriesPerDays.get(meal.getDateTime().toLocalDate());
            if (queueMealWithExcess == null) {
                queueMealWithExcess = new QueueMealWithExcess(caloriesPerDay);
                caloriesPerDays.put(meal.getDateTime().toLocalDate(), queueMealWithExcess);
            }
            queueMealWithExcess.decreaseCount(meal.getCalories());

            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                UserMealWithExcess userMealWithExcess = convertToUserMealWithExcess(meal, queueMealWithExcess.count < 0);
                if (queueMealWithExcess.count >= 0) {
                    caloriesPerDays.put(meal.getDateTime().toLocalDate(), queueMealWithExcess.add(userMealWithExcess));
                }
                result.add(userMealWithExcess);
            }
        });
        return result;
    }

    private static UserMealWithExcess convertToUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}
