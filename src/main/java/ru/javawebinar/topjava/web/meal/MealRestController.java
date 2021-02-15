package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;

    public Meal create(Meal meal) {
        log.info("create userId: {}, meal: {}", authUserId(), meal);
        checkNew(meal);
        return service.create(authUserId(), meal);
    }

    public void update(Meal meal, int id) {
        log.info("update userId: {}, meal: {} with mealId: {}", authUserId(), meal, id);
        assureIdConsistent(meal, id);
        service.update(authUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete userId: {}, id: {}", authUserId(), id);
        service.delete(authUserId(), id);
    }

    public Meal get(int id) {
        log.info("get userId: {}, id: {}", authUserId(), id);
        return service.get(authUserId(), id);
    }

    public List<MealTo> getAll() {
        log.info("getAll userId: {}", authUserId());
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getAll with filters userId: {}, startDate {}, startTime: {}, endDate: {}, endTime: {}", authUserId(), startDate, startTime, endDate, endTime);
        if (startDate == null) {
            startDate = LocalDate.MIN;
        }
        if (startTime == null) {
            startTime = LocalTime.MIN;
        }
        if (endDate == null) {
            endDate = LocalDate.MAX;
        }
        if (endTime == null) {
            endTime = LocalTime.MAX;
        }
        return MealsUtil.getFilteredTos(service.getAllFiltered(authUserId(), startDate, endDate), authUserCaloriesPerDay(), startTime, endTime);
    }
}