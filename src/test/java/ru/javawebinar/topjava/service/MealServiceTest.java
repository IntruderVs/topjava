package ru.javawebinar.topjava.service;

import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest extends TestCase {
    static private LocalDate startDate;
    static private LocalDate theNextDay;

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @BeforeClass
    public static void setup() {
        startDate = LocalDate.of(2020, 1, 30);
        theNextDay = LocalDate.of(2020, 1, 31);
    }

    @Test
    public void create() {
        Meal createdMeal = service.create(getNew(), USER_ID);
        Integer createdId = createdMeal.getId();
        Meal newMeal = getNew();
        newMeal.setId(createdId);
        assertMatch(createdMeal, newMeal);
        assertMatch(service.get(createdId, USER_ID), newMeal);
    }

    @Test
    public void updatePositive() {
        Meal updated = getUpdatedPositive();
        service.update(updated, USER_ID);
        Meal gotMeal = service.get(updated.getId(), USER_ID);
        assertMatch(gotMeal, updated);
    }

    @Test
    public void updateNegativeDuplicate() {
        Meal updated = getUpdatedNegative();
        assertThrows(DuplicateKeyException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void updateNegativeNotFound() {
        Meal updated = getUpdatedPositive();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void getPositive() {
        Meal meal = service.get(MEAL2.getId(), USER_ID);
        assertMatch(meal, MEAL2);
    }

    @Test
    public void getNegative() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL5.getId(), UserTestData.ADMIN_ID));
    }

    @Test
    public void deletePositive() {
        service.delete(MEAL4.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL4.getId(), USER_ID));
    }

    @Test
    public void deleteNegative() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL10.getId(), USER_ID));
    }

    @Test
    public void getBetweenInclusiveFromDateToDate() {
        List<Meal> mealsBetweenTwoDates = service.getBetweenInclusive(startDate, startDate, USER_ID);
        List<Meal> expectedMealsFromDateToDate = mealsOfUser.stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDate(), startDate, theNextDay))
                .collect(Collectors.toList());
        assertMatch(mealsBetweenTwoDates, expectedMealsFromDateToDate);
    }

    @Test
    public void getBetweenInclusiveFromDateToNull() {
        List<Meal> mealsFromDateToNull = service.getBetweenInclusive(theNextDay, null, USER_ID);
        List<Meal> expectedMealsFromDateToNull = mealsOfUser.stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDate(), theNextDay, null))
                .collect(Collectors.toList());
        assertMatch(mealsFromDateToNull, expectedMealsFromDateToNull);
    }

    @Test
    public void getBetweenInclusiveFromNullToDate() {
        List<Meal> mealsToDate = service.getBetweenInclusive(null, startDate, USER_ID);
        List<Meal> expectedMealsToDate = mealsOfUser.stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDate(), null, theNextDay))
                .collect(Collectors.toList());
        assertMatch(mealsToDate, expectedMealsToDate);
    }

    @Test
    public void getAll() {
        List<Meal> mealsOfAdmin = service.getAll(ADMIN_ID);
        assertMatch(mealsOfAdmin, MealTestData.mealsOfAdmin);
    }
}