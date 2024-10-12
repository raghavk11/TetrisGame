import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class TetrisClient {
    // constents for server host and port
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;

    private int playerNumber; // player number

    // setter for player num
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    // getter for player num
    public int getPlayerNumber() {
        return playerNumber;
    }

    public OpMove getExternalMove(PureGame game) {
        OpMove move = null;

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Convert PureGame object to JSON
            Gson gson = new Gson();
            String jsonGameState = gson.toJson(game);

            // Send the game state to the server
            out.println(jsonGameState);
            System.out.println("Sent game state to server!");

            // Wait for the server's response (OpMove)
            String response = in.readLine();
            if (response == null || response.isEmpty()) {
                System.err.println("Received empty response from server.");
                return null;
            }
            System.out.println("Received response from server: " + response);

            // Convert the JSON response to an OpMove object
            move = gson.fromJson(response, OpMove.class);
            if (move == null) {
                System.err.println("Failed to parse OpMove from server response.");
            } else {
                // System.out.println("Optimal Move: X=" + move.getOpX() + ", Rotations=" +
                // move.getOpRotate());
            }

        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
            return null;
        }

        return move;
    }
}
