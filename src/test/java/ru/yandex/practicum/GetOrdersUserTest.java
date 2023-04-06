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
import ru.yandex.practicum.client.IngredientSteps;
import ru.yandex.practicum.client.OrderSteps;
import ru.yandex.practicum.client.UserSteps;
import ru.yandex.practicum.model.ingredient.Ingredient;
import ru.yandex.practicum.model.user.User;
import ru.yandex.practicum.model.user.UserGenerator;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrdersUserTest {
    private UserSteps userSteps;
    private IngredientSteps ingredientSteps;
    private OrderSteps orderSteps;
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
        ingredientSteps = new IngredientSteps();
        orderSteps = new OrderSteps();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Test: get order for auth user. POST /api/orders")
    public void getOrdersForAuthUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        List<Ingredient> ingredients = ingredientSteps.getIngredientList();
        orderSteps.create(accessToken, List.of(ingredients.get(0).get_id(), ingredients.get(1).get_id()));
        orderSteps.getAllOrders(accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("orders.ingredients", is(notNullValue()));
    }

    @Test
    @DisplayName("Test: don't get order for Not auth user. POST /api/orders")
    public void canNotGetOrdersForNotAuthUser() {

        orderSteps.getAllOrders("")
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

}
