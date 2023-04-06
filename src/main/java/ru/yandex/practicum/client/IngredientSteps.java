package ru.yandex.practicum.client;

import io.qameta.allure.Step;
import ru.yandex.practicum.client.base.StellarburgersApiClient;
import ru.yandex.practicum.model.ingredient.Ingredient;
import ru.yandex.practicum.model.ingredient.IngredientList;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientSteps extends StellarburgersApiClient {
    private static final String INGREDIENTS_GET = "/api/ingredients";

    @Step
    public List<Ingredient> getIngredientList() {
        IngredientList ingredients = given()
                .spec(getBaseReqSpec())
                .get(INGREDIENTS_GET)
                .as(IngredientList.class);
        if (ingredients == null) {
            throw new NullPointerException("ingredients is absent in response");
        }

        return ingredients.getData();
    }

}
