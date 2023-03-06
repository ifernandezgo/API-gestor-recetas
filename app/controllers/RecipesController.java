package controllers;

import models.Category;
import models.Ingredient;
import models.Recipe;
import models.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.RecipeResource;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class RecipesController extends Controller {

    public Result createDemo(Http.Request req) throws IOException, ParseException {
        //Vaciar la bd
        this.emptyDb();

        //Rellenar con los datos nuevos
        Object o = new JSONParser().parse(new FileReader("./public/data.json"));
        JSONArray data = (JSONArray) o;
        for (Object obj : data) {
            RecipeResource rs = new RecipeResource((JSONObject) obj);
            if (Type.findByName(rs.getType()) == null) {
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

}
