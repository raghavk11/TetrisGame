import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TetrisServer {
    // constants for the servers host and port
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;
    // instance of tetrisAI
    private static final TetrisAI tetrisAI = new TetrisAI();;

    public static void main(String[] args) {
        // try and open server socket on specified port
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Tetris Server is listening on port " + SERVER_PORT);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    // Read the message
                    String Message = in.readLine();
                    System.out.println("Received response from client!");

                    JsonObject jsonObject = JsonParser.parseString(Message).getAsJsonObject();
                    // Get Width & Height
                    int boardWidth = jsonObject.get("width").getAsInt();
                    int boardHeight = jsonObject.get("height").getAsInt();
                    // Get current Piece
                    JsonObject currentPiece = jsonObject.getAsJsonObject("currentShape");
                    String pieceShape = currentPiece.get("pieceShape").getAsString();
                    Shape curPiece = new Shape();
                    curPiece.setShape(Shape.Tetrominoes.valueOf(pieceShape));
                    // Get Board
                    JsonArray cellsArray = jsonObject.getAsJsonArray("cells");
                    String[][] cells = new String[cellsArray.size()][];
                    for (int i = 0; i < cellsArray.size(); i++) {
                        JsonArray row = cellsArray.get(i).getAsJsonArray();
                        cells[i] = new String[row.size()];

                        for (int j = 0; j < row.size(); j++) {
                            cells[i][j] = row.get(j).getAsString();
                        }
                    }

                    // Convert Array of Strings to Shape.Tetrominoes
                    Shape.Tetrominoes[][] cellsBoard = new Shape.Tetrominoes[boardHeight][boardWidth];
                    // Print the 2D array
                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            cellsBoard[i][j] = Shape.Tetrominoes.valueOf(cells[i][j]);
                        }
                    }

                    // Get BestMove
                    Move bestMove = tetrisAI.findBestMove(cellsBoard, curPiece);
                    // Pass into opmove
                    OpMove opmove = new OpMove(bestMove.column, bestMove.rotation);

                    // Send it back to client
                    Gson gson = new Gson();
                    String jsonGameState = gson.toJson(opmove);
                    System.out.println("Sending " + jsonGameState);
                    out.println(jsonGameState);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in server. " + e.getMessage());
        }
    }
}