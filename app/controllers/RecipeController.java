package controllers;

import models.Type;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;
import models.Recipe;

import javax.inject.Inject;

public class RecipeController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result crearReceta(Http.Request req) {
        Form<RecipeResource> recipeForm = formFactory.form(RecipeResource.class).bindFromRequest(req);

        RecipeResource recipeResource;

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResource = recipeForm.get();
        }

        /*if(!Type.enumContains(recipeResource.getType())) {
            return Results.badRequest("El tipo de la receta debe ser: Desayuno, Comida o Cena");
        }*/

        //if(Recipe.findByName(recipeResource.getName()) != null) {
          //  return Results.badRequest("Esta receta ya existe por lo que no puede ser creada nuevamente");
        //} else {
            Recipe recipe = recipeResource.toModel();
            recipe.save();
        //}

        return Results.created("La receta ha sido creada");
    }

    /*public Result getAllRecipes() {
        return Results.ok(recetas.toString());
    }*/
}
