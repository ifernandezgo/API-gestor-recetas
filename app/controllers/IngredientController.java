package controllers;

import models.Ingredient;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.IngredientResource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngredientController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    MessagesApi messagesApi;

    public Result createIngredient(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
        Form<IngredientResource> ingredientForm = formFactory.form(IngredientResource.class).bindFromRequest(req);

        IngredientResource ingredientResource = new IngredientResource();

        if(ingredientForm.hasErrors()) {
            return Results.badRequest(ingredientForm.errorsAsJson());
        } else {
            ingredientResource = ingredientForm.get();
        }
        Ingredient ingredient = Ingredient.findByName(ingredientResource.getName());
        if(ingredient != null) {
            return Results.badRequest(messages.at("incorrectIngredientName"));
        }
        ingredient = ingredientResource.toModel();
        ingredient.save();

        Result res;
        ingredientResource = new IngredientResource(ingredient);
        if (req.accepts("application/json")) {
            res = Results.created(ingredientResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.created(views.xml.ingredient.render(ingredientResource.getId().intValue(), ingredientResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result getIngredientById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Ingredient i = Ingredient.findById(Long.valueOf(id));
        if(i == null) {
            return Results.notFound(messages.at("ingredientNotFound"));
        }

        Result res;
        IngredientResource ingredientResource = new IngredientResource(i);
        if (req.accepts("application/json")) {
            res = Results.ok(ingredientResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.ingredient.render(ingredientResource.getId().intValue(), ingredientResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result deleteIngredientById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Ingredient i = Ingredient.findById(Long.valueOf(id));
        if(i == null) {
            return Results.notFound(messages.at("ingredientNotFound"));
        }

        if(i.getRecipes().size() != 0) {
            return Results.forbidden(messages.at("ingredientIncorrectDelete"));
        } else {
            i.delete();
            return Results.ok(messages.at("ingredientDeleted"));
        }
    }

    public Result updateIngredientById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Ingredient i = Ingredient.findById(Long.valueOf(id));
        if(i == null) {
            return Results.notFound(messages.at("ingredientNotFound"));
        }

        Form<IngredientResource> ingredientForm = formFactory.form(IngredientResource.class).bindFromRequest(req);

        IngredientResource ingredientResource = new IngredientResource();

        if(ingredientForm.hasErrors()) {
            return Results.badRequest(ingredientForm.errorsAsJson());
        } else {
            ingredientResource = ingredientForm.get();
        }

        i.setName(ingredientResource.getName());
        i.update();

        Result res;
        ingredientResource = new IngredientResource(i);
        if (req.accepts("application/json")) {
            res = Results.ok(ingredientResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.ingredient.render(ingredientResource.getId().intValue(), ingredientResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;

    }

    public Result getAllIngredients(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
        List<Ingredient> ingredients = Ingredient.findAllPaged();
        if(ingredients.size() == 0) {
            return Results.notFound(messages.at("noIngredientsDB"));
        }

        List<IngredientResource> resources = ingredients.
                stream().
                map(IngredientResource::new).
                collect(Collectors.toList());

        Result res;

        if(req.accepts("application/json")) {
            res = Results.ok(Json.toJson(resources)).as("application/json");
        } else if(req.accepts("application/xml")) {
            List<Integer> ids = new ArrayList<>();
            Map<Integer, String> names = new LinkedHashMap<>();
            for(IngredientResource ing: resources) {
                Integer id = ing.getId().intValue();
                ids.add(id);
                names.put(id, ing.getName());
            }
            res = Results.ok(views.xml.ingredients.render(ids, names)).as("application/xml");
        } else {
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }
}
