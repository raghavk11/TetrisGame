// File: src/OpMove.java

import java.util.Arrays;

public class OpMove {
    private int opX;
    private int opRotate;

    // Constructor
    public OpMove(int opX, int opRotate) {
        this.opX = opX;
        this.opRotate = opRotate;
    }

    @Override
    public String toString() {
        return "OpMove{" +
        "opX=" + this.opX +
        ", opRotate=" + this.opRotate +
        '}';
    }

    // Getters and setters
    public int getOpX() { return opX; }
    public void setOpX(int opX) { this.opX = opX; }
    public int getOpRotate() { return opRotate; }
    public void setOpRotate(int opRotate) { this.opRotate = opRotate; }
}
