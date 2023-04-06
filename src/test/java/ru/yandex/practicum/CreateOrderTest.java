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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
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
    @DisplayName("Test: create order with ingredient for auth user. POST /api/orders")
    public void orderCanBeCreateWithIngredientsAndAuth() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        List<Ingredient> ingredients = ingredientSteps.getIngredientList();
        orderSteps.create(accessToken, List.of(ingredients.get(0).get_id(), ingredients.get(1).get_id()))
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", is(notNullValue()))
                .body("order.number", is(notNullValue()));
    }

    @Test
    @DisplayName("Test: create order with ingredient for Not auth user. POST /api/orders")
    public void orderCanBeCreateWithIngredientsAndNotAuth() {
        List<Ingredient> ingredients = ingredientSteps.getIngredientList();
        orderSteps.create("", List.of(ingredients.get(0).get_id(), ingredients.get(1).get_id()))
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("name", is(notNullValue()))
                .body("order.number", is(notNullValue()));
    }

    @Test
    @DisplayName("Test: order cannot be create without ingredient for auth user. POST /api/orders")
    public void orderCanNotBeCreateWithoutIngredientsAuthUser() {
        User user = UserGenerator.getRandomUser();
        accessToken = userSteps.register(user).extract().body().jsonPath().get("accessToken");

        orderSteps.create(accessToken, null)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Test: order cannot be create without ingredient for Not auth user. POST /api/orders")
    public void orderCanNotBeCreateWithoutIngredientsNotAuthUser() {

        orderSteps.create("", null)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Test: order cannot be create with incorrect hash ingredient. POST /api/orders")
    public void orderCanBeCreateWithIncorrectHashIngredient() {
        List<Ingredient> ingredients = ingredientSteps.getIngredientList();
        orderSteps.create("", List.of(ingredients.get(0).get_id(), "incorrectHash"))
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
