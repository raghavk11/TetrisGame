import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ConfigPanel extends JPanel {
    // Instance created at class load time
    private static final ConfigPanel instance = new ConfigPanel();

    // Private constructor to prevent other classes from creating a new instance
    private ConfigPanel() {
    }

    // Public Method to access the instance
    public static ConfigPanel getInstance() {
        return instance;
    }

    // Public member function
    public void SetupPanel(TetrisGame parentFrame) {
        removeAll();
        setLayout(new GridBagLayout()); // Set layout to GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components for spacing

        // Title label configuration
        JLabel titleLabel = new JLabel("Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Larger title font for better visibility
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the title
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset grid width for the rest of the components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Field Width slider
        JLabel fieldWidthLabel = new JLabel("Field Width (No of cells):");
        fieldWidthLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(fieldWidthLabel, gbc);

        JSlider fieldWidthSlider = new JSlider(5, 15, parentFrame.getFieldWidth());
        fieldWidthSlider.setMajorTickSpacing(1);
        fieldWidthSlider.setPaintLabels(true);
        fieldWidthSlider.setPaintTicks(true);
        fieldWidthSlider.setPreferredSize(new Dimension(250, 50)); // Set preferred size for the slider
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(fieldWidthSlider, gbc);

        // Field Height slider
        JLabel fieldHeightLabel = new JLabel("Field Height (No of cells):");
        fieldHeightLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(fieldHeightLabel, gbc);

        JSlider fieldHeightSlider = new JSlider(15, 30, parentFrame.getFieldHeight());
        fieldHeightSlider.setMajorTickSpacing(1);
        fieldHeightSlider.setPaintLabels(true);
        fieldHeightSlider.setPaintTicks(true);
        fieldHeightSlider.setPreferredSize(new Dimension(250, 50)); // Set preferred size for the slider
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(fieldHeightSlider, gbc);

        // Game Level slider
        JLabel gameLevelLabel = new JLabel("Game Level:");
        gameLevelLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(gameLevelLabel, gbc);

        JSlider gameLevelSlider = new JSlider(1, 10, parentFrame.getGameLevel());
        gameLevelSlider.setMajorTickSpacing(1);
        gameLevelSlider.setPaintLabels(true);
        gameLevelSlider.setPaintTicks(true);
        gameLevelSlider.setPreferredSize(new Dimension(250, 50)); // Set preferred size for the slider
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(gameLevelSlider, gbc);

        // Music checkbox
        JCheckBox musicCheckBox = new JCheckBox("Music (On|Off)", parentFrame.isMusicOn());
        musicCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Take up two columns
        add(musicCheckBox, gbc);

        // Sound effect checkbox
        JCheckBox soundEffectCheckBox = new JCheckBox("Sound Effect (On|Off)", parentFrame.isSoundEffectOn());
        soundEffectCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(soundEffectCheckBox, gbc);

        // Extend Mode checkbox
        JCheckBox extendModeCheckBox = new JCheckBox("Extend Mode (On|Off)", parentFrame.isExtendMode());
        extendModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(extendModeCheckBox, gbc);

        // Player One Type radio buttons
        JLabel playerOneTypeLabel = new JLabel("Player One Type:");
        playerOneTypeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        add(playerOneTypeLabel, gbc);

        JRadioButton human1Button = new JRadioButton("Human");
        JRadioButton ai1Button = new JRadioButton("AI");
        JRadioButton external1Button = new JRadioButton("External");
        ButtonGroup playerOneTypeGroup = new ButtonGroup();
        playerOneTypeGroup.add(human1Button);
        playerOneTypeGroup.add(ai1Button);
        playerOneTypeGroup.add(external1Button);

        // Retrieve and set the correct selection for Player One
        String playerOneType = parentFrame.getPlayerOneType();
        if (playerOneType.equals("Human")) {
            human1Button.setSelected(true);
        } else if (playerOneType.equals("AI")) {
            ai1Button.setSelected(true);
        } else {
            external1Button.setSelected(true);
        }

        JPanel playerOneTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerOneTypePanel.add(human1Button);
        playerOneTypePanel.add(ai1Button);
        playerOneTypePanel.add(external1Button);

        gbc.gridx = 1;
        gbc.gridy = 7;
        add(playerOneTypePanel, gbc);

        // Player Two Type radio buttons
        JLabel playerTwoTypeLabel = new JLabel("Player Two Type:");
        playerTwoTypeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(playerTwoTypeLabel, gbc);

        JRadioButton human2Button = new JRadioButton("Human");
        JRadioButton ai2Button = new JRadioButton("AI");
        JRadioButton external2Button = new JRadioButton("External");
        ButtonGroup playerTwoTypeGroup = new ButtonGroup();
        playerTwoTypeGroup.add(human2Button);
        playerTwoTypeGroup.add(ai2Button);
        playerTwoTypeGroup.add(external2Button);

        // Retrieve and set the correct selection for Player Two
        String playerTwoType = parentFrame.getPlayerTwoType();
        if (playerTwoType.equals("Human")) {
            human2Button.setSelected(true);
        } else if (playerTwoType.equals("AI")) {
            ai2Button.setSelected(true);
        } else {
            external2Button.setSelected(true);
        }

        JPanel playerTwoTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerTwoTypePanel.add(human2Button);
        playerTwoTypePanel.add(ai2Button);
        playerTwoTypePanel.add(external2Button);

        gbc.gridx = 1;
        gbc.gridy = 8;
        add(playerTwoTypePanel, gbc);

        // Disable Player Two Type by default if Extend Mode is not selected
        boolean isExtendMode = parentFrame.isExtendMode();
        playerTwoTypePanel.setEnabled(isExtendMode);
        human2Button.setEnabled(isExtendMode);
        ai2Button.setEnabled(isExtendMode);
        external2Button.setEnabled(isExtendMode);

        // Enable or disable Player Two Type based on Extend Mode checkbox state with
        // lambda function
        extendModeCheckBox.addItemListener(e -> {
            boolean isEnabled = extendModeCheckBox.isSelected();
            playerTwoTypePanel.setEnabled(isEnabled);
            human2Button.setEnabled(isEnabled);
            ai2Button.setEnabled(isEnabled);
            external2Button.setEnabled(isEnabled);
        });

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 16));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> {
            // Get values from sliders and checkboxes
            int width = fieldWidthSlider.getValue();
            int height = fieldHeightSlider.getValue();
            int level = gameLevelSlider.getValue();
            boolean music = musicCheckBox.isSelected();
            boolean soundEffect = soundEffectCheckBox.isSelected();
            boolean extendMode = extendModeCheckBox.isSelected();

            // Get player type selections
            String selectedPlayerOneType = human1Button.isSelected() ? "Human"
                    : ai1Button.isSelected() ? "AI" : "External";
            String selectedPlayerTwoType = human2Button.isSelected() ? "Human"
                    : ai2Button.isSelected() ? "AI" : "External";

            // Update settings in the parent frame
            parentFrame.updateSettings(width, height, level, music, soundEffect, extendMode);
            parentFrame.updatePlayerTypes(selectedPlayerOneType, selectedPlayerTwoType);

            // Return to the main menu after saving settings
            parentFrame.showMainMenu();
        });

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(saveButton, gbc);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 16));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> parentFrame.showMainMenu());
        gbc.gridx = 1;
        gbc.gridy = 9;
        add(cancelButton, gbc);
    }
}
