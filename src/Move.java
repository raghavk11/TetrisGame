public class Move {
    public int column;   // The column where the piece should be placed
    public int rotation; // The number of rotations to apply to the piece

    // Constructor to initialize a move with a specific column and rotation
    public Move(int column, int rotation) {
        this.column = column;
        this.rotation = rotation;
    }

    // Getter method for the column
    public int getColumn() {
        return column;
    }

    // Setter method for the column
    public void setColumn(int column) {
        this.column = column;
    }

    // Getter method for the rotation
    public int getRotation() {
        return rotation;
    }

    // Setter method for the rotation
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    //override the toString() method for debugging purposes
    @Override
    public String toString() {
        return "Move [column=" + column + ", rotation=" + rotation + "]";
    }
}
