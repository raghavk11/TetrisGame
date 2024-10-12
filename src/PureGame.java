// PureGame.java
import java.util.Arrays;

public class PureGame {
    private int width;
    private int height;
    private Shape.Tetrominoes[][] cells;
    private Shape currentShape;
    private Shape nextShape;

    @Override
    public String toString() {
        return "PureGame{" +
        "width=" + width +
        ", height=" + height +
        ", cells=" + Arrays.deepToString(cells) +
        ", currentShape=" + (currentShape) +
        ", nextShape=" + (nextShape) +
        '}';
    }

    // Getters and setters
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public Shape.Tetrominoes[][] getCells() { return cells; }
    public void setCells(Shape.Tetrominoes[][] cells) { this.cells = cells; }
    public Shape getCurrentShape() { return currentShape; }
    public void setCurrentShape(Shape currentShape) { this.currentShape = currentShape; }
    public Shape getNextShape() { return nextShape; }
    public void setNextShape(Shape nextShape) { this.nextShape = nextShape; }
}
