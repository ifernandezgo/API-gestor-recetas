package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.ArrayList;

public class RecipeController extends Controller {

    ArrayList<String> recetas = new ArrayList<>();

    public Result crearReceta(Http.Request req) {
        recetas.add(req.queryString("name").orElse(""));
        return Results.created(recetas.toString());
    }

    public Result getAllRecipes() {
        return Results.ok(recetas.toString());
    }
}
