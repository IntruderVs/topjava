DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

-- Meals
INSERT INTO meals (user_id, description, calories, dateTime)
VALUES (100000, 'Завтрак(user)', 500, timestamp '2020-01-30 10:00:00'),
       (100000, 'Обед(user)', 1000, timestamp '2020-01-30 13:00:00'),
       (100000, 'Ужин(user)', 500, timestamp '2020-01-30 20:00:00'),
       (100000, 'Еда на граничное значение(user)', 100, timestamp '2020-01-31 00:00:00'),
       (100000, 'Завтрак(user)', 1000, timestamp '2020-01-31 10:00:00'),
       (100000, 'Обед(user)', 500, timestamp '2020-01-31 13:00:00'),
       (100000, 'Ужин(user)', 410, timestamp '2020-01-31 20:00:00'),
       (100001, 'Завтрак(admin)', 501, timestamp '2020-01-30 10:00:00'),
       (100001, 'Обед(admin)', 1001, timestamp '2020-01-30 13:00:00'),
       (100001, 'Ужин(admin)', 401, timestamp '2020-01-30 20:00:00'),
       (100001, 'Еда на граничное значение(admin)', 101, timestamp '2020-01-31 00:00:00'),
       (100001, 'Завтрак(admin)', 1001, timestamp '2020-01-31 10:00:00'),
       (100001, 'Обед(admin)', 501, timestamp '2020-01-31 13:00:00'),
       (100001, 'Ужин(admin)', 411, timestamp '2020-01-31 20:00:00');