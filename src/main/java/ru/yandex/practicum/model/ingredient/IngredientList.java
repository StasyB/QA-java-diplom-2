package ru.yandex.practicum.model.ingredient;

import java.util.List;

public class IngredientList {
    private Boolean success;
    private List<Ingredient> data;

    public IngredientList(Boolean success, List<Ingredient> data) {
        this.success = success;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}
