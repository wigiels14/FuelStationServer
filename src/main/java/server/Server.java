package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

import database.DatabaseManager;


public class Server {
	public static int PORT = 8080;
	public static ServerSocket serverSocket;
	public static Connection connection;
	public static DatabaseManager databaseManager;

	public static void main(String[] args) throws IOException, SQLException {
		databaseManager = DatabaseManager.getInstance();
		connection = databaseManager.CONNECTION;

		serverSocket = new ServerSocket(PORT);

		while (true) {
			Socket socket = serverSocket.accept();
			new ConnectedClient(connection,
						serverSocket, socket).start();
		}

	}
}