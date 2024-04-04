import java.io.*;
import java.net.*;

public class ClientServerConnection {
    private static final int PORT = 1234;
    private static ServerSocket servSock;
    private static boolean connected;
    private static ApplicationController controller;

    public static void main(String[] args) {
        try {
            System.out.println("Opening port...\n");
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
                System.out.println("Response sent: " + response);
            }
        } while (connected);
    }

    private static String processRequest(String request) throws IncorrectActionException {
        RequestParser parser = new RequestParser();
        String requestType = parser.getRequestType(request);
        ClassSchedule classSchedule;
        boolean requestOutcome;
        connected = true;
        String errorMessage = "";

        switch (requestType) {
            case "add":
                requestOutcome = controller.addClass(request);
                return requestOutcome ? "true" : "false";

            case "remove":
                requestOutcome = controller.removeClass(request);
                return requestOutcome ? "true" : errorMessage;

            case "display":
                String[] parts = request.split(",");
                String className = parts[1].trim();
                String timetable = controller.displayTimetable(className);
                System.out.println(timetable);

                return timetable;

            case "close":
                connected = false;
                return closeConnection() ? "true" : "Failed to close connection";

            default:
                return "Invalid request type";
        }
    }

    private static boolean closeConnection() {
        try {
            servSock.close();
            return true;
        } catch (IOException e) {
            System.out.println("Unable to close connection!");
            return false;
        }
    }
}