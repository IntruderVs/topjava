package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    static final int CALORIES_PER_DAY = 2000;
    static final String INSERT_OR_EDIT = "/editMeal.jsp";
    static final String LIST_MEALS = "/meals.jsp";
    private static final Logger log = getLogger(MealServlet.class);
    private MealDao mealDao;

    @Override
    public void init() {
        mealDao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = LIST_MEALS;
        String action = request.getParameter("action");
        log.info("call {}", action = action == null ? "all meals" : action);
        switch (action) {
            case ("delete"):
                Long mealIdFromRequest = getMealIdFromRequest(request);
                log.debug("call 'delete' by Id: {}", mealIdFromRequest);
                mealDao.delete(mealIdFromRequest);
                response.sendRedirect("meals");
                return;
            case ("edit"):
                mealIdFromRequest = getMealIdFromRequest(request);
                log.debug("call 'edit' by id: {}", mealIdFromRequest);
                Meal meal = mealDao.getById(mealIdFromRequest);
                request.setAttribute("mealForEdit", meal);
                path = INSERT_OR_EDIT;
                break;
            case ("add"):
                request.setAttribute("mealForEdit", new Meal(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), null, 0));
                path = INSERT_OR_EDIT;
                break;
            default:
                request.setAttribute("listMealTo", MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                break;
        }
        request.getRequestDispatcher(path).forward(request, response);
    }

    private Long getMealIdFromRequest(HttpServletRequest request) {
        String mealId = Objects.requireNonNull(request.getParameter("mealId"));
        return Long.parseLong(mealId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("call do post");
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        Long mealId = getMealIdOrNullFromRequest(request);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        Meal meal = new Meal(mealId, dateTime, description, calories);
        log.debug("post {} meal: {}", mealId == null ? "add" : "update", meal);
        if (mealId == null) {
            mealDao.add(meal);
        } else {
            mealDao.update(meal);
        }
        response.sendRedirect("meals");
    }

    private Long getMealIdOrNullFromRequest(HttpServletRequest request) {
        String mealId = request.getParameter("mealId");
        if (mealId == null) {
            return null;
        }
        return Long.parseLong(mealId);
    }
}
