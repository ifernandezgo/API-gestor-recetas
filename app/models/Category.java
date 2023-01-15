package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Category extends BaseModel {

    @ManyToOne
    private Recipe recipe;

    private String name;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
