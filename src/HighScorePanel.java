import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HighScorePanel extends JPanel {

    // initialise high score panel
    public HighScorePanel(TetrisGame game, List<TetrisGame.HighScore> highScores) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("High Score", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(Color.DARK_GRAY);
        add(heading, BorderLayout.NORTH);

        String[] columnNames = { "Name", "Score" };
        Object[][] data = new Object[highScores.size()][2];

        // populate with high score entries
        for (int i = 0; i < highScores.size(); i++) {
            data[i][0] = highScores.get(i).getName();
            data[i][1] = highScores.get(i).getScore();
        }

        // create table model
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create j table
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // create scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // create author label
        JLabel authorLabel = new JLabel("Author: Group 19", JLabel.CENTER);
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        authorLabel.setForeground(Color.GRAY);

        // create back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> game.showMainMenu());

        // create bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(backButton, BorderLayout.CENTER);
        bottomPanel.add(authorLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
