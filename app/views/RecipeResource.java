package views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import play.libs.Json;
import models.*;
import play.data.validation.Constraints;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class RecipeResource {

    @JsonProperty("id")
    private Long id;

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

    @JsonProperty("time")
    @Constraints.Required
    @NotNull(message = "Se debe indicar el tiempo promedio de preparación de la receta")
    private Integer time;

    @JsonProperty("description")
    @NotBlank(message = "La descripción de la receta no puede estar vacía")
    private String description;

    public RecipeResource()  {}

    public RecipeResource(Recipe recipe) {
        super();
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.ingredients = new ArrayList<>();
        for(Ingredient i : recipe.getIngredients()) {
            this.ingredients.add(i.getName());
        }
        this.categories = new ArrayList<>();
        for(Category c : recipe.getCategories()) {
            this.categories.add(c.getName());
        }
        this.type = recipe.getType().getType().name();
        this.time = recipe.getTime();
        this.description = recipe.getDescription();
    }

    public RecipeResource(JSONObject data) {
        super();
        this.name = data.get("name").toString();
        this.ingredients = new ArrayList<>();
        JSONArray ing = (JSONArray) data.get("ingredients");
        for(Object i : ing) {
            this.ingredients.add(i.toString());
        }
        this.categories = new ArrayList<>();
        JSONArray cat = (JSONArray) data.get("categories");
        for(Object c : cat) {
            categories.add(c.toString());
        }
        this.type = data.get("type").toString();
        this.time = Integer.valueOf(data.get("time").toString());
        this.description = data.get("description").toString();
    }

    public Recipe toModel() {
        Recipe recipe = new Recipe();

        recipe.setName(this.name);
        recipe.setDescription(this.description);
        recipe.setTime(this.time);

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

        Type t = Type.findByName(this.type);
        t.addRecipe(recipe);

        return recipe;
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

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
