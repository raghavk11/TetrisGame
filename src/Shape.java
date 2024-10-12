import java.util.Random;

public class Shape {
    // Enum representing the different tetromino shapes
    enum Tetrominoes {
        NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
    }

    // the current shape
    private Tetrominoes pieceShape;

    // array to store the co-ordinated of the current tetromino piece
    private int[][] coords;

    // lookup table to store the co-ordinates for all shapes
    private int[][][] coordsTable;

    // constructor to initialize the shape with a default noshape
    public Shape() {
        coords = new int[4][2]; // each shape has 4 blocks, each with x and y coordinates
        setShape(Tetrominoes.NoShape); // initialize with noshape
    }

    // method to set the shape of the tetromino
    public void setShape(Tetrominoes shape) {
        // define the coordinates for each shape
        coordsTable = new int[][][] {
                { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, // no shape
                { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, // zshape
                { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, // sshape
                { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, // lineshape
                { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, // tshape
                { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // squareshape
                { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, // lshape
                { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } // mirroredlshape
        };

        // assign the coordinates of the selected shape to current shape
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape; // update current shape
    }

    // method to set x coordinate
    private void setX(int index, int x) {
        coords[index][0] = x;
    }

    // method to set y coordinate
    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    // method to get x coordinate
    public int x(int index) {
        return coords[index][0];
    }

    // method to get y coordinate
    public int y(int index) {
        return coords[index][1];
    }

    // method to get the current shape
    public Tetrominoes getShape() {
        return pieceShape;
    }

    // method to randomly select and set a new shape
    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1; // select a random shape
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    // method to find the minimum x coordinates in the current shape
    public int minX() {
        int m = coords[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }

    // method to find the minimum y coordinate in the current shape
    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    // method to rotate the shape 90 degrees to the left
    public Shape rotateLeft() {
        // square shape doesnt change when rotated
        if (pieceShape == Tetrominoes.SquareShape) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        // rotate shape
        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    // method to rotate the shape 90 degrees to the right
    public Shape rotateRight() {
        if (pieceShape == Tetrominoes.SquareShape) {
            return this;
        }

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        // rotates the shape
        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}