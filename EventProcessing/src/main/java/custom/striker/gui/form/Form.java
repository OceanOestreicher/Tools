package custom.striker.gui.form;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.form.validation.ValidationSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a form component.
 */
public abstract class Form extends ContentPanel {

    private ValidationSchema validationSchema;

    private final List<SubmitHandler> handlers = new ArrayList<>();

    /**
     * Register a {@link ValidationSchema} that is run when the onSubmit method is called.
     * @param schema The validation schema to use for this form.
     */
    protected void registerSchema(ValidationSchema schema) {
        validationSchema = schema;
    }

    protected void onSubmit() {
        boolean isValid = true;

        if (validationSchema != null) {
            isValid = validationSchema.validate();
        }

        for (SubmitHandler handler: handlers) {
            handler.handleSubmit(isValid);
        }
    }

    /**
     * Registers a handler which is run whenever the form is submitted
     * @param handler The handler to run
     */
    public void registerSubmitHandler(SubmitHandler handler) {
        handlers.add(handler);
    }
}
