package models;

import io.ebean.Expr;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.PagedList;

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

    @ManyToOne
    private Type type;

    private Integer time;

    private String description;

    public void addIngredient(Ingredient ingredient) {
        if(this.ingredients == null) { this.ingredients = new ArrayList<>(); }

        this.ingredients.add(ingredient);
        ingredient.addRecipe(this);
    }

    public void addCategory(Category category) {
        if(this.categories == null) { this.categories = new ArrayList<>(); }

        this.categories.add(category);
        category.addRecipe(this);
    }

    public static Recipe findById(Long id) {
        return find.byId(id);
    }

    public static Recipe findByName(String name) {
        return find.query().where().ieq("name", name).findOne();
    } 

    public static List<Recipe> searchRecipes(String nameReq, List<String> ingredientsList, List<String> categoriesList, String typeName, Integer duration) {

        ExpressionList<Recipe> query = find.query().where().setMaxRows(10).setFirstRow(0);

        query.eqIfPresent("name", nameReq);

        query.inOrEmpty("ingredients.name", ingredientsList);

        query.inOrEmpty("categories.name", categoriesList);

        query.eqIfPresent("type.type", typeName);

        if(duration != null) { query.le("time", duration); }

        return query.findPagedList().getList();
    }

    public static List<Recipe> findAll() { return find.all(); }

    public static PagedList<Recipe> findAllPaged() { return find.query().where().setMaxRows(10).setFirstRow(0).findPagedList(); }

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
