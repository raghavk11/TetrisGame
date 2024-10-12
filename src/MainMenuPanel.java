import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {
    private final TetrisGame parentFrame;

    public MainMenuPanel(TetrisGame parentFrame, final int width, final int height) {
        this.parentFrame = parentFrame;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.parentFrame.setBounds(x, y, width, height);
        parentFrame.setSize(width, height); // Set width & height of screen
        setLayout(new BorderLayout());

        // Create a top panel for title and icon
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Add Tetris icon to the top
        JLabel gameIcon = new JLabel(resizeIcon("Image/Buttons/tetris_icon.png", 64, 64)); // Resize to 64x64
        topPanel.add(gameIcon);

        // Title label
        JLabel titleLabel = new JLabel("Tetris Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); // Increase font size
        titleLabel.setForeground(Color.DARK_GRAY); // Set font color
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        // Add "Main Menu" title above buttons
        JLabel mainMenuLabel = new JLabel("Main Menu");
        mainMenuLabel.setFont(new Font("Arial", Font.BOLD, 28));
        mainMenuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(mainMenuLabel, BorderLayout.CENTER);

        // Center panel for buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increase padding for buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Play button with hover effect and resized icon
        JButton playButton = createStyledButton("Play", "Image/Buttons/play_icon.png", 48, 48);
        playButton.addActionListener(e -> parentFrame.startGame());
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(playButton, gbc);

        // Add Settings button with hover effect and resized icon
        JButton settingsButton = createStyledButton("Settings", "Image/Buttons/settings_icon.png", 48, 48);
        settingsButton.addActionListener(e -> parentFrame.showConfigPanel());
        gbc.gridy = 1;
        centerPanel.add(settingsButton, gbc);

        // Add High Score button with hover effect and resized icon
        JButton highScoresButton = createStyledButton("High Score", "Image/Buttons/scores_icon.png", 48, 48);
        highScoresButton.addActionListener(e -> parentFrame.showHighScores());
        gbc.gridy = 2;
        centerPanel.add(highScoresButton, gbc);

        // Add Exit button with hover effect and resized icon
        JButton exitButton = createStyledButton("Exit", "Image/Buttons/exit_icon.png", 48, 48);
        exitButton.addActionListener(e -> parentFrame.exitGame());
        gbc.gridy = 3;
        centerPanel.add(exitButton, gbc);

        // Increase size of buttons
        playButton.setPreferredSize(new Dimension(300, 60));
        settingsButton.setPreferredSize(new Dimension(300, 60));
        highScoresButton.setPreferredSize(new Dimension(300, 60));
        exitButton.setPreferredSize(new Dimension(300, 60));

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for author information
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel authorLabel = new JLabel("Author: Group 19");
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 16)); // Italic font for the author
        authorLabel.setForeground(Color.GRAY); // Gray color for author label
        bottomPanel.add(authorLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Helper method to create buttons with hover effect
    private JButton createStyledButton(String text, String iconPath, int iconWidth, int iconHeight) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 20)); // Larger font size
        button.setIcon(resizeIcon(iconPath, iconWidth, iconHeight)); // Resizing the icon
        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of the icon
        button.setBackground(Color.WHITE); // White background
        button.setForeground(Color.DARK_GRAY); // Dark gray text
        button.setFocusPainted(false); // Remove focus outline
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2)); // Add border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 220, 220)); // Light gray on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE); // Back to white when not hovered
            }
        });

        return button;
    }

    // Helper method to resize the icon to the given dimensions
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage(); // Transform the icon to an image
        Image newImg = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Scale smoothly
        return new ImageIcon(newImg); // Return the resized icon
    }
}
