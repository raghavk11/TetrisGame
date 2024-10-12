public class BoardEvaluator {

    // evaluates board based on various factors
    public int evaluateBoard(Shape.Tetrominoes[][] board) {
        int heightScore = getHeight(board);
        int holesScore = getHoles(board);
        int linesCleared = getClearedLines(board);
        int bumpinessScore = getBumpiness(board);
        return (-4 * heightScore) + (3 * linesCleared) - (5 * holesScore) - (2 * bumpinessScore);
    }

    // calc max height of blocks
    private int getHeight(Shape.Tetrominoes[][] board) {
        int height = 0;
        for (int x = 0; x < board[0].length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (board[y][x] != Shape.Tetrominoes.NoShape) {
                    height = Math.max(height, board.length - y);
                    break;
                }
            }
        }
        return height;
    }

    // calculate num of holes in board
    private int getHoles(Shape.Tetrominoes[][] board) {
        int holes = 0;
        for (int x = 0; x < board[0].length; x++) {
            boolean foundBlock = false;
            for (int y = 0; y < board.length; y++) {
                if (board[y][x] != Shape.Tetrominoes.NoShape) {
                    foundBlock = true;
                } else if (foundBlock && board[y][x] == Shape.Tetrominoes.NoShape) {
                    holes++;
                }
            }
        }
        return holes;
    }

    // counts num of cleared lines on board
    private int getClearedLines(Shape.Tetrominoes[][] board) {
        int clearedLines = 0;
        for (int y = 0; y < board.length; y++) {
            boolean isLineFull = true;
            for (int x = 0; x < board[0].length; x++) {
                if (board[y][x] == Shape.Tetrominoes.NoShape) {
                    isLineFull = false;
                    break;
                }
            }
            if (isLineFull) {
                clearedLines++;
            }
        }
        return clearedLines;
    }

    // calc bumpiness
    private int getBumpiness(Shape.Tetrominoes[][] board) {
        int bumpiness = 0;
        for (int x = 0; x < board[0].length - 1; x++) {
            int colHeight1 = getColumnHeight(board, x);
            int colHeight2 = getColumnHeight(board, x + 1);
            bumpiness += Math.abs(colHeight1 - colHeight2);
        }
        return bumpiness;
    }

    // method to get height of specific col
    private int getColumnHeight(Shape.Tetrominoes[][] board, int col) {
        for (int y = 0; y < board.length; y++) {
            if (board[y][col] != Shape.Tetrominoes.NoShape) {
                return board.length - y;
            }
        }
        return 0;
    }
}
