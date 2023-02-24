package models;

import io.ebean.Expr;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.PagedList;
import io.ebeaninternal.server.util.Str;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Recipe extends BaseModel {

    private static final Finder<Long, Recipe> find = new Finder<>(Recipe.class);

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Category> categories;

    @ManyToOne
    private Type type;

    private Integer time;

    private String description;

    public void addIngredient(Ingredient ingredient) {
        if(this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }

        this.ingredients.add(ingredient);
        ingredient.addRecipe(this);
    }

    public void addCategory(Category category) {
        if(this.categories == null) {
            this.categories = new ArrayList<>();
        }

        this.categories.add(category);
        category.addRecipe(this);
    }

    public static Recipe findById(Long id) {
        return find.byId(id);
    }

    public static Recipe findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    } 

    public static List<Recipe> searchRecipes(String nameReq, List<String> ingredientsList, List<String> categoriesList, String typeName, Integer duration) {

        ExpressionList<Recipe> query = find.query().where();
        /*Query q = find.createQuery(Recipe.class);
        Junction<YourClass> junction;*/

        query.eqIfPresent("name", nameReq);

        if(ingredientsList != null) {
            //System.out.println(ingredientsList.get(0) + " " + ingredientsList.get(1));
            //query = query.eq("ingredients.name", ingredientsList.get(0)).eq("ingredients.name", ingredientsList.get(1));
            //query.eq("ingredients.name", ingredientsList.get(0));//.eq("ingredients.name", ingredientsList.get(1));
            //query = query.eq("ingredients.name", ingredientsList.get(1));
            //query = query.where().contains("ingredients.name", ingredientsList.get(1));
            //query.conjunction().eq("ingredients.name", ingredientsList.get(0)).and().eq("ingredients.name", ingredientsList.get(1)).endJunction();
            Ingredient ing;
            for(String i: ingredientsList) {
                //ing = Ingredient.findByName(i);
                query.eq("ingredients.name", i);
                System.out.println(i);
                System.out.println(query.findList().size());
            }
            /*Map<String, Object> map = new HashMap<>();
            for(String i : ingredientsList) {
                Ingredient ing = Ingredient.findByName(i);
                map.put("ingredients", ing);
            }
            query.allEq(map);*/
            //query.arrayContains("ingredients.name", ingredientsList);
            //query.eq("ingredients.name", ingredientsList);
            //query.add(Expr.contains("ingredients.name", ingredientsList.get(0)));
            //query.add(Expr.contains("ingredients.name", ingredientsList.get(1)));
        }

        if(categoriesList != null) {
            for(String c : categoriesList) {
                System.out.println(c);
                query.eq("categories.name", c);
                System.out.println(query.findList().size());
            }
        }

        query.eqIfPresent("type.type", typeName);

        if(duration != null) {
            query.le("time", duration);
        }

        return query.findList();
    }

    public static List<Recipe> findAll() { return find.all(); }

    public static PagedList<Recipe> findAllPaged() { return find.query().where().setMaxRows(10).setFirstRow(0).findPagedList(); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
