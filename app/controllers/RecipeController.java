package controllers;

import models.Category;
import models.Ingredient;
import models.Type;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;
import models.Recipe;


import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result crearReceta(Http.Request req) {
        Form<RecipeResource> recipeForm = formFactory.form(RecipeResource.class).bindFromRequest(req);

        RecipeResource recipeResource = new RecipeResource();

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResource = recipeForm.get();
        }
        /*Recipe recipe = Recipe.findByName(recipeResource.getName());
        if(recipe != null) {
            return Results.badRequest("Esta receta ya existe por lo que no puede ser creada nuevamente");
        }*/
        if(Type.findByName(recipeResource.getType()) == null) {
            Type t = new Type();
            Type.TypeEnum tEnum = Type.TypeEnum.valueOf(recipeResource.getType());
            t.setType(tEnum);
            t.save();
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
                            recipeResource.getTime(),
                            recipeResource.getDescription()))
                    .as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }

    public Result getAllRecipes(Http.Request req) {
        List<Recipe> recipes = Recipe.findAllPaged().getList();
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
            List<String> names = new ArrayList<>();
            Map<String, List<String>> ingredients = new LinkedHashMap<>();
            Map<String, List<String>> categories = new LinkedHashMap<>();
            Map<String, String> types = new LinkedHashMap<>();
            Map<String, Integer> times = new LinkedHashMap<>();
            Map<String, String> descriptions = new LinkedHashMap<>();
            for (RecipeResource rp : resources) {
                names.add(rp.getName());
                ingredients.put(rp.getName(), rp.getIngredients());
                categories.put(rp.getName(), rp.getCategories());
                types.put(rp.getName(), rp.getType());
                times.put(rp.getName(), rp.getTime());
                descriptions.put(rp.getName(), rp.getDescription());
            }
            res = Results.ok(views.xml.recipes.render(names, ingredients, categories, types, times, descriptions))
                    .as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }
        return res;
    }

    public Result deleteById(Http.Request req, Integer id) {
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        r.delete();
        return Results.ok("La receta con id " + id + " ha sido eliminada correctamente");
    }

    public Result actualizarReceta(Http.Request req, Integer id) {

        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        Form<RecipeResource> recipeForm = formFactory.form(RecipeResource.class).bindFromRequest(req);

        RecipeResource recipeResourceReq;

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResourceReq = recipeForm.get();
        }
        Recipe recipeRequest = recipeResourceReq.toModel();
        if(!r.getName().equals(recipeRequest.getName())) {
            r.setName(recipeRequest.getName());
        }
        for(Ingredient i : recipeRequest.getIngredients()) {
            if (!r.getIngredients().contains(i)) {
                r.addIngredient(i);
            }
        }
        for(Category c : recipeRequest.getCategories()) {
            if (!r.getCategories().contains(c)) {
                r.addCategory(c);
            }
        }
        if(!r.getType().equals(recipeRequest.getType())){
            r.setType(recipeRequest.getType());
        }
        if(!r.getDescription().equals(recipeRequest.getDescription())) {
            r.setDescription(recipeRequest.getDescription());
        }
        r.update();

        return Results.ok("La receta se ha actualizado de forma correcta");
    }

    public Result searchRecipes(Http.Request req) {
        return Results.ok();
    }
}
