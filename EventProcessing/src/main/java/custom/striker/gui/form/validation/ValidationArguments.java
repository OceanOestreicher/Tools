package custom.striker.gui.form.validation;

import custom.striker.gui.form.ValidatedComponent;

import java.util.List;

/**
 * Represents the arguments a function uses in a {@link Schema}
 * @param <R> The type of the required input. This is the type of the input that will be validated
 * @param <O> The type of optional arguments to be used when validating the required input
 */
public class ValidationArguments <R extends ValidatedComponent, O> {

    private List<O> optionalArguments;
    private final R requiredArgument;

    public ValidationArguments(R requiredArgument) {
        this.requiredArgument = requiredArgument;
    }

    public void setOptionalArguments(List<O> optionalArguments) {
        this.optionalArguments = optionalArguments;
    }

    public List<O> getOptionalArguments() {
        if (optionalArguments == null) {
            return null;
        }
        return  optionalArguments;
    }

    public R getRequiredArgument() {
        return requiredArgument;
    }
}
