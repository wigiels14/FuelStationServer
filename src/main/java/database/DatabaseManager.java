package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import server.Server;

public class DatabaseManager {
	private static DatabaseManager databaseManagerInstance;
	public final Connection CONNECTION;

	private class DatabaseConnection {
		private final String url = "jdbc:postgresql://localhost:5432/FuelStations";
		private final String username = "postgres";
		private final String password = "admin";

		public Connection connectToDatabase() {
			Connection connection = null;
			try {
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
			return connection;
		}
	}

	public static DatabaseManager getInstance() {
		if (databaseManagerInstance == null) {
			databaseManagerInstance = new DatabaseManager();
		}
		return databaseManagerInstance;
	}

	public DatabaseManager() {
		CONNECTION = new DatabaseConnection().connectToDatabase();
	}

	public boolean isEmployeeInSystemDatabase(String idNumber, String password) {
		String query = "SELECT COUNT(*) AS amount FROM EMPLOYEE WHERE ID_NUMBER = ? AND PASSWORD =  ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, idNumber);
			myStatement.setString(2, password);

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String resultString = queryResult.getString("amount");
				if (resultString.equals("1")) {
					return true;
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean isManagerInSystemDatabase(String idNumber, String password) {
		String query = "SELECT COUNT(*) AS amount FROM MANAGER WHERE ID_NUMBER = ? AND PASSWORD =  ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, idNumber);
			myStatement.setString(2, password);

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String resultString = queryResult.getString("amount");
				if (resultString.equals("1")) {
					return true;
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String[] fetchManager(String idNumber) {
		String query = "SELECT ID, FIRST_NAME, LAST_NAME, PESEL, PASSWORD, BRANCH_ID"
				+ " FROM MANAGER WHERE ID_NUMBER = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, idNumber);

			queryResult = myStatement.executeQuery();

			String id = null, firstName = null, lastName = null, pesel = null, password = null;
			int branchID = 0;

			while (queryResult.next()) {
				id = queryResult.getString("ID");
				firstName = queryResult.getString("FIRST_NAME");
				lastName = queryResult.getString("LAST_NAME");
				pesel = queryResult.getString("PESEL");
				password = queryResult.getString("PASSWORD");
				branchID = queryResult.getInt("BRANCH_ID");
			}
			return new String[] { id, firstName, lastName, pesel, password, idNumber, String.valueOf(branchID) };

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[] fetchEmployee(String idNumber) {
		String query = "SELECT ID, FIRST_NAME, LAST_NAME, PESEL, PASSWORD, BRANCH_ID"
				+ " FROM EMPLOYEE WHERE ID_NUMBER = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, idNumber);

			queryResult = myStatement.executeQuery();

			String id = null, firstName = null, lastName = null, pesel = null, password = null;
			int branchID = 0;

			while (queryResult.next()) {
				id = queryResult.getString("ID");
				firstName = queryResult.getString("FIRST_NAME");
				lastName = queryResult.getString("LAST_NAME");
				pesel = queryResult.getString("PESEL");
				password = queryResult.getString("PASSWORD");
				branchID = queryResult.getInt("BRANCH_ID");
			}
			return new String[] { id, firstName, lastName, pesel, password, idNumber, String.valueOf(branchID) };

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	 
	 public void addBousPointsToAccountBalance(String bonusPoints, String customerID) {
			String query = "UPDATE CUSTOMER_ACCOUNT SET BONUS_POINTS = BONUS_POINTS + ? WHERE CUSTOMER_ID = ?";

			PreparedStatement myStatement;
			ResultSet queryResult = null;
			try {
				myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
				myStatement.setInt(1, Integer.decode(bonusPoints));
				myStatement.setInt(2, Integer.decode(customerID));

				queryResult = myStatement.executeQuery();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	 
	 public void substractBousPointsToAccountBalance(String bonusPoints, String customerID) {
			String query = "UPDATE CUSTOMER_ACCOUNT SET BONUS_POINTS = BONUS_POINTS - ? WHERE CUSTOMER_ID = ?";

			PreparedStatement myStatement;
			ResultSet queryResult = null;
			try {
				myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
				myStatement.setInt(1, Integer.decode(bonusPoints));
				myStatement.setInt(2, Integer.decode(customerID));

				queryResult = myStatement.executeQuery();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	public String fetchBranchCityByBranchID(String branchID) {
		String query = "SELECT CITY" + " FROM BRANCH WHERE ID = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setInt(1, Integer.decode(branchID));

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String city = queryResult.getString("CITY");
				return city;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String fetchBranchIDByCityName(String cityName) {
		String query = "SELECT ID" + " FROM BRANCH WHERE CITY = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, cityName);

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String id = queryResult.getString("ID");
				return id;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void changeManagerFirstName(String managerID, String firstName) {
		String query = "SELECT change_manager_firstName(?,?);";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setString(1, managerID);
			myStatement.setString(2, firstName);

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void changeManagerLastName(String managerID, String lastName) {
		String query = "SELECT change_manager_lastName(?,?);";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setString(1, managerID);
			myStatement.setString(2, lastName);

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void changeManagerPassword(String managerID, String password) {
		String query = "SELECT change_manager_password(?,?);";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setString(1, managerID);
			myStatement.setString(2, password);

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteEmployeeByID(String id) {
		String query = "DELETE FROM EMPLOYEE WHERE ID = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setInt(1, Integer.decode(id));

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createEmployee(String firstName, String lastName, String PESEL, String idNumber, String password,
			String branchID) {
		String query = "SELECT create_employee(?, ?, ?, ?, ?, ?)";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setString(1, firstName);
			myStatement.setString(2, lastName);
			myStatement.setString(3, PESEL);
			myStatement.setString(4, idNumber);
			myStatement.setString(5, password);
			myStatement.setInt(6, Integer.decode(branchID));

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] fetchCustomerByIDNumber(String idNumber) {
		String query = "SELECT ID, FIRST_NAME, LAST_NAME, PESEL, ID_NUMBER FROM CUSTOMER" + " WHERE ID_NUMBER = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setString(1, idNumber);

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String id = queryResult.getString("ID");
				String firstName = queryResult.getString("FIRST_NAME");
				String lastName = queryResult.getString("LAST_NAME");
				String pesel = queryResult.getString("PESEL");

				return new String[] { id, firstName, lastName, pesel, idNumber };
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public String fetchCustomerACCIDByCustomerID(String customerID) {
		String query = "SELECT ID FROM CUSTOMER_ACCOUNT" + " WHERE CUSTOMER_ID = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setInt(1, Integer.decode(customerID));

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String id = queryResult.getString("ID");

				return id;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] fetchCustomerACCByCustomerID(String customerID) {
		String query = "SELECT ID, BONUS_POINTS FROM CUSTOMER_ACCOUNT" + " WHERE CUSTOMER_ID = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setInt(1, Integer.decode(customerID));

			queryResult = myStatement.executeQuery();

			while (queryResult.next()) {
				String id = queryResult.getString("ID");
				String bonusPoints = queryResult.getString("BONUS_POINTS");


				return new String[] {id, bonusPoints};
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void createTransaction(String customerAccountID, String employeeID, Date date, String cost,
			String addedPoints) {
		String query = "SELECT create_transaction(?, ?, ?, ?, ?)";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setDate(1, date);
			myStatement.setInt(2, Integer.decode(customerAccountID));
			myStatement.setInt(3, Integer.decode(employeeID));
			myStatement.setInt(4, Integer.decode(cost));
			myStatement.setInt(5, Integer.decode(addedPoints));

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addCustomerAccount(String customerID) {
		String query = "SELECT create_customer_account(?)";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setInt(1, Integer.decode(customerID));

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addCustomer(String firstName, String lastName, String PESEL, String idNumber) {
		String query = "SELECT create_customer(?, ?, ?, ?)";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);

			myStatement.setString(1, firstName);
			myStatement.setString(2, lastName);
			myStatement.setString(3, PESEL);
			myStatement.setString(4, idNumber);

			queryResult = myStatement.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String[]> fetchEmployeesByBranchID(String branchID) {
		ArrayList<String[]> resArrayList = new ArrayList<String[]>();
		String query = "SELECT ID, FIRST_NAME, LAST_NAME, PESEL, ID_NUMBER, PASSWORD, BRANCH_ID FROM EMPLOYEE"
				+ " WHERE BRANCH_ID = ?";

		PreparedStatement myStatement;
		ResultSet queryResult = null;
		int result = Integer.decode(branchID);
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setInt(1, result);

			queryResult = myStatement.executeQuery();

			String id = null, firstName = null, lastName = null, pesel = null, idNumber = null, password = null;
			int iterator = 0;
			while (queryResult.next()) {
				id = queryResult.getString("ID");
				firstName = queryResult.getString("FIRST_NAME");
				lastName = queryResult.getString("LAST_NAME");
				pesel = queryResult.getString("PESEL");
				idNumber = queryResult.getString("ID_NUMBER");
				password = queryResult.getString("PASSWORD");
				branchID = queryResult.getString("BRANCH_ID");

				String[] resString = { id, firstName, lastName, pesel, idNumber, password, branchID };
				resArrayList.add(resString);
			}
			return resArrayList;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String[]> fetchCustomers() {
		ArrayList<String[]> resArrayList = new ArrayList<String[]>();
		String query = "SELECT ID, FIRST_NAME, LAST_NAME, PESEL, ID_NUMBER FROM CUSTOMER";
		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);


			queryResult = myStatement.executeQuery();

			String id = null, firstName = null, lastName = null, pesel = null, idNumber = null;
			int iterator = 0;
			while (queryResult.next()) {
				id = queryResult.getString("ID");
				firstName = queryResult.getString("FIRST_NAME");
				lastName = queryResult.getString("LAST_NAME");
				pesel = queryResult.getString("PESEL");
				idNumber = queryResult.getString("ID_NUMBER");

				String[] resString = { id, firstName, lastName, pesel, idNumber};
				resArrayList.add(resString);
			}
			return resArrayList;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String[]> fetchCustomerTransactions(String customerID) {
		ArrayList<String[]> resArrayList = new ArrayList<String[]>();
		String query = "SELECT ID, EMPLOYEE_ID, DATE, COST, ADDED_BONUS_POINTS FROM TRANSACTION"
				+" WHERE CUSTMER_ACCOUNT_ID = (SELECT ID FROM CUSTOMER_ACCOUNT WHERE CUSTOMER_ID = ?);";
		PreparedStatement myStatement;
		ResultSet queryResult = null;
		try {
			myStatement = Server.databaseManager.CONNECTION.prepareStatement(query);
			myStatement.setInt(1, Integer.decode(customerID));

			queryResult = myStatement.executeQuery();

			String id = null, employeeID = null, cost = null, addedBonusPoints = null;
			Date date = null;
			int iterator = 0;
			while (queryResult.next()) {
				id = queryResult.getString("ID");
				employeeID = queryResult.getString("EMPLOYEE_ID");
				cost = queryResult.getString("COST");
				addedBonusPoints = queryResult.getString("ADDED_BONUS_POINTS");
				date = queryResult.getDate("DATE");
				
				SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd-hh:mm");

				String[] resString = {id, customerID, employeeID, cost, simpleDate.format(date), addedBonusPoints};
				resArrayList.add(resString);
			}
			return resArrayList;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
