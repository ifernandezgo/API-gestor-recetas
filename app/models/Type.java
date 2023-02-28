package models;

import io.ebean.Finder;
import io.ebean.annotation.EnumValue;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Type extends BaseModel {

    private static final Finder<Long, Type> find = new Finder<>(Type.class);

    @OneToMany(cascade= CascadeType.ALL, mappedBy = "type")
    private List<Recipe> recipes;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    public enum TypeEnum {
        @EnumValue("Desayuno")
        Desayuno,

        @EnumValue("Comida")
        Comida,

        @EnumValue("Cena")
        Cena
    }

    public static boolean enumContains(String value) {
        for(TypeEnum t : TypeEnum.values()) {
            if(t.name().equals(value)) {
                return true;
            }
        }

        return false;
    }

    public static Type findByName(String name) {
        //Type.TypeEnum tEnum = Type.TypeEnum.valueOf(name);
        return find.query().where().eq("type", name).findOne();
    }

    public static List<Type> findAll() { return find.all(); }

    public void addRecipe(Recipe recipe) {
        if(this.recipes == null) {
            this.recipes = new ArrayList<>();
        }

        this.recipes.add(recipe);
        recipe.setType(this);
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
