package ru.javawebinar.topjava;

import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.ClassUtils;
import ru.javawebinar.topjava.web.Profiles;

//http://stackoverflow.com/questions/23871255/spring-profiles-simple-example-of-activeprofilesresolver
public class ActiveDbProfileResolver implements ActiveProfilesResolver {
    //  Get DB profile depending of DB driver in classpath
    public static String getActiveDbProfile() {
        if (ClassUtils.isPresent("org.postgresql.Driver", null)) {
            return Profiles.POSTGRES_DB;
        } else if (ClassUtils.isPresent("org.hsqldb.jdbcDriver", null)) {
            return Profiles.HSQL_DB;
        } else {
            throw new IllegalStateException("Could not find DB driver");
        }
    }

    @Override
    public @NonNull
    String[] resolve(@NonNull Class<?> aClass) {
        return new String[]{getActiveDbProfile()};
    }
}
