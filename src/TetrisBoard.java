import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TetrisBoard extends JPanel implements ActionListener {
    // Game board dimensions and variables
    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;
    private int INITIAL_DELAY;
    public Timer timer;
    private boolean isFallingFinished = false;
    private int globalRotationValue = 0;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int score = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Shape.Tetrominoes[][] board;
    private TetrisGame parentFrame;
    private int gameLevel;
    private boolean musicOn;
    private boolean soundEffectOn;
    private boolean aiPlay;
    private boolean extendMode;
    private boolean externalMode;
    private List<TetrisGame.HighScore> highScores;
    private boolean isGameOver = false;

    private AudioPlr audioPlayer; // Reference to the audio player for sound effects
    private TetrisAI tetrisAI; // Reference to AI
    private TetrisClient externalClient; // reference to external

    // UI Elements for the game status
    private JLabel playerTypeLabel;
    private JLabel initialLevelLabel;
    private JLabel currentLevelLabel;
    private JLabel currentScoreLabel;
    private JLabel linesErasedLabel;

    private JPanel statusPanel;

    // Private constructor to restrict instantiation
    public TetrisBoard(TetrisGame parentFrame, int width, int height, int level, boolean music, boolean soundEffect,
            boolean ai, boolean extend, boolean externalMode) {
        this.parentFrame = parentFrame;
        this.BOARD_WIDTH = width;
        this.BOARD_HEIGHT = height - 2;
        this.gameLevel = level;
        this.musicOn = music;
        this.soundEffectOn = soundEffect;
        this.aiPlay = ai;
        this.extendMode = extend;
        this.externalMode = externalMode;
        this.INITIAL_DELAY = 400 - (level - 1) * 40;
        this.highScores = parentFrame.getHighScores();
        this.audioPlayer = parentFrame.getAudioPlayer(); // Reference to audio player
        this.tetrisAI = new TetrisAI(); // Initialize AI

        setFocusable(true);
        requestFocusInWindow();
        curPiece = new Shape();
        timer = new Timer(INITIAL_DELAY, e -> actionPerformed(e));
        board = new Shape.Tetrominoes[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();

        // Initialize game status panel
        initGameStatusPanel();

        setLayout(new BorderLayout());

        // Add the game status panel at the top
        add(statusPanel, BorderLayout.NORTH);
    }

    // Method to initialize the game status panel with improved UI and dynamic
    // resizing
    private void initGameStatusPanel() {
        statusPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Dynamically adjust font size based on window width
        int windowWidth = parentFrame.getWidth();
        int fontSize = Math.max(12, windowWidth / 100); // Adjusted the factor for dynamic font sizing

        Font statusFont = new Font("Arial", Font.BOLD, fontSize);

        // Set background to dark and text to white
        statusPanel.setBackground(Color.DARK_GRAY);

        // Create labels with dynamically adjusted font size
        String playerType = parentFrame.getPlayerOneType(); // Fetch the player type from TetrisGame
        playerTypeLabel = createStatusLabel("Player Type: " + playerType, statusFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        statusPanel.add(playerTypeLabel, gbc);

        initialLevelLabel = createStatusLabel("Initial Level: " + gameLevel, statusFont);
        gbc.gridx = 1;
        statusPanel.add(initialLevelLabel, gbc);

        currentLevelLabel = createStatusLabel("Current Level: " + gameLevel, statusFont);
        gbc.gridx = 2;
        statusPanel.add(currentLevelLabel, gbc);

        currentScoreLabel = createStatusLabel("Score: " + score, statusFont);
        gbc.gridx = 3;
        statusPanel.add(currentScoreLabel, gbc);

        linesErasedLabel = createStatusLabel("Lines Erased: " + numLinesRemoved, statusFont);
        gbc.gridx = 4;
        statusPanel.add(linesErasedLabel, gbc);

        // Add padding to the panel itself for better spacing
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Helper method to create a JLabel with the desired font and background
    private JLabel createStatusLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.WHITE); // White text
        label.setBackground(Color.DARK_GRAY); // Dark background
        label.setOpaque(true); // Ensure background color is visible
        return label;
    }

    // pause game
    public void pauseGame() {
        if (!isStarted)
            return;
        isPaused = true;
        timer.stop();
    }

    // resume game
    public void resumeGame() {
        if (!isStarted || !isPaused)
            return;
        isPaused = false;
        timer.start();
    }

    // get score
    public int getScore() {
        return score;
    }

    // method to update the game settings
    public void updateSettings(int width, int height, int level, boolean music, boolean soundEffect, boolean ai,
            boolean extend) {
        this.BOARD_WIDTH = width;
        this.BOARD_HEIGHT = height - 2;
        this.gameLevel = level;
        this.musicOn = music;
        this.soundEffectOn = soundEffect;
        this.aiPlay = ai;
        this.extendMode = extend;
        this.INITIAL_DELAY = 400 - (level - 1) * 40;
        clearBoard();
    }

    // check if game is paused
    public boolean isPaused() {
        return this.isPaused;
    }

    // check if game is started
    public boolean isStarted() {
        return this.isStarted;
    }

    // check if game is over
    public boolean isGameOver() {
        return isGameOver;
    }

    // toggle pause and resume state
    public void pause() {
        if (!isStarted)
            return;
        isPaused = !isPaused;
        if (isPaused) {
            pauseGame();
        } else {
            resumeGame();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // return if ai or external mode active
        if (aiPlay || externalMode) {
            return;
        }
        if (isPaused)
            return; // Ignore actions if paused
        // generates new piece when one has fallen
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    // method to calculate square size
    private int squareSize() {
        return Math.min((int) getSize().getWidth() / BOARD_WIDTH, (int) getSize().getHeight() / BOARD_HEIGHT);
    }

    // returns shape at x and y position on board
    private Shape.Tetrominoes shapeAt(int x, int y) {
        return board[y][x];
    }

    // Private helper method to clear the game board
    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = Shape.Tetrominoes.NoShape;
            }
        }
    }

    // drops current piece to bottom of board
    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped(); // handles the peice when reaches bottom
    }

    // moves the current piece down one line
    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    // initialise new game
    public void startGame() {
        isStarted = true;
        isPaused = false;
        isGameOver = false;
        numLinesRemoved = 0;
        score = 0;
        clearBoard();
        newPiece();
        timer.start();

        setFocusable(true);
        requestFocusInWindow();
        updateScore();

        // if AI play enabled, make the AI move
        if (aiPlay) {
            aiMakeMove();
        }
        // if external mode enabled sets up external client
        if (parentFrame.getPlayerOneType().equals("External") || parentFrame.getPlayerTwoType().equals("External")) {
            externalClient = new TetrisClient();
            aiMakeMove();
        }
    }

    // handles logic when piece is dropped
    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[y][x] = curPiece.getShape();
        }

        removeFullLines();

        // if piece is finished falling creates new piece
        if (!isFallingFinished) {
            newPiece();
            if (aiPlay || parentFrame.getPlayerOneType().equals("External")
                    || parentFrame.getPlayerTwoType().equals("External")) {
                aiMakeMove();
            }
        }
    }

    // generates random piece to top of board
    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        // checks if the new piece can be placed if not game ends
        if (!tryMove(curPiece, curX, curY) && !isGameOver) {
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            isGameOver = true;
            audioPlayer.playSoundEffect("game-finish"); // Play game over sound effect
            // show game over dialog depending on mode
            if (!parentFrame.isExtendMode()) {
                showGameOverDialog();
            } else {
                parentFrame.showGameOverDialog(extendMode);
            }
        }
    }

    // tries to move piece to new position
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            // if new position is out of bounds return false
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Shape.Tetrominoes.NoShape) {
                return false;
            }
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    // Play sound when a line is erased
    private void removeFullLines() {
        int numFullLines = 0; // initialise counter for number of full lines

        // iterate from the bottom of board to top
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            // check col in row
            for (int j = 0; j < BOARD_WIDTH; j++) {
                // if cell in line is empty, line is not full
                if (shapeAt(j, i) == Shape.Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            // removes full line and shift down
            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[k][j] = board[k + 1][j];
                    }
                }
                audioPlayer.playSoundEffect("erase-line"); // Play line erase sound effect
            }
        }

        // if any lines removed update score and game
        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            score += calculateScore(numFullLines); // Calculate score based on number of rows removed
            updateScore();
            updateLevel(); // Check for level progression
            isFallingFinished = true;
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            repaint();

            // Update the game status display
            updateStatusDisplay();
        }
    }

    // Scoring logic based on the number of rows removed at once
    private int calculateScore(int linesRemoved) {
        switch (linesRemoved) {
            case 1:
                return 100;
            case 2:
                return 300;
            case 3:
                return 600;
            case 4:
                return 1000;
            default:
                return 0;
        }
    }

    // Method to update the player's level based on the number of lines erased
    private void updateLevel() {
        int newLevel = numLinesRemoved / 10 + 1; // Every 10 rows, increase the level
        if (newLevel > gameLevel) {
            gameLevel = newLevel;
            levelUp(); // Trigger level-up sound or effect
        }
    }

    // method to update game with current level, score and lines erased
    private void updateStatusDisplay() {
        currentLevelLabel.setText("Current Level: " + gameLevel);
        currentScoreLabel.setText("Score: " + score);
        linesErasedLabel.setText("Lines Erased: " + numLinesRemoved);
    }

    // refresh score display
    private void updateScore() {
        currentScoreLabel.setText("Score: " + score);
    }

    // show game over dialog and handle input for restarting, viewing score
    private void showGameOverDialog() {
        audioPlayer.playSoundEffect("game-finish"); // Play game finish sound effect
        String playerName = JOptionPane.showInputDialog(this, "Game Over\nYour Score: " + score + "\nEnter your name:",
                "Enter Name", JOptionPane.PLAIN_MESSAGE);

        // add high score
        if (playerName != null && !playerName.trim().isEmpty()) {
            parentFrame.addHighScore(playerName, score);
        }

        // display options for restarting, view high score or menu
        int option = JOptionPane.showOptionDialog(this, "Would you like to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[] { "Restart", "View High Scores", "Main Menu" }, "Restart");

        // handle player choice
        if (option == JOptionPane.YES_OPTION) {
            parentFrame.restartGame();
        } else if (option == 1) {
            parentFrame.showHighScores();
        } else {
            parentFrame.showMainMenu();
        }
    }

    // paint component to draw the board and pieces
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareSize();

        // Draw the entire board content
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Shape.Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Shape.Tetrominoes.NoShape) {
                    drawSquare(g, j * squareSize(), boardTop + i * squareSize(), shape);
                }
            }
        }

        // Draw the falling piece
        if (curPiece.getShape() != Shape.Tetrominoes.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareSize(), boardTop + (BOARD_HEIGHT - y - 1) * squareSize(),
                        curPiece.getShape());
            }
        }

        // Draw the board outline for single-player or each board in extended mode
        g.setColor(Color.BLACK);

        if (parentFrame.isExtendMode()) {
            // For extended mode, draw two separate outlines for both boards
            g.drawRect(0, boardTop, BOARD_WIDTH * squareSize(), BOARD_HEIGHT * squareSize()); // Left board
            g.drawRect(BOARD_WIDTH * squareSize(), boardTop, BOARD_WIDTH * squareSize(), BOARD_HEIGHT * squareSize()); // Right
                                                                                                                       // board

            // Draw a divider between the two boards
            g.fillRect(BOARD_WIDTH * squareSize() - 2, 0, 4, getHeight()); // Divider line, 4px thick for better
                                                                           // visibility
        } else {
            // For single-player mode, draw a single outline around the whole board
            g.drawRect(0, boardTop, BOARD_WIDTH * squareSize(), BOARD_HEIGHT * squareSize());
        }

        // Draw pause overlay
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String pauseMessage = "Game Paused";
            String resumeMessage = "Press 'P' to resume";
            int pauseMessageWidth = g.getFontMetrics().stringWidth(pauseMessage);
            int resumeMessageWidth = g.getFontMetrics().stringWidth(resumeMessage);
            g.drawString(pauseMessage, (getWidth() - pauseMessageWidth) / 2, getHeight() / 2 - 20);
            g.drawString(resumeMessage, (getWidth() - resumeMessageWidth) / 2, getHeight() / 2 + 10);
        }
    }

    // method to draw square for pieces
    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape) {
        // colours for tetrimino pieces
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0) };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareSize() - 2, squareSize() - 2);

        // Add a black border for better visual distinction
        g.setColor(Color.BLACK);
        g.drawRect(x, y, squareSize(), squareSize());

        g.setColor(color.brighter());
        g.drawLine(x, y + squareSize() - 1, x, y);
        g.drawLine(x, y, x + squareSize() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareSize() - 1, x + squareSize() - 1, y + squareSize() - 1);
        g.drawLine(x + squareSize() - 1, y + squareSize() - 1, x + squareSize() - 1, y + 1);
    }

    // move current piece down by one line if not paused
    public void moveDown() {
        if (!isPaused) {
            oneLineDown();
        }
    }

    // drop piece to bottom if not paused
    public void drop() {
        if (!isPaused) {
            dropDown();
        }
    }

    // Play sound when the piece is moved
    public void moveLeft() {
        if (!isPaused && tryMove(curPiece, curX - 1, curY)) {
            audioPlayer.playSoundEffect("move-turn"); // Play move sound effect
        }
    }

    // move piece right if not paused and valid move
    public void moveRight() {
        if (!isPaused && tryMove(curPiece, curX + 1, curY)) {
            audioPlayer.playSoundEffect("move-turn"); // Play move sound effect
        }
    }

    // rotate piece if not paused
    public void rotate() {
        if (!isPaused && tryMove(curPiece.rotateRight(), curX, curY)) {
            audioPlayer.playSoundEffect("move-turn"); // Play rotate sound effect
        }
    }

    // Play sound when leveling up
    private void levelUp() {
        gameLevel++;
        audioPlayer.playSoundEffect("level-up"); // Play level up sound effect
    }

    // toggle music
    public void toggleMusic() {
        musicOn = !musicOn;
    }

    // return width of board
    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    // return the board and its current values
    public Shape.Tetrominoes[][] getBoard() {
        return board;
    }

    // AI move integration
    public void aiMakeMove() {
        Move bestMove = null;
        if (isGameOver) {
            return;
        }
        if (parentFrame.getPlayerOneType().equals("External") || parentFrame.getPlayerTwoType().equals("External")) {
            bestMove = externalFunction();
        } else {
            bestMove = tetrisAI.findBestMove(this, curPiece);
        }

        if (bestMove != null) {
            // Rotate the piece to the best rotation
            if (globalRotationValue < bestMove.rotation) {
                rotate();
                globalRotationValue++;
            }
            // Move the piece to the best column
            if (curX < bestMove.column) {
                moveRight();
            }
            if (curX > bestMove.column) {
                moveLeft();
            }
            // Drop the piece
            // dropDown();
        }
    }

    // AI game loop
    public void runAIControlledGame() {
        timer = new Timer(INITIAL_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameOver) {
                    return;
                }
                if (!isFallingFinished) {
                    aiMakeMove(); // Let AI make its move
                    oneLineDown();
                } else {
                    isFallingFinished = false;
                    globalRotationValue = 0;
                    newPiece();
                }
                repaint(); // Ensure the board is refreshed
            }
        });
        timer.start(); // Start the game timer for AI moves
    }

    // get move from external source
    public Move externalFunction() {
        PureGame gp = new PureGame();
        gp.setHeight(BOARD_HEIGHT);
        gp.setWidth(BOARD_WIDTH);
        gp.setCells(getBoard());
        gp.setCurrentShape(curPiece);
        OpMove move = externalClient.getExternalMove(gp);
        if (move == null) {
            parentFrame.showNoConnectionDialogue();
            return null;
        }
        Move bestMove = new Move(move.getOpX(), move.getOpRotate());
        return bestMove;
    }

}