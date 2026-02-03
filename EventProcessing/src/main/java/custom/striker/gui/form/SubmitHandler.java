package custom.striker.gui.form;

/**
 * Interface for a handler which is called when a {@link Form} is submitted
 */
public interface SubmitHandler {

    void handleSubmit(boolean formIsValid);
}
