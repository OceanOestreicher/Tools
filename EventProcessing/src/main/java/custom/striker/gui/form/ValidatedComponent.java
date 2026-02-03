package custom.striker.gui.form;

import custom.striker.gui.form.validation.ValidationSchema;

/**
 * Interface for a component which is validated by a {@link ValidationSchema}
 */
public interface ValidatedComponent {

    void componentIsValid(boolean isValid);
}
