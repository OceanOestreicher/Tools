package custom.striker.gui.components.optionSelector;

/**
 * Functional interface for actions to be performed when an option is selected in the {@link OptionSelector}
 */
@FunctionalInterface
public interface OptionAction {

    void execute();
}
