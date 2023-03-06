package controllers;

import models.Category;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.CategoryResource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryController {

    @Inject
    private FormFactory formFactory;

    public Result createCategory(Http.Request req) {
        Form<CategoryResource> categroyForm = formFactory.form(CategoryResource.class).bindFromRequest(req);

        CategoryResource categoryResource = new CategoryResource();

        if(categroyForm.hasErrors()) {
            return Results.badRequest(categroyForm.errorsAsJson());
        } else {
            categoryResource = categroyForm.get();
        }

        Category category = Category.findByName(categoryResource.getName());
        if(category != null) {
            return Results.badRequest("Esta categoría ya existe por lo que no puede ser creado nuevamente");
        }
        category = categoryResource.toModel();
        category.save();

        Result res;
        categoryResource = new CategoryResource(category);
        if (req.accepts("application/json")) {
            res = Results.created(categoryResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.created(views.xml.category.render(categoryResource.getId().intValue() ,categoryResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }

    public Result getCategoryById(Http.Request req, Integer id) {
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound("No existe ninguna categoría con ese id en la base de datos. Pruebe con otra");
        }

        Result res;
        CategoryResource categoryResource = new CategoryResource(c);
        if (req.accepts("application/json")) {
            res = Results.ok(categoryResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.category.render(categoryResource.getId().intValue(), categoryResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }

    public Result deleteCategoryById(Http.Request req, Integer id) {
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound("No existe ninguna categoría con ese id en la base de datos. Pruebe con otra");
        }

        if(c.getRecipes().size() != 0) {
            return Results.forbidden("La categoría no puede ser eliminada porque pertenece a una o varias recetas");
        } else {
            c.delete();
            return Results.ok("La categoría con id " + id + " ha sido eliminada correctamente");
        }
    }

    public Result updateCategoryById(Http.Request req, Integer id) {
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound("No existe ninguna categoría con ese id en la base de datos. Pruebe con otra");
        }

        Form<CategoryResource> categoryForm = formFactory.form(CategoryResource.class).bindFromRequest(req);
        CategoryResource categoryResource = new CategoryResource();

        if(categoryForm.hasErrors()) {
            return Results.badRequest(categoryForm.errorsAsJson());
        } else {
            categoryResource = categoryForm.get();
        }

        c.setName(categoryResource.getName());
        c.update();

        Result res;
        categoryResource = new CategoryResource(c);
        if (req.accepts("application/json")) {
            res = Results.ok(categoryResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.category.render(categoryResource.getId().intValue(), categoryResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }

        return res;
    }

    public Result getAllCategories(Http.Request req) {
        List<Category> categories = Category.findAllPaged();
        if(categories == null) {
            return Results.notFound("No hay ninguna categoría guardada en la base de datos");
        }

        Result res;
        List<CategoryResource> resources = categories.
                stream().
                map(CategoryResource::new).
                collect(Collectors.toList());

        if(req.accepts("application/json")) {
            res = Results.ok(Json.toJson(resources)).as("application/json");
        } else if(req.accepts("application/xml")) {
            List<Integer> ids = new ArrayList<>();
            Map<Integer, String> names = new LinkedHashMap<>();
            for(CategoryResource cr : resources) {
                Integer id = cr.getId().intValue();
                ids.add(id);
                names.put(id, cr.getName());
            }
            res = Results.ok(views.xml.categories.render(ids, names)).as("application/xml");
        } else {
            res = Results.unsupportedMediaType("Solo podemos devolver los datos en formato json o xml");
        }
        return res;
    }
}
