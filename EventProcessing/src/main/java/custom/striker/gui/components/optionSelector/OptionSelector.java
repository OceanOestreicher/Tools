package custom.striker.gui.components.optionSelector;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.ScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Component that displays a list of {@link Option}s and allows the user to select one.
 */
public class OptionSelector<T extends Option> extends ContentPanel implements ActionListener {

    private T selectedOption;
    private List<T> options;
    private ScrollPane scrollPane;

    private Dimension pendingSize;
    private Integer pendingVerticalScrollBarPolicy;
    private Integer pendingHorizontalScrollBarPolicy;

    public OptionSelector() {
        setLayout(new BorderLayout());
    }

    public void setOptions(List<T> options) {
        this.options = options;
        removeAll();
        ContentPanel optionPanel = new ContentPanel();
        optionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.gridy = 0;

        for (Option option : options) {
            optionPanel.add(option, gbc);
            option.addActionListener(this);
            gbc.gridy++;
        }

        addVerticalFillPanel(gbc.gridy, optionPanel);
        scrollPane = new ScrollPane(optionPanel);

        if (pendingSize != null) {
            scrollPane.setPreferredSize(pendingSize);
        }

        if (pendingVerticalScrollBarPolicy != null) {
            scrollPane.setVerticalScrollBarPolicy(pendingVerticalScrollBarPolicy);
        }

        if (pendingHorizontalScrollBarPolicy != null) {
            scrollPane.setHorizontalScrollBarPolicy(pendingHorizontalScrollBarPolicy);
        }

        OptionSelector.this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(OptionSelector.this.scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    public void addOption(T option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
        setOptions(this.options);
    }

    public void setSelectedOption(T option) {
        if (option != null && !options.contains(option)) {
            throw new IllegalArgumentException("Option must be in the options list");
        }

        if (selectedOption != null) {
            selectedOption.setSelected(false);
        }

        selectedOption = option;

        if (selectedOption != null) {
            selectedOption.setSelected(true);
            SwingUtilities.invokeLater(() -> {
                Rectangle bounds = selectedOption.getBounds();
                scrollPane.getViewport().scrollRectToVisible(bounds);
            });
        }
    }

    public Option getSelectedOption() {
        return selectedOption;
    }

    public void setWindowSize(Dimension preferredSize) {
        if (this.scrollPane == null) {
            pendingSize = preferredSize;
            return;
        }
        scrollPane.setPreferredSize(preferredSize);
    }

    public void setVerticalScrollBarPolicy(int policy) {
        if (this.scrollPane == null) {
            pendingVerticalScrollBarPolicy = policy;
            return;
        }
        scrollPane.setVerticalScrollBarPolicy(policy);
    }

    public void setHorizontalScrollBarPolicy(int policy) {
        if (this.scrollPane == null) {
            pendingHorizontalScrollBarPolicy = policy;
            return;
        }
        scrollPane.setHorizontalScrollBarPolicy(policy);
    }

    public void removeOption(T option) {
        if (option == null || !options.contains(option)) {
            throw new IllegalArgumentException("Option must be in the options list");
        }

        if (selectedOption == option) {
            selectedOption.setSelected(false);
            selectedOption = null;
        }

        options.remove(option);
        setOptions(options);
    }

    private void addVerticalFillPanel(int gridY, ContentPanel optionPanel) {
        ContentPanel filler = new ContentPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.gridy = gridY;
        gbc.weighty = 1;
        optionPanel.add(filler, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int command = Integer.parseInt(e.getActionCommand());

        switch (command) {
            case Option.OPTION_DIRTY -> {
                revalidate();
            }
            case Option.OPTION_SELECTED -> {
                if (e.getSource() == selectedOption) {
                    return;
                }

                if (selectedOption != null) {
                    selectedOption.setSelected(false);
                }

                selectedOption = (T) e.getSource();
            }
        }
    }
}
