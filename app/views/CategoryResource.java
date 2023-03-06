package views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Category;
import models.Recipe;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class CategoryResource {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("category")
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Constraints.Required
    private String name;

    @JsonIgnore
    private Set<Recipe> recipes;

    public CategoryResource() {}

    public CategoryResource(Category category) {
        super();
        this.id = category.getId();
        this.name = category.getName();
        this.recipes = category.getRecipes();
    }

    public Category toModel() {
        Category category = new Category();
        category.setName(this.name);
        if(this.recipes != null) { category.setRecipes(this.recipes); }
        return category;
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
