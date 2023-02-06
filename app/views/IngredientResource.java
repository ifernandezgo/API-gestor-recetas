package views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Ingredient;
import models.Recipe;
import play.libs.Json;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class IngredientResource {

    @JsonProperty("name")
    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    private String name;

    @JsonProperty("ingredients")
    private Set<Recipe> recipes;

    public IngredientResource(Ingredient ingredient) {
        super();
        this.name = ingredient.getName();
        this.recipes = ingredient.getRecipes();
    }

    public Ingredient toModel() {
        Ingredient ingredient = new Ingredient();

        ingredient.setName(this.name);

        return ingredient;
    }

    public JsonNode toJson() { return Json.toJson(this); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }
}
