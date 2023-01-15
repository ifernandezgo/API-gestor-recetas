package models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Recipe extends BaseModel {

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL)
    private Type type;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
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
