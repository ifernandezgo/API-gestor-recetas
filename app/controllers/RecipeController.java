package controllers;

import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Xml;
import views.RecipeResource;
import models.Recipe;
import views.xml.recipe;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            res = Results.ok(views.xml.recipe.render(recipeResource.getName(),
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
            res = Results.ok(views.xml.recipe.render(recipeResource.getName(),
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


    public Result getAllRecipes(Http.Request req) {
        List<Recipe> recipes = Recipe.findAll();
        if(recipes.size() == 0) {
            return Results.notFound("No hay ninguna receta guardada en la base de datos.");
        }

        List<RecipeResource> resources = recipes.
                stream().
                map(RecipeResource::new).
                collect(Collectors.toList());

        Result res;

        if(req.accepts("application/json")) {
            res = Results.ok(Json.toJson(resources)).as("application/json");
        } else if(req.accepts("application/xml")) {
            List<Xml> recipesXML = new ArrayList<>();

            for (RecipeResource rp : resources) {
                recipesXML.add(views.xml.recipe.render(rp.getName(),
                        rp.getIngredients(),
                        rp.getCategories(),
                        rp.getType(),
                        rp.getDescription()));
            }
            res = Results.ok().as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }
        return res;
    }
}
