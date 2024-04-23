import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimetableManagerView {
    private static final int PORT = 1234;
    private static ServerSocket servSock;
    private static boolean connected;
    private static TimetableManagerController controller;

    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        try {
            System.out.println("\nOpening port...\n");
            servSock = new ServerSocket(PORT);
            controller = new TimetableManagerController();
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
            connected = true; // Set connected to true initially

            // Added a shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                connected = false; // Signal to stop accepting new connections
                executor.shutdown(); // Initiate shutdown of the ExecutorService
                try {
                    servSock.close(); // Close the server socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (connected) { // Continuously accept new client connections
                Socket socket = servSock.accept(); // Accept incoming client connection
                executor.submit(new ClientHandler(socket)); // Start a new thread to handle client
            }
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
            e.printStackTrace();
            System.exit(1); // Exit if unable to start server
        }
    }

    // Runnable class to handle individual client connections
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    try {
                        String request = dataInputStream.readUTF(); // Read client request
                        String response = processRequest(request); // Process request
                        dataOutputStream.writeUTF(response); // Send response to client
                        System.out.println("Response sent:\n" + response + "\n"); // Log response
                    } catch (EOFException e) {
                        // Client closed connection
                        System.out.println("Client disconnected.");
                        break; // Exit the loop
                    } catch (SocketException e) {
                        break; // Exit the loop
                    } catch (IOException e) {
                        // Handle other I/O exceptions
                        e.printStackTrace();
                        break; // Exit the loop
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dataInputStream != null) dataInputStream.close();
                    if (dataOutputStream != null) dataOutputStream.close();
                    socket.close(); // Close the socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Process client request and generate response
        private static String processRequest(String request) {
            RequestParser parser = new RequestParser();
            String requestType = parser.getRequestType(request);
            String responseMessage;
            String[] parts;
            String courseID;

            switch (requestType) {
                case "add":
                    responseMessage = controller.addSchedule(request);
                    break;

                case "remove":
                    responseMessage = controller.removeClass(request);
                    break;

                case "display":
                    parts = request.split(",");
                    courseID = parts[1].trim();
                    responseMessage = controller.displayTimetable(courseID);
                    break;

                case "early":
                    parts = request.split(",");
                    courseID = parts[1].trim();
                    responseMessage = controller.requestEarlyLectures(courseID);
                    break;

                case "close":
                    connected = false; // Stop accepting new connections
                    responseMessage = closeConnection(); // Close server
                    break;

                default:
                    responseMessage = "Invalid request type";
                    break;
            }

            return responseMessage; // Return response to client
        }

        // Close server connection
        private static String closeConnection() {
            try {
                servSock.close(); // Close server socket
                return "Connection closed successfully!";
            } catch (IOException e) {
                System.out.println("Unable to close connection!");
                return "Unable to close connection!";
            }
        }
    }
}