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
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 1), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 2), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 3), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 4), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 5), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 6), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 7), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 8), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 9), "Завтрак", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByOneCycleAndFunctionalInterface(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
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

    //-->-------------------------------------------------filteredByOneCycleAndFunctionalInterface
    @FunctionalInterface
    private interface ExecutionQueue {
        void execute();

        default ExecutionQueue oneMoreExecute(ExecutionQueue next) {
            //The creation of a new ExecutionQueue witch says "execute 'this' then do the next" - the queue
            return () -> {
                this.execute();
                next.execute();
            };
        }
    }

    public static List<UserMealWithExcess> filteredByOneCycleAndFunctionalInterface(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDays = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        ExecutionQueue deferredExecutionQueue = () -> {/*Stub*/};

        for (UserMeal meal: meals) {
            caloriesPerDays.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                deferredExecutionQueue = deferredExecutionQueue.oneMoreExecute(() -> result.add(convertToUserMealWithExcess(meal,
                        caloriesPerDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)));
            }
        }
        deferredExecutionQueue.execute();
        return result;
    }
    //--<-------------------------------------------------filteredByOneCycleAndFunctionalInterface

    private static UserMealWithExcess convertToUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }


}
