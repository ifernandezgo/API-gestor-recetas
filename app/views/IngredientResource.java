package views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Ingredient;
import models.Recipe;
import play.libs.Json;
import play.data.validation.Constraints;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class IngredientResource {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ingredient")
    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    @Constraints.Required
    private String name;

    @JsonIgnore
    private Set<Recipe> recipes;

    public IngredientResource() {}

    public IngredientResource(Ingredient ingredient) {
        super();
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.recipes = ingredient.getRecipes();
    }

    public Ingredient toModel() {
        Ingredient ingredient = new Ingredient();

        ingredient.setName(this.name);
        if(this.recipes != null) { ingredient.setRecipes(this.recipes); };

        return ingredient;
    }

    public JsonNode toJson() { return Json.toJson(this); }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
