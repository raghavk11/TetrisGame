public class TetrisAI {
    // instance of board evaluator
    private BoardEvaluator evaluator = new BoardEvaluator();

    // find best move for current piece on board
    public Move findBestMove(TetrisBoard board, Shape piece) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // Simulate all rotations and column positions
        for (int rotation = 0; rotation < 4; rotation++) {
            Shape rotatedPiece = piece;
            for (int i = 0; i < rotation; i++) {
                rotatedPiece = rotatedPiece.rotateRight();
            }

            // Try placing the piece in each column
            for (int col = 0; col < board.getBoardWidth(); col++) {
                // Simulate the drop of the piece at different column positions
                Shape.Tetrominoes[][] simulatedBoard = simulateDrop(board.getBoard(), rotatedPiece, col);
                // System.out.println(simulatedBoard);
                if (simulatedBoard != null) { // Add null check for out-of-bounds
                    int score = evaluator.evaluateBoard(simulatedBoard);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Move(col, rotation);
                    }
                }
            }
        }

        // if no valid move found randomise
        if (bestMove == null) {
            int randomCol = (int) (Math.random() * board.getBoardWidth());
            int randomRot = (int) (Math.random() * 4);
            bestMove = new Move(randomCol, randomRot);
            System.out.println("Best Move = NULL! Randomising Instead!");
        }

        return bestMove;
    }

    // Function Overload for Server Call
    public Move findBestMove(Shape.Tetrominoes[][] board, Shape piece) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // Simulate all rotations and column positions
        for (int rotation = 0; rotation < 4; rotation++) {
            Shape rotatedPiece = piece;
            for (int i = 0; i < rotation; i++) {
                rotatedPiece = rotatedPiece.rotateRight();
            }

            // Try placing the piece in each column
            for (int col = 0; col < board[0].length; col++) {
                // Simulate the drop of the piece at different column positions
                Shape.Tetrominoes[][] simulatedBoard = simulateDrop(board, rotatedPiece, col);
                // System.out.println(simulatedBoard);
                if (simulatedBoard != null) { // Add null check for out-of-bounds
                    int score = evaluator.evaluateBoard(simulatedBoard);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Move(col, rotation);
                    }
                }
            }
        }
        // if no best move found randomise
        if (bestMove == null) {
            int randomCol = (int) (Math.random() * board[0].length);
            int randomRot = (int) (Math.random() * 4);
            bestMove = new Move(randomCol, randomRot);
            //System.out.println("Best Move = NULL! Randomising Instead!");
        }

        return bestMove;
    }

    // simulate dropping piece at col position
    private Shape.Tetrominoes[][] simulateDrop(Shape.Tetrominoes[][] board, Shape piece, int col) {
        Shape.Tetrominoes[][] simulatedBoard = copyBoard(board);
        // printBoard(simulatedBoard);
        int dropRow = getDropRow(simulatedBoard, piece, col);

        // Check if the piece can be placed within valid bounds
        if (dropRow >= 0) {
            placePiece(simulatedBoard, piece, col, dropRow);
            return simulatedBoard;
        }
        return null; // Return null if placement is invalid
    }

    // determine row where piece can be placed
    private int getDropRow(Shape.Tetrominoes[][] board, Shape piece, int col) {
        int row = 0;
        while (canPlacePiece(board, piece, col, row)) {
            row++;
        }
        // System.out.println(row-1);
        return row - 1; // Return the last valid row
    }

    // check if piece can be placed at the position
    private boolean canPlacePiece(Shape.Tetrominoes[][] board, Shape piece, int col, int row) {
        for (int i = 0; i < 4; i++) {
            int x = col + piece.x(i);
            int y = row + piece.y(i);

            // System.out.println(y+" " +x);
            // Check if x and y are within valid bounds
            if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
                return false;
            }
            if (board[y][x] != Shape.Tetrominoes.NoShape) {
                return false;
            }
        }
        // System.out.println("Can True");
        return true;
    }

    // place piece on board at specified location
    private void placePiece(Shape.Tetrominoes[][] board, Shape piece, int col, int row) {
        for (int i = 0; i < 4; i++) {
            int x = col + piece.x(i);
            int y = row + piece.y(i);

            // Check again to ensure that x and y are valid before placing
            if (x >= 0 && x < board[0].length && y >= 0 && y < board.length) {
                board[y][x] = piece.getShape();
            }
        }
    }

    // copy board
    private Shape.Tetrominoes[][] copyBoard(Shape.Tetrominoes[][] board) {
        Shape.Tetrominoes[][] newBoard = new Shape.Tetrominoes[board.length][board[0].length];
        for (int y = 0; y < board.length; y++) {
            System.arraycopy(board[y], 0, newBoard[y], 0, board[0].length);
        }
        return newBoard;
    }

    //print board for debugging
    private void printBoard(Shape.Tetrominoes[][] board) {
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                System.out.print(board[y][x] == Shape.Tetrominoes.NoShape ? "." : "#");
            }
            System.out.println();
        }
    }
}
