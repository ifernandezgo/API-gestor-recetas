package views;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.annotation.EnumValue;
import models.*;
import play.data.validation.Constraints;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class RecipeResource {

    @JsonProperty("name")
    @Constraints.Required
    @Constraints.ValidateWith(RecipeValidator.class)
    @NotBlank(message = "El nombre de la receta no puede estar vacío")
    private String name;

    @JsonProperty("ingredients")
    @Constraints.Required
    @NotEmpty(message = "La receta debe contener algún ingrediente")
    private List<String> ingredients;

    @JsonProperty("categories")
    @NotEmpty(message = "La receta debe pertenecer a alguna categoría")
    private List<String> categories;

    @JsonProperty("type")
    @Constraints.ValidateWith(TypeValidator.class)
    @Constraints.Required
    @NotBlank(message = "Se debe indicar el tipo de comida que es la receta: (Desayuno, Comida o Cena)")
    private String type;

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

        for (String c : this.categories) {
            Category category = Category.findByName(c);
            if (category == null) {
                category = new Category();
                category.setName(c);
            }
            recipe.addCategory(category);
        }

        Type t = new Type();
        Type.TypeEnum tEnum = Type.TypeEnum.valueOf(this.type);
        t.setType(tEnum);
        recipe.setType(t);
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

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
