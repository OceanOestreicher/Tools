package custom.striker.gui.form.validation;

import custom.striker.gui.form.ValidatedComponent;

import java.util.function.Function;

/**
 * Schema for validating the component of type R
 * @param <R> The type of the required input to validate
 * @param <O> The type of the optional arguments used by the validation function
 */
public class Schema<R extends ValidatedComponent, O> {

    private final ValidationArguments<R, O> arguments;
    private final Function<ValidationArguments<R, O>, Boolean> schemaFunction;

    public Schema(ValidationArguments<R, O> arguments, Function<ValidationArguments<R, O>, Boolean> schemaFunction) {
        this.arguments = arguments;
        this.schemaFunction = schemaFunction;
    }

    public ValidationArguments<R, O> getArguments() {
        return arguments;
    }

    public Function<ValidationArguments<R, O>, Boolean> getSchemaFunction() {
        return schemaFunction;
    }
}
