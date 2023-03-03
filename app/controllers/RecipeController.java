package controllers;

import models.Category;
import models.Ingredient;
import models.Type;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;
import models.Recipe;
import views.SearchUpdateRecipeResource;
import org.json.simple.JSONObject;

import javax.inject.Inject;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result createRecipe(Http.Request req) {
        Form<RecipeResource> recipeForm = formFactory.form(RecipeResource.class).bindFromRequest(req);

        RecipeResource recipeResource = new RecipeResource();

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResource = recipeForm.get();
        }
        if(Type.findByName(recipeResource.getType()) == null) {
            Type t = new Type();
            Type.TypeEnum tEnum = Type.TypeEnum.valueOf(recipeResource.getType());
            t.setType(tEnum);
            t.save();
        }
        Recipe recipe = recipeResource.toModel();
        recipe.save();

        Result res;
        recipeResource.setId(recipe.getId());
        if(req.accepts("application/json")) {
            res = Results.created(recipeResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.created(views.xml.recipe.render(
                            recipeResource.getId().intValue(),
                            recipeResource.getName(),
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

    public Result getRecetaById(Http.Request req, Integer id) {
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        return okResponseRecipe(req, r);
    }

    public Result getAllRecipes(Http.Request req) {
        List<Recipe> recipes = Recipe.findAllPaged().getList();
        if(recipes.size() == 0) {
            return Results.notFound("No hay ninguna receta guardada en la base de datos.");
        }

        return okResponseListRecipes(req, recipes);
    }

    public Result deleteById(Http.Request req, Integer id) {
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        r.delete();
        return Results.ok("La receta con id " + id + " ha sido eliminada correctamente");
    }

    public Result updateRecipe(Http.Request req, Integer id) {

        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound("No existe ninguna receta con ese id en la base de datos. Pruebe con otra.");
        }

        Form<SearchUpdateRecipeResource> recipeForm = formFactory.form(SearchUpdateRecipeResource.class).bindFromRequest(req);

        SearchUpdateRecipeResource recipeResourceReq;

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResourceReq = recipeForm.get();
        }
        recipeResourceReq.completeResource(r);
        if(!Type.enumContains(recipeResourceReq.getType())) {
            return Results.badRequest("El tipo de la receta debe ser: Desayuno, Comida o Cena");
        }
        Recipe recipeRequest = recipeResourceReq.toModel();
        if(!r.getName().equals(recipeRequest.getName())) {
            if(Recipe.findByName(recipeRequest.getName()) != null) {
                return Results.badRequest("No se puede actualizar el nombre porque ya existe una receta con ese mismo nombre");
            } else {
                r.setName(recipeRequest.getName());
            }
        }
        if(!r.getIngredients().equals(recipeRequest.getIngredients())) {
            r.setIngredients(recipeRequest.getIngredients());
        }
        if(!r.getCategories().equals(recipeRequest.getCategories())) {
            r.setCategories(recipeRequest.getCategories());
        }
        if(!r.getType().equals(recipeRequest.getType())){
            r.setType(recipeRequest.getType());
        }
        if(!r.getTime().equals(recipeRequest.getTime())){
            r.setTime(recipeRequest.getTime());
        }
        if(!r.getDescription().equals(recipeRequest.getDescription())) {
            r.setDescription(recipeRequest.getDescription());
        }
        r.update();

        return okResponseRecipe(req, r);
    }

    public Result searchRecipes(Http.Request req) {

        Form<SearchUpdateRecipeResource> recipeForm = formFactory.form(SearchUpdateRecipeResource.class).bindFromRequest(req);

        SearchUpdateRecipeResource recipeResourceReq;
        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResourceReq = recipeForm.get();
        }

        List<Recipe> recipes = Recipe.searchRecipes(recipeResourceReq.getName(), recipeResourceReq.getIngredients(),
                recipeResourceReq.getCategories(), recipeResourceReq.getType(), recipeResourceReq.getTime());

        return okResponseListRecipes(req, recipes);
    }

    public Result createDemo(Http.Request req) throws IOException, ParseException {
        //Vaciar la bd
        this.emptyDb();

        //Rellenar con los datos nuevos
        Object o = new JSONParser().parse(new FileReader("./public/data.json"));
        JSONArray data = (JSONArray) o;
        for(int i = 0; i < data.size(); i++) {
            RecipeResource rs = new RecipeResource((JSONObject) data.get(i));
            if(Type.findByName(rs.getType()) == null) {
                Type t = new Type();
                Type.TypeEnum tEnum = Type.TypeEnum.valueOf(rs.getType());
                t.setType(tEnum);
                t.save();
            }
            Recipe recipe = rs.toModel();
            recipe.save();
        }

        return Results.ok("Se han añadido " + data.size() + " recetas a la base de datos.");
    }

    public void emptyDb() {
        List<Recipe> recipes = Recipe.findAll();
        for(Recipe r : recipes) { r.delete(); }

        List<Ingredient> ingredients = Ingredient.findAll();
        for(Ingredient i : ingredients) { i.delete(); }

        List<Category> categories = Category.findAll();
        for(Category c : categories) { c.delete(); }

        List<Type> types = Type.findAll();
        for(Type t : types) { t.delete(); }
    }

    public Result okResponseRecipe(Http.Request req, Recipe r) {
        Result res;
        RecipeResource recipeResource = new RecipeResource(r);
        if(req.accepts("application/json")) {
            res = Results.ok(recipeResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.recipe.render(
                            recipeResource.getId().intValue(),
                            recipeResource.getName(),
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

    public Result okResponseListRecipes(Http.Request req, List<Recipe> recipes) {

        List<RecipeResource> resources = recipes.
                stream().
                map(RecipeResource::new).
                collect(Collectors.toList());

        Result res;

        if(req.accepts("application/json")) {
            res = Results.ok(Json.toJson(resources)).as("application/json");
        } else if(req.accepts("application/xml")) {
            List<Integer> ids = new ArrayList<>();
            Map<Integer, String> names = new LinkedHashMap<>();
            Map<Integer, List<String>> ingredients = new LinkedHashMap<>();
            Map<Integer, List<String>> categories = new LinkedHashMap<>();
            Map<Integer, String> types = new LinkedHashMap<>();
            Map<Integer, Integer> times = new LinkedHashMap<>();
            Map<Integer, String> descriptions = new LinkedHashMap<>();
            for (RecipeResource rp : resources) {
                Integer id = rp.getId().intValue();
                ids.add(id);
                names.put(id, rp.getName());
                ingredients.put(id, rp.getIngredients());
                categories.put(id, rp.getCategories());
                types.put(id, rp.getType());
                times.put(id, rp.getTime());
                descriptions.put(id, rp.getDescription());
            }
            res = Results.ok(views.xml.recipes.render(ids, names, ingredients, categories, types, times, descriptions))
                    .as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }
        return res;
    }

}
