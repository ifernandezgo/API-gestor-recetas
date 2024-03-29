package views;

import models.Recipe;
import play.data.validation.Constraints;
import play.libs.F;

public class RecipeValidator extends Constraints.Validator<String>{
    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<String, Object[]>(
                "Esta receta ya existe por lo que no puede ser creada nuevamente", new Object[]{""}
        );
    }

    @Override
    public boolean isValid(String value) {
        return Recipe.findByName(value) == null;
    }
}
