package controllers;

import models.Category;
import models.Ingredient;
import models.Type;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;
import models.Recipe;
import views.SearchUpdateRecipeResource;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    MessagesApi messagesApi;

    public Result createRecipe(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result getRecetaById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound(messages.at("recipeNotFound"));
        }

        return okResponseRecipe(req, r);
    }

    public Result getAllRecipes(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
        List<Recipe> recipes = Recipe.findAllPaged().getList();
        if(recipes.size() == 0) {
            return Results.notFound(messages.at("noRecipesDB"));
        }

        return okResponseListRecipes(req, recipes);
    }

    public Result deleteById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound(messages.at("recipeNotFound"));
        }

        r.delete();
        return Results.ok(messages.at("recipeDeleted"));
    }

    public Result updateRecipe(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Recipe r = Recipe.findById(Long.valueOf(id));
        if(r == null) {
            return Results.notFound(messages.at("recipeNotFound"));
        }

        Form<SearchUpdateRecipeResource> recipeForm = formFactory.form(SearchUpdateRecipeResource.class).bindFromRequest(req);

        SearchUpdateRecipeResource recipeResourceReq;

        if(recipeForm.hasErrors()) {
            return Results.badRequest(recipeForm.errorsAsJson());
        } else {
            recipeResourceReq = recipeForm.get();
        }
        recipeResourceReq.completeResource(r);
        Recipe recipeRequest = recipeResourceReq.toModel();
        if(!r.getName().equals(recipeRequest.getName())) {
            if(Recipe.findByName(recipeRequest.getName()) != null) {
                return Results.badRequest(messages.at("incorrectRecipeName"));
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
        Messages messages = messagesApi.preferred(req);
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

    public Result okResponseRecipe(Http.Request req, Recipe r) {
        Messages messages = messagesApi.preferred(req);
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result okResponseListRecipes(Http.Request req, List<Recipe> recipes) {
        Messages messages = messagesApi.preferred(req);
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }
        return res;
    }

}
