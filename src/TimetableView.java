import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimetableView {
    // Define constants for the port number and maximum number of threads
    private static final int PORT = 1234;
    private static final int MAX_THREADS = 100;

    // Declare variables for the server socket, connection status, and controller
    private static ServerSocket servSock;
    private static boolean connected;
    private static TimetableController controller;

    public static void main(String[] args) {
        try {
            // Initialize the server socket and controller
            servSock = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            controller = new TimetableController();

            // Create a thread pool with a fixed number of threads
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
            connected = true;

            // Add a shutdown hook to close the server and executor when the program exits
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
                System.out.println("Accepted new client connection from " + socket.getInetAddress() + "\n");
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
                        System.out.println("Received request:\n     " + request + "\n");
                        String response = processRequest(request); // Process request
                        dataOutputStream.writeUTF(response); // Send response to client
                        System.out.println("Response sent:\n     " + response + "\n"); // Log response
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
        private static String processRequest(String request){
            RequestParser parser = new RequestParser();
            String requestType = parser.getRequestType(request);
            String responseMessage;
            String[] parts;
            String courseID;

            switch (requestType) {
                case "add":
                    responseMessage = controller.addSchedule(request);
                    System.out.println("Added schedule:\n    " + request + "\n");
                    break;

                case "remove":
                    responseMessage = controller.removeClass(request);
                    System.out.println("Removed schedule:\n     " + request + "\n");
                    break;

                case "display":
                    parts = request.split(",");
                    courseID = parts[1].trim();
                    responseMessage = controller.displayTimetable(courseID);
                    System.out.println("Displayed timetable for course: " + courseID + "\n");
                    break;

                case "early":
                    parts = request.split(",");
                    courseID = parts[1].trim();
                    responseMessage = controller.requestEarlyLectures(courseID);
                    System.out.println("Requested early lectures for course: " + courseID + "\n");
                    break;

                case "close":
                    connected = false; // Stop accepting new connections
                    responseMessage = closeConnection(); // Close server
                    System.out.println("Server connection closed" + "\n");
                    break;

                default:
                    responseMessage = "ERROR - Invalid request type";
                    System.out.println("Received invalid request type" + "\n");
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
                System.out.println("Unable to close connection!" + "\n");
                return "Unable to close connection!";
            }
        }
    }
}