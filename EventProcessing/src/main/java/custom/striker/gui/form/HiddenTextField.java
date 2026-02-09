package custom.striker.gui.form;

import java.awt.event.*;
import custom.striker.gui.Timer;

/**
 * {@link FormTextField} which will mask user input. After a character is typed, it will be displayed
 * for 1 second or until the next character is typed.
 */
public class HiddenTextField extends FormTextField {

    private static final int TIMER_DELAY_MS = 1 * 1000; // 1 second to show last character

    private StringBuilder currentText = new StringBuilder();
    private final Timer timer;

    public HiddenTextField() {
        this("", 0);
    }

    public HiddenTextField(String defaultString) {
        this(defaultString, 0);
    }

    public HiddenTextField(int columns) {
        this("", columns);
    }

    public HiddenTextField(String defaultString, int columns) {
        super(defaultString, columns);

        timer = new Timer(TIMER_DELAY_MS, this::maskText);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Doesn't correctly handle insertions or deletion insertions
                if (e.isActionKey() || Character.isISOControl(e.getKeyChar())) {
                    return;
                }

                timer.restart();
                setText("*".repeat(currentText.length()));
                currentText.append(e.getKeyChar());
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && e.getKeyCode() != KeyEvent.VK_DELETE) {
                    return;
                }

                timer.stop();
                int selectionStart = HiddenTextField.this.getSelectionStart();
                int selectionEnd = HiddenTextField.this.getSelectionEnd();

                if (selectionStart == selectionEnd) {
                    selectionStart = Math.max(selectionStart - 1, 0);
                }

                currentText.delete(selectionStart, selectionEnd);
            }
        });

        addHierarchyListener(e -> {
            long f = e.getChangeFlags();

            // When the parent window is disposed, shutdown the timer so that the application can exit
            if ((f & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!HiddenTextField.this.isDisplayable()) {
                    timer.shutdown();
                    return;
                }
            }

            // If the window is just hidden, stop the timer
            if ((f & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (HiddenTextField.this.isShowing() && !currentText.isEmpty()) {
                    timer.restart();
                } else {
                    timer.stop();
                }
            }
        });
    }

    /**
     * Whether to show the masked text
     * @param show Should the text be shown
     */
    public void showHiddenText(boolean show) {
        if (show) {
            setText(getText());
        } else {
            setText("*".repeat(currentText.length()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resetField() {
        super.resetField();
        timer.stop();
        currentText = new StringBuilder();
    }

    @Override
    public String getText() {
        if (currentText.isEmpty()) {
            return super.getText();
        }
        return currentText.toString();
    }

    private void maskText() {
        setText("*".repeat(currentText.length()));
    }
}
