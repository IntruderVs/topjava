package ru.javawebinar.topjava;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MeasurementOfTime extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementOfTime.class);
    private long startTime;
    private String methodName;
    private static final List<String> timeOfMethods = new ArrayList<>();

    public static void listOfTimeToTheLogs() {
        for (String s : timeOfMethods) {
            LOGGER.debug(s);
        }
        timeOfMethods.clear();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        methodName = description.getMethodName();
        return super.apply(base, description);
    }

    @Override
    protected void before() throws Throwable {
        startTime = System.nanoTime();
    }

    @Override
    protected void after() {
        long endTime = System.nanoTime();
        long mc = (endTime - startTime) / 1000000;
        timeOfMethods.add("Executing method - " + methodName + ", mc: " + mc);
        LOGGER.debug("Executing, mc: {}", mc);
    }
}
