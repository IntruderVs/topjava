package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        System.out.println(filteredByCycle(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
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

    public static List<UserMealWithExcess> filteredByCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class ExcessCalories {
            private int calories;
            private final AtomicBoolean excess = new AtomicBoolean();

            private ExcessCalories(int calories) {
                this.calories = calories;
            }

            private void increase(int calories) {
                this.calories += calories;
                this.excess.compareAndSet(false, this.calories > caloriesPerDay);
            }
        }

        Map<LocalDate, ExcessCalories> caloriesPerDays = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        meals.forEach(meal -> {
            caloriesPerDays.merge(meal.getDateTime().toLocalDate(), new ExcessCalories(meal.getCalories()), (c, c2) -> {
                c.increase(c2.calories);
                return c;
            });
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(convertToUserMealWithExcess(meal, caloriesPerDays.get(meal.getDateTime().toLocalDate()).excess));
            }
        });
        return result;
    }

    public static List<UserMealWithExcess> filteredByStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .entrySet().stream()
                .flatMap(mealsPerDay -> {
                    AtomicBoolean generalExcess = new AtomicBoolean();
                    final AtomicInteger caloriesPerDayLocal = new AtomicInteger(caloriesPerDay);
                    Stream<UserMealWithExcess> stream = mealsPerDay.getValue().stream()
                            .peek(meal -> caloriesPerDayLocal.addAndGet(-meal.getCalories()))
                            .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> convertToUserMealWithExcess(meal, generalExcess))
                            .collect(Collectors.toList()).stream();
                    generalExcess.compareAndSet(false, caloriesPerDayLocal.get() < 0);
                    return stream;
                })
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess convertToUserMealWithExcess(UserMeal userMeal, AtomicBoolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

    private static UserMealWithExcess convertToUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}
