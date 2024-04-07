import java.io.*;
import java.net.*;

public class ClientServerConnection {
    private static final int PORT = 1234;
    private static ServerSocket servSock;
    private static boolean connected;
    private static ApplicationController controller;

    public static void main(String[] args) {
        try {
            System.out.println("\nOpening port...\n");
            servSock = new ServerSocket(PORT);
            controller = new ApplicationController();
            run();
        } catch (IOException | IncorrectActionException e) {
            System.out.println("Unable to attach to port!");
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                servSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void run() throws IOException, IncorrectActionException {
        do {
            Socket socket = servSock.accept();
            try (socket;
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                String request = dataInputStream.readUTF();
                String response = processRequest(request);

                dataOutputStream.writeUTF(response);
                System.out.println("Response sent: " + response + "\n");
            }
        } while (connected);
    }

    private static String processRequest(String request){
        RequestParser parser = new RequestParser();
        String requestType = parser.getRequestType(request);
        ClassSchedule classSchedule;
        boolean requestOutcome;
        connected = true;
        String responseMessage = "";

        switch (requestType) {
            case "add":
                responseMessage = controller.addClass(request);
                return responseMessage;

            case "remove":
                responseMessage = controller.removeClass(request);
                return responseMessage;

            case "display":
                String[] parts = request.split(",");
                String className = parts[1].trim();
                return controller.displayTimetable(className);

            case "close":
                connected = false;
                return closeConnection();

            default:
                return "Invalid request type";
        }
    }

    private static String closeConnection() {
        try {
            servSock.close();
            return "Connection closed successfully!";
        } catch (IOException e) {
            System.out.println("Unable to close connection!");
            return "Unable to close connection!";
        }
    }
}