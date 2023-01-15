package models;

import io.ebean.annotation.EnumValue;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

@Entity
public class Type extends BaseModel {

    @OneToOne(mappedBy = "type")
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    public enum TypeEnum {
        @EnumValue("Desayuno")
        DESAYUNO,

        @EnumValue("Comida")
        COMIDA,

        @EnumValue("Cena")
        CENA
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
