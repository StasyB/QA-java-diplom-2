package ru.yandex.practicum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.client.base.StellarburgersApiClient;
import ru.yandex.practicum.model.order.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps extends StellarburgersApiClient {
    private static final String ORDERS = "/api/orders";

    @Step
    public ValidatableResponse create(String accessToken, List<String> ingredients) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .body(new Order(ingredients))
                .when()
                .post(ORDERS)
                .then();
    }

    @Step
    public ValidatableResponse getAllOrders(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS)
                .then();
    }
}
