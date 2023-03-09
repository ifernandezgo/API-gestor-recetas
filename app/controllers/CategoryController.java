package controllers;

import models.Category;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
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

    @Inject
    MessagesApi messagesApi;

    public Result createCategory(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
        Form<CategoryResource> categroyForm = formFactory.form(CategoryResource.class).bindFromRequest(req);

        CategoryResource categoryResource = new CategoryResource();

        if(categroyForm.hasErrors()) {
            return Results.badRequest(categroyForm.errorsAsJson());
        } else {
            categoryResource = categroyForm.get();
        }

        Category category = Category.findByName(categoryResource.getName());
        if(category != null) {
            return Results.badRequest(messages.at("incorrectCategoryName"));
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result getCategoryById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound(messages.at("categoryNotFound"));
        }

        Result res;
        CategoryResource categoryResource = new CategoryResource(c);
        if (req.accepts("application/json")) {
            res = Results.ok(categoryResource.toJson()).as("application/json");
        } else if (req.accepts("application/xml")) {
            res = Results.ok(views.xml.category.render(categoryResource.getId().intValue(), categoryResource.getName())).as("application/xml");
        } else {
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result deleteCategoryById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound(messages.at("categoryNotFound"));
        }

        if(c.getRecipes().size() != 0) {
            return Results.forbidden(messages.at("categoryIncorrectDelete"));
        } else {
            c.delete();
            return Results.ok(messages.at("categoryDeleted"));
        }
    }

    public Result updateCategoryById(Http.Request req, Integer id) {
        Messages messages = messagesApi.preferred(req);
        Category c = Category.findById(Long.valueOf(id));
        if(c == null) {
            return Results.notFound(messages.at("categoryNotFound"));
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }

        return res;
    }

    public Result getAllCategories(Http.Request req) {
        Messages messages = messagesApi.preferred(req);
        List<Category> categories = Category.findAllPaged();
        if(categories == null) {
            return Results.notFound(messages.at("noCategoriesDB"));
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
            res = Results.unsupportedMediaType(messages.at("unsupportedMedia"));
        }
        return res;
    }
}
