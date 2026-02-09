package custom.striker.gui.form;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A text field that only accepts numeric input, either integer or decimal.
 */
public class NumericTextField extends FormTextField {

    public static final int INTEGER = 1;
    public static final int DECIMAL = 2;

    private final int fieldType;
    private int decimalPrecision = 2;
    private String fieldRegex;

    /**
     * Creates a default NumericTextField which accepts only the chosen fieldType's input
     * @param fieldType The type of numeric input to accept (1 for INTEGER or 2 for DECIMAL)
     */
    public NumericTextField(int fieldType) {
        this("", 0, fieldType);
    }

    /**
     * Creates a NumericTextField with a custom default value and which accepts only the chosen fieldType's input
     * @param defaultString The default string to display in the text field
     * @param fieldType The type of numeric input to accept (1 for INTEGER or 2 for DECIMAL)
     */
    public NumericTextField(String defaultString, int fieldType) {
        this(defaultString, 0, fieldType);
    }

    /**
     * Creates a NumericTextField with a custom column count and which accepts only the chosen fieldType's input
     * @param columns The number of columns to display in the text field
     * @param fieldType The type of numeric input to accept (1 for INTEGER or 2 for DECIMAL)
     */
    public NumericTextField(int columns, int fieldType) {
        this("", columns, fieldType);
    }

    /**
     * Creates a NumericTextField with a custom default value, column count and which accepts only the chosen fieldType's input
     * @param columns The number of columns to display in the text field
     * @param defaultString The default string to display in the text field
     * @param fieldType The type of numeric input to accept (1 for INTEGER or 2 for DECIMAL)
     */
    public NumericTextField(String defaultString, int columns, int fieldType) {
        super(defaultString, columns);
        this.fieldType = fieldType;

        updateFieldRegex();

        // Only accept keystrokes that will produce text matching the regex.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Allow control characters (backspace, delete, enter, arrows handled elsewhere)
                if (Character.isISOControl(c)) {
                    return;
                }

                String current = getText();
                int selStart = getSelectionStart();
                int selEnd = getSelectionEnd();
                StringBuilder sb = new StringBuilder();

                if (selStart != selEnd) {
                    // Replace selected text with the typed character
                    sb.append(current.substring(0, selStart));
                    sb.append(c);
                    sb.append(current.substring(selEnd));
                } else {
                    int pos = getCaretPosition();
                    sb.append(current.substring(0, Math.max(0, Math.min(pos, current.length()))));
                    sb.append(c);
                    sb.append(current.substring(Math.max(0, Math.min(pos, current.length()))));
                }

                String next = sb.toString();

                if (!next.matches(fieldRegex)) {
                    e.consume();
                }
            }
        });
    }

    /**
     * Returns the numeric value of the text field as an Integer or Double depending on the fieldType, or null if the
     * text is empty or invalid
     * @return The numeric value of the text field as an Integer or Double depending on the fieldType, or null if the
     * text is empty or invalid
     * @param <T> The type of numeric value to return (Integer or Double)
     */
    public <T> T getNumericValue() {
        String text = getText();
        if (isDefaultText() || text.isEmpty()) {
            return null;
        }

        try {
            if (fieldType == INTEGER) {
                return (T) Integer.valueOf(text);
            } else {
                return (T) Double.valueOf(text);
            }
        } catch (NumberFormatException e) {
            return null; // Should not happen due to regex validation, but just in case
        }
    }

    private void updateFieldRegex() {
        if (fieldType == INTEGER) {
            // Allow empty, a lone '-', or a single zero, or a non-zero digit followed by digits (no leading zeros)
            fieldRegex = "-?(?:0|[1-9]\\d*)?";
        } else {
            // Decimal: allow empty, a lone '-', numbers like 0, 0.x, non-zero integers (no leading zeros) with optional decimal part,
            // or numbers that start with a dot like .5. The decimalPrecision is respected for fractional digits.
            fieldRegex = "-?(?:(?:0|[1-9]\\d*)(?:\\.\\d{0," + decimalPrecision + "})?|\\.\\d{0," + decimalPrecision + "})?";
        }
    }

    public int getDecimalPrecision() {
        return decimalPrecision;
    }

    /**
     * If the fieldType is DECIMAL, sets the number of decimal places to allow
     * @param decimalPrecision The number of decimal places to allow
     */
    public void setDecimalPrecision(int decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
        updateFieldRegex();
    }
}
