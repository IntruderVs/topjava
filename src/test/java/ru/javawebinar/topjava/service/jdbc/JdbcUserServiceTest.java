package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.UserServiceTest;

import static ru.javawebinar.topjava.web.Profiles.JDBC;

@ActiveProfiles(JDBC)
public class JdbcUserServiceTest extends UserServiceTest {
}
