package controllers;

import models.Ingredient;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.IngredientResource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientController extends Controller {

    @Inject
    private FormFactory formFactory;

    public Result crearIngrediente(Http.Request req) {
        Form<IngredientResource> ingredientForm = formFactory.form(IngredientResource.class).bindFromRequest(req);

        IngredientResource ingredientResource = new IngredientResource();

        if(ingredientForm.hasErrors()) {
            return Results.badRequest(ingredientForm.errorsAsJson());
        } else {
            ingredientResource = ingredientForm.get();
        }
        Ingredient ingredient = Ingredient.findByName(ingredientResource.getName());
        if(ingredient != null) {
            return Results.badRequest("Este ingrediente ya existe por lo que no puede ser creado nuevamente");
        }
        ingredient = ingredientResource.toModel();
        ingredient.save();

        return Results.created("El ingrediente ha sido creado de forma correcta");
    }

    public Result getAllIngredients(Http.Request req) {
        List<Ingredient> ingredients = Ingredient.findAllIngredientsPaged().getList();
        if(ingredients.size() == 0) {
            return Results.notFound("Todavía no hay ningún ingrediente en la base de datos");
        }

        List<IngredientResource> resources = ingredients.
                stream().
                map(IngredientResource::new).
                collect(Collectors.toList());

        Result res;

        if(req.accepts("application/json")) {
            res = Results.ok(Json.toJson(resources)).as("application/json");
        } else if(req.accepts("application/xml")) {
            List<String> names = new ArrayList<>();
            for(IngredientResource ing: resources) {
                names.add(ing.getName());
            }
            res = Results.ok(views.xml.ingredients.render(names)).as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }
}
