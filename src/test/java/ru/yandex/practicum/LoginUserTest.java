package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.practicum.client.UserSteps;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.model.user.UserCredentials;
import ru.yandex.practicum.model.user.UserGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {
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
    @DisplayName("Test: login user is success. POST /api/auth/login")
    public void userCanBeLoginWithValidData() {
        User user = UserGenerator.getRandomUser();

        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");
        userSteps.login(UserCredentials.from(user))
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Test: check all fields in response after success login user. POST /api/auth/login")
    public void checkAllResponseFieldsAfterSuccessLogin() {
        User user = UserGenerator.getRandomUser();

        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        ValidatableResponse response = userSteps.login(UserCredentials.from(user));
        String accessTokenLogin = response.extract().body().jsonPath().get("accessToken");
        String refreshTokenLogin = response.extract().body().jsonPath().get("refreshToken");

        response
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", is(accessTokenLogin))
                .body("refreshToken", is(refreshTokenLogin))
                .body("user.email", is(user.getEmail().toLowerCase()))
                .body("user.name", is(user.getName()));
    }

    @Test
    @DisplayName("Test: login user is unsuccessful. Incorrect email. POST /api/auth/login")
    public void userCanNotBeLoginWithIncorrectEmail() {
        User user = UserGenerator.getRandomUser();
        String expected = "email or password are incorrect";

        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        userSteps.login(UserCredentials.getCredentialsWithIncorrectEmail(user))
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is(expected));
    }

    @Test
    @DisplayName("Test: login user is unsuccessful. Incorrect password. POST /api/auth/login")
    public void userCanNotBeLoginWithIncorrectPassword() {
        User user = UserGenerator.getRandomUser();
        String expected = "email or password are incorrect";

        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        userSteps.login(UserCredentials.getCredentialsWithIncorrectPassword(user))
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is(expected));
    }
}
