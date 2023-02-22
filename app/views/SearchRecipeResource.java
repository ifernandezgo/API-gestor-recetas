package views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Category;
import models.Ingredient;
import models.Recipe;
import models.Type;
import play.data.validation.Constraints;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class SearchRecipeResource {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

    @JsonProperty("ingredients")
    private List<String> ingredients;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("type")
    @Constraints.ValidateWith(TypeValidatorSearch.class)
    private String type;

    @JsonProperty("time")
    private Integer time;

    @JsonProperty("description")
    private String description;


    public SearchRecipeResource() {}

    public SearchRecipeResource(Recipe recipe) {
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
