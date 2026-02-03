package custom.striker.gui.form;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * JTextField for use within a {@link Form}
 */
public class FormTextField extends JTextField implements ValidatedComponent {

    private final static Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");
    private final static Border DEFAULT_INVALID_BORDER = new LineBorder(Color.RED);
    protected final String defaultText;
    private boolean isValid = true;
    private Border validBorder, invalidBorder;

    public FormTextField() {
        this("", 0);
    }

    public FormTextField(String defaultString) {
        this(defaultString, 0);
    }

    public FormTextField(int columns) {
        this("", columns);
    }

    public FormTextField(String defaultString, int columns) {
        super(defaultString, columns);
        this.defaultText = defaultString;

        if (defaultText != null && !defaultText.isEmpty()) {
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (FormTextField.this.getText().equals(defaultText)) {
                        FormTextField.this.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (FormTextField.this.getText().isEmpty()) {
                        FormTextField.this.setText(defaultText);
                    }
                }
            });
        }
        validBorder = DEFAULT_BORDER;
        invalidBorder = DEFAULT_INVALID_BORDER;
    }

    public void resetField() {
        setText(defaultText);
    }

    public void setValidBorder(Border border) {
        validBorder = border;
    }

    public void setInvalidBorder(Border border) {
        invalidBorder = border;
    }

    @Override
    public void componentIsValid(boolean isValid) {
        if (isValid == this.isValid) {
            return;
        }

        this.isValid = isValid;

        Border newBorder = validBorder;

        if (!isValid) {
            newBorder = invalidBorder;
        }
        setBorder(newBorder);
    }
}
