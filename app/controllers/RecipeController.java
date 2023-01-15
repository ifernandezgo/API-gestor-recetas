package controllers;

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

        Recipe recipe = recipeResource.toModel();
        recipe.save();

        return Results.created("La receta ha sido creada");
    }

    /*public Result getAllRecipes() {
        return Results.ok(recetas.toString());
    }*/
}
