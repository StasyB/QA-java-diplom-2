package ru.yandex.practicum.model.user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    static int length = 10;

    public static User getRandomUser() {
        String email = RandomStringUtils.random(length, true, true) + "@gmail.com";
        String password = RandomStringUtils.random(length, true, true);
        String name = RandomStringUtils.random(length, true, true);
        return new User(email, password, name);
    }


}
