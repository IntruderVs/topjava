package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;

public class MealToTestData {
    public static final TestMatcher<MealTo> MEAL_TO_MATCHER = TestMatcher.usingIgnoringFieldsComparator(MealTo.class, "user");

    public static final MealTo mealTo1 = convertMealTo(meal1, false);
    public static final MealTo mealTo2 = convertMealTo(meal2, false);
    public static final MealTo mealTo3 = convertMealTo(meal3, false);
    public static final MealTo mealTo4 = convertMealTo(meal4, true);
    public static final MealTo mealTo5 = convertMealTo(meal5, true);
    public static final MealTo mealTo6 = convertMealTo(meal6, true);
    public static final MealTo mealTo7 = convertMealTo(meal7, true);

    public static final List<MealTo> mealsTo = List.of(mealTo7, mealTo6, mealTo5, mealTo4, mealTo3, mealTo2, mealTo1);

    private static MealTo convertMealTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
