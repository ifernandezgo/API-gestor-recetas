package views;

import models.Type;
import play.data.validation.Constraints;
import play.libs.F;

public class TypeValidator extends Constraints.Validator<String> {

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<String, Object[]>(
          "El tipo de la receta debe ser: Desayuno, Comida o Cena", new Object[]{""}
        );
    }

    @Override
    public boolean isValid(String value) {
        return Type.enumContains(value);
    }
}
