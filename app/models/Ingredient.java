package models;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Ingredient extends BaseModel {

    @ManyToMany(mappedBy = "ingredients")
    private Set<Recipe> recetas;
    private String name;

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
