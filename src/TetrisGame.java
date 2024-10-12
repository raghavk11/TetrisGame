import javax.swing.*;
import com.google.gson.Gson;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TetrisGame extends JFrame {
    // Music Pointer
    private AudioPlr music; // Background music instance

    // Window default size
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 1000;

    // High score
    private List<HighScore> highScores;

    // Main Attributes
    private TetrisBoard board; // player 1s game board
    private TetrisBoard board2; // player 2s game board when in extended mode
    private int fieldWidth = 10; // default columns
    private int fieldHeight = 20; // default height
    private int gameLevel = 1; // starting level
    private boolean soundEffectOn = true; // starting with sound effects on
    private boolean extendMode = true; // extend mode

    private String playerOneType = "Human"; // AI For Testing
    private String playerTwoType = "Human"; // Human by default

    // UI components in the game panel
    private JButton playPauseButton;
    private JButton backButton;
    private JPanel buttonPanel;
    private JPanel topPanel;

    private KeyListener keyListener; // listens for user input

    // dialog for server connection issues
    private JDialog noConnectionDialog = new JOptionPane("There is no Connection! Please Setup Server First!",
            JOptionPane.INFORMATION_MESSAGE).createDialog("Message");

    // constructor for tetris game
    public TetrisGame() {
        initUI();
        this.music = new AudioPlr("SFX/originalOST.wav", true); // Initialize background music
        // Load the sound effects
        music.loadSoundEffect("erase-line", "SFX/erase-line.wav");
        music.loadSoundEffect("game-finish", "SFX/game-finish.wav");
        music.loadSoundEffect("move-turn", "SFX/move-turn.wav");
        music.loadSoundEffect("level-up", "SFX/level-up.wav");
        music.playAudio(); // Enable to play from start
        this.highScores = new ArrayList<>();
        loadHighScoresFromJSON(); // Load high scores on startup
    }

    // Getter method for AudioPlr
    public AudioPlr getAudioPlayer() {
        return music;
    }

    // initialises the main menu layout
    private void initUI() {
        showMainMenu();
    }

    // starts new game
    public void startGame() {
        getContentPane().removeAll(); // removes previous components
        removeKeyListener(keyListener); // removes key listeners

        if (board != null) {
            remove(board); // removes game board
        }

        setTitle("Tetris");
        setLayout(new BorderLayout());

        topPanel = new JPanel(new GridLayout(1, 2));

        createAndStartBoard(); // initialises player 1s game board

        // checks if in extend mode and if so initialises player 2s game board
        if (extendMode) {
            setSize(fieldWidth * 2 * squareSize(), fieldHeight * squareSize() + getInsets().top);
            createAndStartBoard2();
        } else {
            setSize(fieldWidth * squareSize(), fieldHeight * squareSize() + getInsets().top);
        }

        if (getPlayerOneType().equals("AI") || getPlayerOneType().equals("External")) {
            // Start AI-controlled game with proper Timer handling
            board.runAIControlledGame();
        }

        if (getPlayerTwoType().equals("AI") || getPlayerTwoType().equals("External")) {
            // Start AI-controlled game with proper Timer handling
            board2.runAIControlledGame();
        }

        createOptionPanel(); // Call button initialization here to ensure no nulls

        add(topPanel, BorderLayout.CENTER);
        setFocusable(true);
        requestFocusInWindow();

        // if player 1 or 2 is human this creates key listeners for handling input
        keyListener = new TAdapter();
        if (getPlayerOneType().equals("Human") || getPlayerTwoType().equals("Human")) {
            addKeyListener(keyListener);
        }

        centerFrame();
        revalidate(); // Ensure UI updates
        repaint(); // Force the game board to be visible
    }

    // creates and starts player 1s game board
    private void createAndStartBoard() {
        board = new TetrisBoard(this, fieldWidth, fieldHeight, gameLevel, isMusicOn(), soundEffectOn,
                playerOneType.equals("AI"), false, playerOneType.equals("External"));
        topPanel.add(board);
        board.startGame();
    }

    // creates and starts player 2s game board
    private void createAndStartBoard2() {
        board2 = new TetrisBoard(this, fieldWidth, fieldHeight, gameLevel, isMusicOn(), soundEffectOn,
                playerTwoType.equals("AI"), extendMode, playerTwoType.equals("External"));
        topPanel.add(board2); // adds second game to top panel
        board2.startGame(); // starts second game
    }

    // center game window on the screen
    private void centerFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - getWidth()) / 2;
        int y = (screen.height - getHeight()) / 2;
        setLocation(x, y);
    }

    // calculates square sizes based on window size
    private int squareSize() {
        return Math.min(getWidth() / fieldWidth, (getHeight() - getInsets().top) / fieldHeight);
    }

    // scales images used for buttons
    public ImageIcon createIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // creates the button panel
    private void createOptionPanel() {
        if (buttonPanel != null) {
            remove(buttonPanel); // removes existing button panel
        }

        playPauseButton = new JButton(createIcon("Image/Buttons/pause.png")); // creates pause button
        backButton = new JButton(createIcon("Image/Buttons/back.png")); // creates back button

        playPauseButton.setFocusable(false);
        backButton.setFocusable(false);

        playPauseButton.addActionListener(e -> togglePlayPause()); // event listener for pause button
        backButton.addActionListener(e -> backToMainMenu()); // event listener for back button

        // sets size and adds buttons
        buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setPreferredSize(new Dimension(getWidth(), 50));
        buttonPanel.add(backButton);
        buttonPanel.add(playPauseButton);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate(); // Ensure the new buttonPanel is reflected in the UI
        repaint();
    }

    // toggles between play and pause
    private void togglePlayPause() {
        if (board.isPaused()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    // display dialog if no server connection
    public void showNoConnectionDialogue() {
        Thread dialogThread = new Thread(() -> {
            if (noConnectionDialog.isVisible()) {
                return;
            }
            noConnectionDialog.setVisible(true);
        });
        dialogThread.start();
    }

    // returns to main menu
    private void backToMainMenu() {
        board.timer.stop(); // stop game player 1
        // if extend mode is active stops player 2
        if (extendMode && board2 != null) {
            board2.timer.stop();
        }

        // confirms decision to return to main menu
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return to the main menu?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            showMainMenu();
        } else {
            if (!board.isPaused()) {
                board.timer.start();
                if (extendMode && board2 != null) {
                    board2.timer.start();
                }
            }
        }
    }

    // updates the game settings
    public void updateSettings(int width, int height, int level, boolean musicOn, boolean soundEffectOn, boolean extendMode) {
        // set game settings
        this.fieldWidth = width;
        this.fieldHeight = height;
        this.gameLevel = level;
        this.soundEffectOn = soundEffectOn;
        this.extendMode = extendMode;

        if (musicOn) {
            music.playAudio();
        } else {
            music.endAudio();
        }
    }

    // updates player types
    public void updatePlayerTypes(String playerOneType, String playerTwoType) {
        this.playerOneType = playerOneType;
        this.playerTwoType = playerTwoType;
    }

    // returns player 1 type
    public String getPlayerOneType() {
        return playerOneType;
    }

    // returns player 2 type
    public String getPlayerTwoType() {
        return playerTwoType;
    }

    // displays config panel
    public void showConfigPanel() {
        getContentPane().removeAll(); // clears current content
        ConfigPanel configPanel = ConfigPanel.getInstance(); // creates config panel
        configPanel.SetupPanel(this);
        getContentPane().add(configPanel); // adds to content pane
        revalidate();
        repaint();
    }

    // displays main menu
    public void showMainMenu() {
        getContentPane().removeAll(); // clear content
        MainMenuPanel mainMenuPanel = new MainMenuPanel(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        getContentPane().add(mainMenuPanel); // add main menu
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT); // sets window size to defualts
        centerFrame();
        revalidate();
        repaint();
    }

    // restart game
    public void restartGame() {
        startGame();
    }

    // resume game
    public void resumeGame() {
        board.resumeGame();
        // if in extend mode also resets player 2
        if (extendMode && board2 != null) {
            board2.resumeGame();
        }
        playPauseButton.setIcon(createIcon("Image/Buttons/pause.png"));
    }

    // pause game
    public void pauseGame() {
        board.pause();
        if (extendMode && board2 != null) {
            board2.pause();
        }
        playPauseButton.setIcon(createIcon("Image/Buttons/play.png"));
    }

    // displays game over dialog
    public void showGameOverDialog(boolean extend) {
        board.timer.stop();// stops timer
        // checks if in extend mode and stops player 2 timer
        if (extendMode && board2 != null) {
            board2.timer.stop();
        }

        // determines winner and displays message
        String winnerMessage = extend ? "Player 1 won!" : "Player 2 won!";
        int option = JOptionPane.showOptionDialog(this, winnerMessage + " Would you like to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[] { "Restart", "Main Menu" }, "Restart");

        // actions for buttons created above
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            showMainMenu();
        }
    }

    // confirms then exits the game
    public void exitGame() {
        // pauses game for player 1 and 2
        if (this.board != null) {
            this.board.pauseGame();
            if (this.extendMode && this.board2 != null) {
                this.board2.pauseGame();
            }
        }

        // dialog to confirm if player wants to exit
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the Game?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // handles game over
    public void gameOver() {
        pauseGame(); // pauses game
        // show game over dialog
        int option = JOptionPane.showOptionDialog(this,
                "Game Over\nYour Score: " + board.getScore(),
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[] { "Restart", "Main Menu" },
                "Restart");

        // handles options in dialog
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            showMainMenu();
        }
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public boolean isMusicOn() {
        return music.isMusicOn();
    }

    public boolean isSoundEffectOn() {
        return soundEffectOn;
    }

    public boolean isExtendMode() {
        return extendMode;
    }

    // High score methods
    public void addHighScore(String name, int score) {
        if (score > 0) {
            highScores.add(new HighScore(name, score));
            // Sort highScores by score in descending order
            highScores.sort(Comparator.comparingInt(HighScore::getScore).reversed());
            saveHighScoresToJSON(highScores); // Save updated high scores to JSON
        }
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    public void showHighScores() {
        // Remove the existing content to display the high scores
        getContentPane().removeAll();

        // Create the panel to hold the high scores
        JPanel highScorePanel = new JPanel(new BorderLayout());

        // Create a JTextArea to display the high scores
        JTextArea highScoreArea = new JTextArea();
        highScoreArea.setEditable(false); // Ensure the text area is not editable

        // Display high scores in descending order (it is already sorted from the
        // addHighScore method)
        highScoreArea.append("High Scores:\n");
        for (HighScore highScore : highScores) {
            highScoreArea.append(highScore.getName() + ": " + highScore.getScore() + "\n");
        }

        // Add the high score display to a scroll pane
        highScorePanel.add(new JScrollPane(highScoreArea), BorderLayout.CENTER);

        // Create a button panel for the "Back" and "Clear Scores" buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        // "Back to Main Menu" button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> showMainMenu());

        // "Clear All Scores" button
        JButton clearScoresButton = new JButton("Clear All Scores");
        clearScoresButton.addActionListener(e -> clearAllScores());

        // Add buttons to the button panel
        buttonPanel.add(backButton);
        buttonPanel.add(clearScoresButton);

        // Add the button panel to the bottom of the high score panel
        highScorePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the high score panel to the content pane and update the UI
        getContentPane().add(highScorePanel);
        revalidate();
        repaint();
    }

    // Clear all scores
    public void clearAllScores() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all high scores?",
                "Clear High Scores", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            highScores.clear(); // clear highscore list
            saveHighScoresToJSON(highScores); // Save the cleared high score list to JSON
            showHighScores(); // Refresh the high score display
            System.out.println("All high scores have been cleared.");
        }
    }

    // Save high scores to JSON file
    public void saveHighScoresToJSON(List<HighScore> highScores) {
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter("high_scores.json")) {
            gson.toJson(highScores, writer); // writes high score to files
            System.out.println("High scores saved to JSON file.");
        } catch (IOException e) {
            e.printStackTrace(); // handles errors
        }
    }

    // Load high scores from JSON file
    public void loadHighScoresFromJSON() {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("high_scores.json")) {
            // Fully qualify the Type class to avoid conflicts
            java.lang.reflect.Type highScoreListType = new com.google.gson.reflect.TypeToken<List<HighScore>>() {
            }.getType();

            // Deserialize JSON to List<HighScore>
            highScores = gson.fromJson(reader, highScoreListType);

            if (highScores == null) {
                highScores = new ArrayList<>(); // Initialize if no high scores were found
            }
            System.out.println("High scores loaded from JSON file.");
        } catch (IOException e) {
            System.out.println("No high scores found, starting fresh.");
            highScores = new ArrayList<>(); // If no file found, start with an empty list
            saveHighScoresToJSON(highScores); // Create the file if it doesn't exist
        }
    }

    // handles single player controls
    private void handleSinglePlayerControls(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_COMMA || key == KeyEvent.VK_LEFT) {
            board.moveLeft();
        } else if (key == KeyEvent.VK_PERIOD || key == KeyEvent.VK_RIGHT) {
            board.moveRight();
        } else if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_DOWN) {
            board.moveDown();
        } else if (key == KeyEvent.VK_L || key == KeyEvent.VK_UP) {
            board.rotate();
        }
    }

    // handles controls for player 1 if in extend mode
    private void handlePlayerOneControls(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_COMMA) { // KeyEvent.VK_COMMA
            board.moveLeft();
        } else if (key == KeyEvent.VK_PERIOD) { // KeyEvent.VK_PERIOD
            board.moveRight();
        } else if (key == KeyEvent.VK_SPACE) { // KeyEvent.VK_SPACE
            board.moveDown();
        } else if (key == KeyEvent.VK_L) { // KeyEvent.VK_L
            board.rotate();
        }
    }

    // handles controls for player 2 if in extend mode
    private void handlePlayerTwoControls(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            board2.moveLeft();
        } else if (key == KeyEvent.VK_RIGHT) {
            board2.moveRight();
        } else if (key == KeyEvent.VK_DOWN) {
            board2.moveDown();
        } else if (key == KeyEvent.VK_UP) {
            board2.rotate();
        }
    }

    // handles controls in game such as pausing and toggling music
    private void handleGameStatusControls(KeyEvent e) {
        int key = e.getKeyCode();

        // toggles pause when p is pressed
        if (key == KeyEvent.VK_P) {
            if (board.isPaused()) {
                resumeGame();
            } else {
                pauseGame();
            }
            // toggles music when m is pressed
        } else if (key == KeyEvent.VK_M) {
            if (isMusicOn()) {
                music.endAudio();
            } else {
                music.playAudio();
            }
        }
    }

    // adaptor class to handle key events
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // if in extend mode handles multiplayer controls
            if (extendMode) {
                if (getPlayerOneType().equals("Human")) {
                    handlePlayerOneControls(e);
                }
                if (getPlayerTwoType().equals("Human")) {
                    handlePlayerTwoControls(e);
                }
            } else { // handles single player controls if not multiplayer
                handleSinglePlayerControls(e);
            }
            handleGameStatusControls(e);
        }
    }

    // represents high score in game
    public static class HighScore {
        private final String name;
        private final int score;

        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    // main method to run game
    public static void main(String[] args) {
        // shows splash screen for 5 seconds
        SplashScreen splash = new SplashScreen(5000, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        splash.showSplash();

        // initialises game main menu
        SwingUtilities.invokeLater(() -> {
            TetrisGame game = new TetrisGame();
            game.setResizable(false);
            game.setVisible(true);

            game.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            game.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    game.exitGame(); // exits game in event of window being closed
                }
            });
        });
    }
}