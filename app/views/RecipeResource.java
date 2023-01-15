package views;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.Category;
import models.Ingredient;
import models.Recipe;
import models.Type;
import play.data.validation.Constraints;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class RecipeResource {

    @JsonProperty("name")
    @Constraints.Required
    @NotBlank(message = "El nombre de la receta no puede estar vacío")
    private String name;

    @JsonProperty("ingredients")
    @Constraints.Required
    //@NotBlank(message = "La receta debe contener algún ingrediente")
    private List<String> ingredients;

    @JsonProperty("categories")
    private List<Category> categories;

    @JsonProperty("type")
    //@Constraints.Required
    //@NotBlank(message = "Se debe indicar el tipo de comida que es la receta: (Desayuno, Comida o Cena)")
    private Type type;

    @JsonProperty("description")
    private String description;

    public Recipe toModel() {
        Recipe recipe = new Recipe();

        recipe.setName(this.name);
        recipe.setDescription(this.description);

        for (String i : this.ingredients) {
            Ingredient ingredient = Ingredient.findByName(i);
            if (ingredient == null) {
                ingredient = new Ingredient();
                ingredient.setName(i);
            }
            recipe.addIngredient(ingredient);
        }

        return recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
