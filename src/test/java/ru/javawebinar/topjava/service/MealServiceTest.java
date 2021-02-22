package ru.javawebinar.topjava.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

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
    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

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
    public void updateSuccessful() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        Meal gotMeal = service.get(updated.getId(), USER_ID);
        assertMatch(gotMeal, getUpdated());
    }

    @Test
    public void updateFailDuplicate() {
        Meal updated = getUpdatedWithDuplicateDateTime();
        assertThrows(DuplicateKeyException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void updateFailNotFound() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void getSuccessful() {
        Meal meal = service.get(MEAL2.getId(), USER_ID);
        assertMatch(meal, MEAL2);
    }

    @Test
    public void getFail() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL5.getId(), ADMIN_ID));
    }

    @Test
    public void deleteSuccessful() {
        service.delete(MEAL4.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL4.getId(), USER_ID));
    }

    @Test
    public void deleteFail() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL10.getId(), USER_ID));
    }

    @Test
    public void getBetweenInclusiveFromDateToDate() {
        final LocalDate filterDate = LocalDate.of(2020, 1, 30);
        List<Meal> mealsBetweenTwoDates = service.getBetweenInclusive(filterDate, filterDate, USER_ID);
        assertMatch(mealsBetweenTwoDates, MEAL2, MEAL1, MEAL0);
    }

    @Test
    public void getBetweenInclusiveFromDateToNull() {
        final LocalDate filterDate = LocalDate.of(2020, 1, 31);
        List<Meal> mealsFromDateToNull = service.getBetweenInclusive(filterDate, null, USER_ID);
        assertMatch(mealsFromDateToNull, MEAL6, MEAL5, MEAL4, MEAL3);
    }

    @Test
    public void getBetweenInclusiveFromNullToDate() {
        final LocalDate filterDate = LocalDate.of(2020, 1, 30);
        List<Meal> mealsToDate = service.getBetweenInclusive(null, filterDate, USER_ID);
        assertMatch(mealsToDate, MEAL2, MEAL1, MEAL0);
    }

    @Test
    public void getAll() {
        List<Meal> mealsOfAdmin = service.getAll(ADMIN_ID);
        assertMatch(mealsOfAdmin, MEAL13, MEAL12, MEAL11, MEAL10, MEAL9, MEAL8, MEAL7);
    }
}