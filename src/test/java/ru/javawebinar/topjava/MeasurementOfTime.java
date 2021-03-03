package ru.javawebinar.topjava;

import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MeasurementOfTime extends Stopwatch {
    private static final Logger logger = LoggerFactory.getLogger(MeasurementOfTime.class);
    private static final List<Entry> timeOfMethods = new ArrayList<>();

    static class Entry {
        private final String name;
        private final long ms;

        public Entry(String name, long ms) {
            this.name = name;
            this.ms = ms;
        }
    }

    @Override
    protected void finished(long nanos, Description description) {
        Entry entry = new Entry(description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
        timeOfMethods.add(entry);
        logger.debug("Executing, ms: {}", entry.ms);
    }

    public static void listOfTimeToTheLogs() {
        logger.debug(timeOfMethods.stream()
                .map(timeOfMethod -> String.format("\n%s%" + (30 - timeOfMethod.name.length()) + "s - %d ms", timeOfMethod.name, " ", timeOfMethod.ms))
                .collect(Collectors.joining()));
    }
}
