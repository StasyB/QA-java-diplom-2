package ru.yandex.practicum.model.user;

import org.apache.commons.lang3.RandomStringUtils;

public class UserCredentials {
    static String randomString = RandomStringUtils.random(3, true, true);
    private String email;
    private String password;


    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCredentials from(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword());
    }

    public static UserCredentials getCredentialsWithIncorrectEmail(User user) {
        return new UserCredentials(user.getEmail() + randomString, user.getPassword());
    }

    public static UserCredentials getCredentialsWithIncorrectPassword(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword() + randomString);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
