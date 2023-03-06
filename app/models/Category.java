package models;

import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Set;

@Entity
public class Category extends BaseModel {

    private static final Finder<Long, Category> find = new Finder<>(Category.class);

    @ManyToMany(mappedBy = "categories")
    private Set<Recipe> recipes;

    private String name;

    public static Category findById(Long id) {
        return find.byId(id);
    }

    public static Category findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }

    public static List<Category> findAll()  { return find.all(); }

    public static List<Category> findAllPaged() { return find.query().where().setMaxRows(10).setFirstRow(0).findPagedList().getList(); }

    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipe) {
        this.recipes = recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
