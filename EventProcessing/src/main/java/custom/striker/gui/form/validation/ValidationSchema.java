package custom.striker.gui.form.validation;

import custom.striker.gui.form.ValidatedComponent;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class that represents a validation schema to be used to provide validation for various
 * {@link ValidatedComponent}s. A {@link ValidatedComponent} can register one or more {@link Schema}s
 * to the {@link ValidationSchema}, which can then be validated as a whole by calling the
 * {@link #validate()} method.
 */
public final class ValidationSchema {

    private final Map<ValidatedComponent, List<Schema<?, ?>>> schemas;

    public ValidationSchema() {
        schemas = new HashMap<>();
    }

    public boolean validate() {
        boolean isValid = true;

        for (Map.Entry<ValidatedComponent, List<Schema<?, ?>>> componentSchemas: schemas.entrySet()) {
            ValidatedComponent component = componentSchemas.getKey();
            List<Schema<?, ?>> schemaList = componentSchemas.getValue();

            for (Schema<?, ?> schema: schemaList) {
                isValid = schemaValidated(schema);
                component.componentIsValid(isValid);

                if (!isValid) {
                    Toolkit.getDefaultToolkit().beep();
                    break;
                }
            }
        }

        return isValid;
    }

    private <R extends ValidatedComponent, O> boolean schemaValidated(Schema<R, O> schema) {
        ValidationArguments<R, O> arguments = schema.getArguments();
        var schemaFunction = schema.getSchemaFunction();
        return schemaFunction.apply(arguments);
    }

    public void registerSchema(ValidatedComponent component, Schema<?, ?> schema) {
        if (!schemas.containsKey(component)) {
            schemas.put(component, new LinkedList<>());
        }

        var validationFunctions = schemas.get(component);

        validationFunctions.add(schema);
    }
}
