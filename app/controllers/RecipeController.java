package controllers;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;
import models.Recipe;

import views.xml.recipe;

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

    public Result getRecetaById(Http.Request req, Integer id) {
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        Result res;
        RecipeResource recipeResource = new RecipeResource(r);
        if(req.accepts("application/json")) {
            res = Results.ok(recipeResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(recipe.render(recipeResource.getName(),
                            recipeResource.getIngredients(),
                            recipeResource.getCategories(),
                            recipeResource.getType(),
                            recipeResource.getDescription()))
                    .as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }

    public Result getRecetaByName(Http.Request req, String name) {
        System.out.println(name);
        Recipe r = Recipe.findByName(name);
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        Result res;
        RecipeResource recipeResource = new RecipeResource(r);
        if(req.accepts("application/json")) {
            res = Results.ok(recipeResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(recipe.render(recipeResource.getName(),
                            recipeResource.getIngredients(),
                            recipeResource.getCategories(),
                            recipeResource.getType(),
                            recipeResource.getDescription()))
                    .as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }


    /*public Result getAllRecipes() {
        return Results.ok(recetas.toString());
    }*/
}
