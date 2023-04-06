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
import ru.yandex.practicum.model.user.UserGenerator;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

public class RegisterUserTest {

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
    @DisplayName("Test: register user is success. POST /api/auth/register")
    public void userCanBeRegisterWithValidData() {
        User user = UserGenerator.getRandomUser();

        ValidatableResponse response = userSteps.register(user);
        accessToken = response.extract().body().jsonPath().get("accessToken");

        response
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Test: check all fields in response after success register user. POST /api/auth/register")
    public void checkAllResponseFieldsAfterSuccessRegister() {
        User user = UserGenerator.getRandomUser();
        ValidatableResponse response = userSteps.register(user);
        accessToken = response.extract().body().jsonPath().get("accessToken");
        String refreshToken = response.extract().body().jsonPath().get("refreshToken");

        response
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", is(accessToken))
                .body("refreshToken", is(refreshToken))
                .body("user.email", is(user.getEmail().toLowerCase()))
                .body("user.name", is(user.getName()));
    }

    @Test
    @DisplayName("Test: user registration is unsuccessful. Missing email. POST /api/auth/register")
    public void userCanNotBeRegisterWithoutEmail() {
        User user = UserGenerator.getRandomUser();
        user.setEmail(null);
        String expected = "Email, password and name are required fields";

        ValidatableResponse response = userSteps.register(user);

        accessToken = response.extract().body().jsonPath().get("accessToken");

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(expected));
    }

    @Test
    @DisplayName("Test: user registration is unsuccessful. Missing password. POST /api/auth/register")
    public void userCanNotBeRegisterWithoutPassword() {
        User user = UserGenerator.getRandomUser();
        user.setPassword(null);
        String expected = "Email, password and name are required fields";

        ValidatableResponse response = userSteps.register(user);

        accessToken = response.extract().body().jsonPath().get("accessToken");

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(expected));
    }

    @Test
    @DisplayName("Test: user registration is unsuccessful. Missing name. POST /api/auth/register")
    public void userCanNotBeRegisterWithoutName() {
        User user = UserGenerator.getRandomUser();
        user.setName(null);
        String expected = "Email, password and name are required fields";

        ValidatableResponse response = userSteps.register(user);

        accessToken = response.extract().body().jsonPath().get("accessToken");

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(expected));
    }

    @Test
    @DisplayName("Test: user cannot be register a second time. POST /api/auth/register")
    public void userCanNotBeRegisterSecondTime() {
        User user = UserGenerator.getRandomUser();
        String expected = "User already exists";

        accessToken = userSteps.register(user)
                .extract().body().jsonPath().get("accessToken");

        ValidatableResponse secondRegister = userSteps.register(user);
        accessToken = secondRegister.extract().body().jsonPath().get("accessToken");

        secondRegister
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(expected));
    }

}