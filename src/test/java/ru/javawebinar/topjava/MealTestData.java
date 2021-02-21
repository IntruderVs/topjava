package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {

    public static final Meal MEAL0 = new Meal(100000, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак(user)", 500);
    public static final Meal MEAL1 = new Meal(100001, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед(user)", 1000);
    public static final Meal MEAL2 = new Meal(100002, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин(user)", 500);
    public static final Meal MEAL3 = new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение(user)", 100);
    public static final Meal MEAL4 = new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак(user)", 1000);
    public static final Meal MEAL5 = new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед(user)", 500);
    public static final Meal MEAL6 = new Meal(100006, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин(user)", 410);
    public static final Meal MEAL7 = new Meal(100007, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак(admin)", 501);
    public static final Meal MEAL8 = new Meal(100008, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед(admin)", 1001);
    public static final Meal MEAL9 = new Meal(100009, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин(admin)", 401);
    public static final Meal MEAL10 = new Meal(100010, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение(admin)", 101);
    public static final Meal MEAL11 = new Meal(100011, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак(admin)", 1001);
    public static final Meal MEAL12 = new Meal(100012, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед(admin)", 501);
    public static final Meal MEAL13 = new Meal(100013, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин(admin)", 411);
    public static final List<Meal> mealsOfUser = Arrays.asList(MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1, MEAL0);
    public static final List<Meal> mealsOfAdmin = Arrays.asList(MEAL13, MEAL12, MEAL11, MEAL10, MEAL9, MEAL8, MEAL7);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2021, 2, 21, 13, 24), "New meal", 1555);
    }

    public static Meal getUpdatedPositive() {
        Meal updated = new Meal();
        updated.setId(MEAL1.getId());
        updated.setCalories(201);
        updated.setDescription("Updated description");
        updated.setDateTime(LocalDateTime.of(2021, 1, 31, 12, 34));
        return updated;
    }

    public static Meal getUpdatedNegative() {
        Meal updated = new Meal();
        updated.setId(MEAL2.getId());
        updated.setCalories(201);
        updated.setDescription("Updated description");
        updated.setDateTime(MEAL3.getDateTime());
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
