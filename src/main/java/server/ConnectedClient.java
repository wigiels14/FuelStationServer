package server;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

import client.ClientMessage;

public class ConnectedClient extends Thread {
	private final Socket socket;
	private final ServerSocket serverSocket;
	private final Connection conn;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private DataOutputStream userNumberStream;

	public ConnectedClient(Connection conn, ServerSocket serverSocket, Socket socket) {
		this.conn = conn;
		this.serverSocket = serverSocket;
		this.socket = socket;
	}

	void initStreams() {
		OutputStream outPut;
		try {
			outPut = socket.getOutputStream();
			out = new ObjectOutputStream(outPut);

			BufferedInputStream inPut = new BufferedInputStream(socket.getInputStream());
			in = new ObjectInputStream(inPut);

			userNumberStream = new DataOutputStream(socket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		initStreams();

		while (socket.isConnected()) {
			Object clientMessage = convertClientQuery();
			if (clientMessage instanceof ClientMessage) {
				if (((ClientMessage) clientMessage).messageType.equals("isUserInSystemDatabase")) {
					proceedIsUserInSystemDatabase((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("sendQuitApp")) {
					proceedSendExitApp();
				}
				if (((ClientMessage) clientMessage).messageType.equals("sendFetchManager")) {
					proceedSendFetchManager((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("sendFetchEmployee")) {
					proceedSendFetchEmployee((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("changeManagerFirstName")) {
					proceedChangeManagerFirstName((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("changeManagerLastName")) {
					proceedChangeManagerLastName((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("changeManagerPassword")) {
					proceedChangeManagerPassword((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("createEmployee")) {
					proceedCreateEmployee((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("fetchEmployees")) {
					proceedFetchEmployees((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("fireEmployee")) {
					proceedFireEmployee((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("addCustomer")) {
					proceedAddCustomer((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("realizeTransaction")) {
					proceedRealizeTransaction((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("fetchCustomers")) {
					proceedFetchCustomers((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("fetchCustomerTransactions")) {
					proceedFetchCustomerTransactions((ClientMessage) clientMessage);
				}
				if (((ClientMessage) clientMessage).messageType.equals("fetchCustomerAccount")) {
					proceedFetchCustomerAccount((ClientMessage) clientMessage);
				}
			}
		}
	}
	
	private void proceedFetchCustomerAccount(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("fetchCustomerAccount"));
		String[] customerAcc = Server.databaseManager.fetchCustomerACCByCustomerID(clientMessage.parameters[0]);
		
		response.parameters[0] = customerAcc[0];
		response.parameters[1] = customerAcc[1];
		
		sendResponse(response);
		
	}
	
	private void proceedFetchCustomerTransactions(ClientMessage clientMessage) {
		String customerID = clientMessage.parameters[0];
		
		ClientMessage response = (new ClientMessage("fetchCustomerTransactions"));
		ArrayList<Object>resultList = new ArrayList<Object>();
		ArrayList<String[]> customerTransactions = Server.databaseManager.fetchCustomerTransactions(customerID);
		
		resultList.addAll(customerTransactions);
		
		response.setSendingObjects(resultList);
		
		sendResponse(response);
	}
	
	private void proceedFetchCustomers(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("fetchCustomers"));
		response.parameters[0] = clientMessage.parameters[0];
		ArrayList<Object>resultList = new ArrayList<Object>();
		ArrayList<String[]> customers = Server.databaseManager.fetchCustomers();
		
		resultList.addAll(customers);
		response.setSendingObjects(resultList);
		sendResponse(response);
	}

	private void proceedRealizeTransaction(ClientMessage clientMessage) {
		String idNumber = clientMessage.parameters[0];
		String cost = clientMessage.parameters[1];
		String employeeID = clientMessage.parameters[2];

		System.out.println("Id number: " + idNumber);
		System.out.println("Cost: " + cost);
		System.out.println("Employee id: " + employeeID);

		Date date = (Date) clientMessage.getSendingObjects().get(0);

		int bonusPoints = Integer.decode(cost) / 50;

		String customerID = Server.databaseManager.fetchCustomerByIDNumber(idNumber)[0];
		String customerAccountID = Server.databaseManager.fetchCustomerACCIDByCustomerID(customerID);

		Server.databaseManager.createTransaction(customerAccountID, employeeID, date, cost,
				String.valueOf(bonusPoints));
		
		Server.databaseManager.addBousPointsToAccountBalance(String.valueOf(bonusPoints), customerID);

	}

	private void proceedAddCustomer(ClientMessage clientMessage) {
		String firstName = clientMessage.parameters[0];
		String lastName = clientMessage.parameters[1];
		String pesel = clientMessage.parameters[2];
		String idNumber = clientMessage.parameters[3];

		Server.databaseManager.addCustomer(firstName, lastName, pesel, idNumber);

		String[] customer = Server.databaseManager.fetchCustomerByIDNumber(idNumber);

		Server.databaseManager.addCustomerAccount(customer[0]);
	}

	private void proceedFireEmployee(ClientMessage clientMessage) {
		Server.databaseManager.deleteEmployeeByID(clientMessage.parameters[0]);

		ClientMessage response = (new ClientMessage("fireEmployee"));
		sendResponse(response);
	}

	private void proceedFetchEmployees(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("fetchEmployees"));

		String branchID = Server.databaseManager.fetchBranchIDByCityName(clientMessage.parameters[0]);
		ArrayList<String[]> employees = Server.databaseManager.fetchEmployeesByBranchID(branchID);

		ArrayList<Object> finalResult = new ArrayList<Object>();

		finalResult.addAll(employees);

		response.setSendingObjects(finalResult);

		sendResponse(response);

	}

	private void proceedCreateEmployee(ClientMessage clientMessage) {
		String firstName = clientMessage.parameters[0];
		String lastName = clientMessage.parameters[1];
		String PESEL = clientMessage.parameters[2];
		String idNumber = clientMessage.parameters[3];
		String password = clientMessage.parameters[4];
		String cityName = clientMessage.parameters[5];

		String branchId = Server.databaseManager.fetchBranchIDByCityName(cityName);

		Server.databaseManager.createEmployee(firstName, lastName, PESEL, idNumber, password, branchId);
	}

	private void proceedChangeManagerFirstName(ClientMessage clientMessage) {
		String managerID = clientMessage.parameters[0];
		String firstName = clientMessage.parameters[1];
		Server.databaseManager.changeManagerFirstName(managerID, firstName);
	}

	private void proceedChangeManagerLastName(ClientMessage clientMessage) {
		String managerID = clientMessage.parameters[0];
		String lastName = clientMessage.parameters[1];
		Server.databaseManager.changeManagerLastName(managerID, lastName);
	}

	private void proceedChangeManagerPassword(ClientMessage clientMessage) {
		String managerID = clientMessage.parameters[0];
		String password = clientMessage.parameters[1];
		Server.databaseManager.changeManagerPassword(managerID, password);
	}

	private void proceedSendFetchEmployee(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("sendFetchEmployee"));
		String[] manager = Server.databaseManager.fetchEmployee(clientMessage.parameters[0]);
		String branchCity = Server.databaseManager.fetchBranchCityByBranchID(manager[6]);

		response.parameters[0] = manager[0];
		response.parameters[1] = manager[1];
		response.parameters[2] = manager[2];
		response.parameters[3] = manager[3];
		response.parameters[4] = manager[4];
		response.parameters[5] = manager[5];
		response.parameters[5] = manager[6];
		response.parameters[7] = branchCity;

		sendResponse(response);
	}

	private void proceedSendFetchManager(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("sendFetchManager"));
		String[] manager = Server.databaseManager.fetchManager(clientMessage.parameters[0]);
		String branchCity = Server.databaseManager.fetchBranchCityByBranchID(manager[6]);

		response.parameters[0] = manager[0];
		response.parameters[1] = manager[1];
		response.parameters[2] = manager[2];
		response.parameters[3] = manager[3];
		response.parameters[4] = manager[4];
		response.parameters[5] = manager[5];
		response.parameters[5] = manager[6];
		response.parameters[7] = branchCity;

		sendResponse(response);
	}

	private void proceedSendExitApp() {
		this.stop();
	}

	private void proceedIsUserInSystemDatabase(ClientMessage clientMessage) {
		ClientMessage response = (new ClientMessage("isUserInSystemDatabase"));

		boolean isEmployeeInSystemDatabase = Server.databaseManager
				.isEmployeeInSystemDatabase(clientMessage.parameters[0], clientMessage.parameters[1]);
		if (isEmployeeInSystemDatabase) {
			response.parameters[0] = clientMessage.parameters[0];
			response.isMessageTrue = isEmployeeInSystemDatabase;
			response.parameters[9] = "employee";

			sendResponse(response);
			return;
		} else {
			boolean isCustomerInSystemDatabase = Server.databaseManager
					.isManagerInSystemDatabase(clientMessage.parameters[0], clientMessage.parameters[1]);
			response.parameters[0] = clientMessage.parameters[0];
			response.isMessageTrue = isCustomerInSystemDatabase;
			response.parameters[9] = "manager";

			sendResponse(response);
		}

	}

	private void sendResponse(ClientMessage response) {
		try {
			out.writeObject(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object convertClientQuery() {
		Object clientQuery = null;
		try {
			clientQuery = in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clientQuery;
	}
}
