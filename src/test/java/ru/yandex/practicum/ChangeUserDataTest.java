package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.practicum.client.UserSteps;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.model.user.UserGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class ChangeUserDataTest {
    private UserSteps userSteps;
    private String accessToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        userSteps = new UserSteps();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Test: change email. Authorized user. POST /api/auth/user")
    public void emailCanBeChangedForAuthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setEmail("newEmail@gmail.com");

        userSteps.change(accessToken, user)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail().toLowerCase()))
                .body("user.name", is(user.getName()));
    }

    @Test
    @DisplayName("Test: change password. Authorized user. POST /api/auth/user")
    public void passwordCanBeChangedForAuthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setPassword("newPassword");

        userSteps.change(accessToken, user)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail().toLowerCase()))
                .body("user.name", is(user.getName()));
    }

    @Test
    @DisplayName("Test: change name. Authorized user. POST /api/auth/user")
    public void nameCanBeChangedForAuthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setName("newName");

        userSteps.change(accessToken, user)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", is(user.getEmail().toLowerCase()))
                .body("user.name", is(user.getName()));
    }

    @Test
    @DisplayName("Test: cannot change email. Unauthorized user. POST /api/auth/user")
    public void emailCanNotBeChangedForUnauthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setEmail("newEmail@gmail.com");

        userSteps.change("", user)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Test: cannot change password. Unauthorized user. POST /api/auth/user")
    public void passwordCanNotBeChangedForUnauthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setPassword("newPassword");

        userSteps.change("", user)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Test: cannot change name. Unauthorized user. POST /api/auth/user")
    public void nameCanNotBeChangedForUnauthorizedUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        user.setEmail("newName");

        userSteps.change("", user)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Test: cannot change email. User with such email already exists. Authorized user. POST /api/auth/user")
    public void emailCanNotBeChangedAlreadyExists() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        User secondUser = UserGenerator.getRandomUser();
        String secondAccessToken = userSteps.register(secondUser).extract().body().jsonPath().get("accessToken");

        user.setEmail(secondUser.getEmail());

        userSteps.change(accessToken, user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User with such email already exists"));

        userSteps.delete(secondAccessToken);
    }
}
