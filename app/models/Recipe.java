package models;

import io.ebean.Finder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recipe extends BaseModel {

    private static final Finder<Long, Recipe> find = new Finder<>(Recipe.class);

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL)
    private Type type;

    private String description;

    public void addIngredient(Ingredient ingredient) {
        if(this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }

        this.ingredients.add(ingredient);
        ingredient.addRecipe(this);
    }

    public void addCategory(Category category) {
        if(this.categories == null) {
            this.categories = new ArrayList<>();
        }

        this.categories.add(category);
        category.addRecipe(this);
    }

    public static Recipe findById(Long id) {
        return find.byId(id);
    }

    public static Recipe findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }


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
