package custom.striker.gui.components.optionSelector;

import custom.striker.gui.ContentPanel;
import custom.striker.gui.ScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Component that displays a list of {@link Option}s and allows the user to select one.
 */
public class OptionSelector extends ContentPanel implements ActionListener {

    private Option selectedOption;

    public OptionSelector() {
        setLayout(new BorderLayout());
    }

    public void setOptions(List<Option> options) {
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
        ScrollPane sp = new ScrollPane(optionPanel);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    public Option getSelectedOption() {
        return selectedOption;
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

                selectedOption = (Option) e.getSource();
            }
        }
    }
}
