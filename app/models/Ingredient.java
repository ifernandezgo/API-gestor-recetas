package models;

import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Ingredient extends BaseModel {

    private static final Finder<Long, Ingredient> find = new Finder<>(Ingredient.class);

    @ManyToMany(mappedBy = "ingredients")
    private Set<Recipe> recetas;
    private String name;

    public void addRecipe(Recipe recipe) {
        this.recetas.add(recipe);
    }

    public static Ingredient findById(Long id) {
        return find.byId(id);
    }

    public static Ingredient findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }

    public Set<Recipe> getRecetas() {
        return recetas;
    }

    public void setRecetas(Set<Recipe> recetas) {
        this.recetas = recetas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
