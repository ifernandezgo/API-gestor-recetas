package views;

import play.data.validation.Constraints;

import javax.validation.constraints.NotBlank;

public class RecipeResource {

    @Constraints.Required
    @NotBlank(message = "Nombe de la receta vacío")
    private String name;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
