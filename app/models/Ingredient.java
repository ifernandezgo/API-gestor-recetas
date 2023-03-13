package models;

import io.ebean.Finder;
import io.ebean.PagedList;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Set;

@Entity
public class Ingredient extends BaseModel {

    private static final Finder<Long, Ingredient> find = new Finder<>(Ingredient.class);

    @ManyToMany(mappedBy = "ingredients")
    private Set<Recipe> recipes;
    private String name;

    public void addRecipe(Recipe recipe) { this.recipes.add(recipe); }

    public static Ingredient findById(Long id) {
        return find.byId(id);
    }

    public static List<Ingredient> findAll() { return find.all(); }

    public static List<Ingredient> findAllPaged() { return find.query().where().setMaxRows(10).setFirstRow(0).findPagedList().getList(); }

    public static Ingredient findByName(String name) {
        return find.query().where().ieq("name", name).findOne();
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
