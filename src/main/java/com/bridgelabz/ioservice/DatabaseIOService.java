package com.bridgelabz.ioservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bridgelabz.exceptions.DBException;
import com.bridgelabz.model.EmployeePayrollData;

public class DatabaseIOService {

	private int connectionCounter = 0;
	private PreparedStatement employeePayrollDataStatement;
	private static DatabaseIOService employeeDBService;

	private DatabaseIOService() {
	}

	public static DatabaseIOService getInstatnce() {
		if(employeeDBService == null)
			employeeDBService = new DatabaseIOService();
		return employeeDBService;
	}

	private synchronized Connection establishConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "First12@";
		Connection connection;
		connectionCounter++;
		System.out.println("Processing Thread : " + Thread.currentThread().getName() + 
						   " Establishing connection to database with Id : " + connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Processing Thread : " + Thread.currentThread().getName() + 
						   " Id : " + connectionCounter + " Connection is successfull!!! " + connection);
		return connection;
	}

	private void prepareStatementForEmployeeData() throws DBException {
		String sql = "select company.name as company_name, employee.id as employee_id, employee.name as employee_name, "
					+ "employee.gender as gender, payroll.basic_pay as basic_pay "
					+ "from employee "
					+ "inner join company		on company.id = employee.company_id "
					+ "inner join payroll		on payroll.employee_id = employee.id "
					+ "where employee.is_active = true and employee.name = ?;";
		try {
			Connection connection = this.establishConnection();
			this.employeePayrollDataStatement = connection.prepareStatement(sql);
		}catch (SQLException e) {
			throw new DBException("Cannot establish connection", DBException.ExceptionType.CONNECTION_FAIL);
		}
	}

	public List<EmployeePayrollData> readData() throws DBException {
		String sql ="select company.name as company_name, employee.id as employee_id, employee.name as employee_name, employee.gender as gender, payroll.basic_pay as basic_pay "
				+ "from employee "
				+ "inner join company		on company.id = employee.company_id "
				+ "inner join payroll		on payroll.employee_id = employee.id "
				+ "where employee.is_active = true; ";
		return this.getEmplyoeePayrollDataUsingDB(sql);
	}

	public List<EmployeePayrollData> getEmplyoeePayrollDataUsingName(String name) throws DBException {
		List<EmployeePayrollData> employeePayrollList = null;
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmplyoeePayrollDataUsingResultSet(resultSet);			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Cannot execute query", DBException.ExceptionType.SQL_ERROR);
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> readEmployeeDataForDateRange(LocalDate startDate, LocalDate endDate) throws DBException {
		String sql = String.format("select company.name as company_name, employee.id as employee_id, employee.name as employee_name, employee.gender as gender, payroll.basic_pay as basic_pay "
				+ "from employee "
				+ "inner join company				on company.id = employee.company_id "
				+ "inner join payroll				on payroll.employee_id = employee.id "
				+ "where employee.is_active = true and employee.id in "
				+ "(select employee_id from employee_department "
				+ "where start_date between '%s' and '%s');",Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmplyoeePayrollDataUsingDB(sql);
	}

	public Map<String, Double> readAverageSalaryByGender() throws DBException {
		String sql = "select employee.gender as gender, avg(payroll.basic_pay) as average_salary "
				+ "from employee "
				+ "inner join payroll "
				+ "on employee.id = payroll.employee_id "
				+ "where employee.is_active = true "
				+ "group by employee.gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = this.establishConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				double averageSalary = resultSet.getDouble("average_salary");
				genderToAverageSalaryMap.put(gender, averageSalary);
			}
		} catch (SQLException e) {
			throw new DBException("Cannot establish connection",DBException.ExceptionType.CONNECTION_FAIL);
		}
		return genderToAverageSalaryMap;
	}

	private List<EmployeePayrollData> getEmplyoeePayrollDataUsingDB(String sql) throws DBException {
		List<EmployeePayrollData> employeePayrollList = null;
		try {
			Connection connection = this.establishConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmplyoeePayrollDataUsingResultSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Cannot establish connection",DBException.ExceptionType.CONNECTION_FAIL);
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmplyoeePayrollDataUsingResultSet(ResultSet resultSet) throws DBException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try {
			while (resultSet.next()) {

				String companyName = resultSet.getString("company_name");
				int id = resultSet.getInt("employee_id");
				String employeeName = resultSet.getString("employee_name");
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("basic_pay");
				
				List<LocalDate> startDateList = new ArrayList<>();
				List<String> departmentNameList = new ArrayList<>();
				List<String> phoneNumberList = new ArrayList<>();
				try(Connection connection = this.establishConnection()){
					String sql = String.format("select employee.id as employee_id, department.name as department_name , "
											 + "employee_department.start_date as start_date "
											 + "from employee "
											 + "inner join employee_department	on employee.id = employee_department.employee_id "
											 + "inner join department			on employee_department.department_id = department.id "
											 + "where employee.is_active = true and employee.id = %s;",id);
					Statement statement = connection.createStatement();
					ResultSet resultSetForDepartmentNameAndStartDate = statement.executeQuery(sql);
					while(resultSetForDepartmentNameAndStartDate.next()) {
						startDateList.add(resultSetForDepartmentNameAndStartDate.getDate("start_date").toLocalDate());
						departmentNameList.add(resultSetForDepartmentNameAndStartDate.getString("department_name"));
					}
				}
				try(Connection connection = this.establishConnection()){
					String sql = String.format("select phone_number from employee_phone where employee_id = %s;", id);
					Statement statement = connection.createStatement();
					ResultSet resultSetForPhoneNumber = statement.executeQuery(sql);
					while(resultSetForPhoneNumber.next()) phoneNumberList.add(resultSetForPhoneNumber.getString("phone_number"));
				}				
				employeePayrollList.add(new EmployeePayrollData(id, employeeName, salary, startDateList, gender, companyName, phoneNumberList, departmentNameList));
			}
		} catch (SQLException e) {
			throw new DBException("Cannot populate employee payroll data", DBException.ExceptionType.RETRIEVE_ERROR);
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) throws DBException {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws DBException {
		String sql = "update payroll set basic_pay = ? where employee_id = "
				+ "(select employee.id "
				+ "from employee "
				+ "where name = ? and is_active = true);";
		try (Connection connection = this.establishConnection()){
			PreparedStatement employeePayrollUpdateStatement = connection.prepareStatement(sql);
			employeePayrollUpdateStatement.setDouble(1,salary);
			employeePayrollUpdateStatement.setString(2, name);
			return employeePayrollUpdateStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException("Cannot establish connection", DBException.ExceptionType.CONNECTION_FAIL);
		}
	}

	@SuppressWarnings("unused")
	private int updateEmployeeDataUsingStatement(String name, double salary) throws DBException {
		String sql = String.format("update payroll set basic_pay = %.2f where employee_id = "
				+ "(select employee.id "
				+ "from employee "
				+ "where name = '%s' and is_active = true);", salary, name);
		try (Connection connection = this.establishConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DBException("Cannot establish connection", DBException.ExceptionType.CONNECTION_FAIL);
		}
	}

	public EmployeePayrollData addEmployeeToPayroll(String employeeName, double salary, LocalDate startDate, String gender, String companyName, String phoneNumber, String departmentName) throws DBException {
		EmployeePayrollData[] newEmployeePayrollData = new EmployeePayrollData[1];
		Connection[] connection = new Connection[1];
		try {
			connection[0] = this.establishConnection();
			connection[0].setAutoCommit(false);
		} catch (SQLException e) {
			try {
				connection[0].rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		int companyId = insertIntoCompanyTable(connection[0], companyName);
		int employeeId = insertIntoEmployeeTable(connection[0], employeeName, gender, companyId);
		int departmentId = insertIntoDepartmentTable(connection[0], departmentName);
		
		Map<Integer, Boolean> employeePhoneAdditionStatus = new HashMap<Integer, Boolean>();
		Runnable employeePhoneAddition = () -> {
			employeePhoneAdditionStatus.put(employeeId, false);
			try {
				boolean isEmployeePhoneUpdated = insertIntoEmployeePhoneTable(connection[0], employeeId, phoneNumber);
				employeePhoneAdditionStatus.put(employeeId, isEmployeePhoneUpdated);
			} catch (DBException e) {
				e.printStackTrace();
			}
		};
		Thread employeePhoneThread = new Thread(employeePhoneAddition);
		employeePhoneThread.start();
		while(employeePhoneAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		Map<Integer, Boolean> employeeDepartmentAdditionStatus = new HashMap<Integer, Boolean>();
		Runnable employeeDepartmentAddition = () -> {
			employeeDepartmentAdditionStatus.put(departmentId, false);
			try {
				boolean isEmployeeDepartmentUpdated = insertIntoEmployeeDepartment(connection[0], employeeId, departmentName, startDate, departmentId);
				employeeDepartmentAdditionStatus.put(departmentId, isEmployeeDepartmentUpdated);
			} catch (DBException e) {
				e.printStackTrace();
			}
		};
		Thread employeeDepartmentThread = new Thread(employeeDepartmentAddition);
		employeeDepartmentThread.start();
		while(employeeDepartmentAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		Map<Integer, Boolean> employeePayrollAdditionStatus = new HashMap<Integer, Boolean>();
		Runnable payrollAddition = () -> {
			employeePayrollAdditionStatus.put(employeeId, false);
			boolean isPayrollUpdated;
			try {
				isPayrollUpdated = insertIntoPayrollTable(connection[0], employeeId, salary);
				if(isPayrollUpdated) {
					newEmployeePayrollData[0] = new EmployeePayrollData(employeeId, employeeName, salary, startDate, gender, companyName, phoneNumber, departmentName);
				}
				employeePayrollAdditionStatus.put(employeeId, isPayrollUpdated);
			} catch (DBException e) {
				e.printStackTrace();
			}
		};
		Thread employeePayrollThread = new Thread(payrollAddition);
		employeePayrollThread.start();
		while(employeePayrollAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			connection[0].commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection[0].close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return newEmployeePayrollData[0];
	}
	private int insertIntoCompanyTable(Connection connection, String companyName) {
		int companyId =-1;
		try (Statement statement = connection.createStatement()) {
			String sqlToRetrieveCompanyId = String.format("select id from company where name = '%s';", companyName);
			ResultSet resultSetToRetrieveCompanyId = statement.executeQuery(sqlToRetrieveCompanyId);
			if(resultSetToRetrieveCompanyId.next()) {
				companyId = resultSetToRetrieveCompanyId.getInt("id");
				System.out.println("Retrieved company id : "+companyId);
			}
			else {
				String sqlToInsert = String.format("insert into company (name) values ('%s');", companyName);
				int companyRowsUpdated = statement.executeUpdate(sqlToInsert, statement.RETURN_GENERATED_KEYS);
				if(companyRowsUpdated == 1) {
					ResultSet resultSet = statement.getGeneratedKeys();
					if(resultSet.next())
						companyId = resultSet.getInt(1);
				}
				System.out.println("Inserted company id : "+companyId);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return companyId;
	}

	private int insertIntoEmployeeTable(Connection connection, String employeeName, String gender, int companyId) {
		int employeeId = -1;
		try (Statement statement = connection.createStatement()) {
			String sqlToRetrieveEmployeeId = String.format("select id from employee where name ='%s' and gender = '%s' and company_id = %s;", employeeName, gender, companyId);
			ResultSet resultSetToRetrieveEmployeeId = statement.executeQuery(sqlToRetrieveEmployeeId);
			if(resultSetToRetrieveEmployeeId.next()) {
				employeeId = resultSetToRetrieveEmployeeId.getInt("id");
				System.out.println("Retrieved employee id : "+employeeId);
			}
			else {
				String sqlToInsert = String.format("insert into employee (name, gender, company_id) values "
										 + "('%s', '%s', %s);", employeeName, gender, companyId);
				int employeeRowsUpdated = statement.executeUpdate(sqlToInsert, statement.RETURN_GENERATED_KEYS);
				if(employeeRowsUpdated == 1) {
					ResultSet resultSet = statement.getGeneratedKeys();
					if(resultSet.next())
						employeeId = resultSet.getInt(1);
				}
				System.out.println("Inserted employee id : "+employeeId);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return employeeId;
	}

	private boolean insertIntoEmployeePhoneTable(Connection connection, int employeeId, String phoneNumber) throws DBException{
		boolean isUpdated = false;
		List<String> phoneNumberList = new ArrayList<>();
		try (Statement statement = connection.createStatement()) {
			String sqlToRetrievePhone = String.format("select phone_number from employee_phone where employee_id = %s;", employeeId);
			ResultSet resultSetToRetrievePhone = statement.executeQuery(sqlToRetrievePhone);
			while(resultSetToRetrievePhone.next()) {
				phoneNumberList.add(resultSetToRetrievePhone.getString("phone_number"));
				System.out.println("Phone number retrieved : YES");
			}if(!phoneNumberList.contains(phoneNumber)) {
				String sqlToInsert = String.format("insert into employee_phone (employee_id, phone_number) values "
										 + "('%s', '%s');", employeeId, phoneNumber);
				int phoneRowsUpdated = statement.executeUpdate(sqlToInsert, statement.RETURN_GENERATED_KEYS);
				if(phoneRowsUpdated == 1) {
					phoneNumberList.add(phoneNumber);
					isUpdated = true;
				}
				System.out.println("Phone number inserted : YES");
			}else {
				isUpdated = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return isUpdated;
	}

	private int insertIntoDepartmentTable(Connection connection, String departmentName) {
		int departmentId = -1;
		try (Statement statement = connection.createStatement()) {
			String sqlToRetrieveDepartmentId = String.format("select id from department where name = '%s';", departmentName);
			ResultSet resultSetToRetrieveDepartmentId = statement.executeQuery(sqlToRetrieveDepartmentId);
			if(resultSetToRetrieveDepartmentId.next()) {
				departmentId = resultSetToRetrieveDepartmentId.getInt("id");
				System.out.println("Retrieved department id : "+ departmentId);
			}else {
				String sqlToInsert = String.format("insert into department (name) values ('%s');", departmentName);
				int departmentRowsUpdated = statement.executeUpdate(sqlToInsert, statement.RETURN_GENERATED_KEYS);
				if(departmentRowsUpdated == 1) {
					ResultSet resultSet = statement.getGeneratedKeys();
					if(resultSet.next())
						departmentId = resultSet.getInt(1);
				}
				System.out.println("Inserted department id : "+ departmentId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return departmentId;
	}

	private boolean insertIntoEmployeeDepartment(Connection connection, int employeeId, String departmentName, LocalDate startDate, int departmentId) throws DBException {
		boolean isUpdated = false;
		List<LocalDate> startDateList = new ArrayList<>();
		List<String> departmentNameList = new ArrayList<>();
		try (Statement statement = connection.createStatement()) {
			String sqlToRetrieveDateAndDepartment = String.format("select start_date, department_id , department.name as department_name  from employee_department "
													+ "inner join department on employee_department.department_id = department.id "
													+ "where employee_id = %s;", employeeId);
			ResultSet resultSetToRetrieveDateAndDepartment = statement.executeQuery(sqlToRetrieveDateAndDepartment);
			while(resultSetToRetrieveDateAndDepartment.next()) {
				startDateList.add(resultSetToRetrieveDateAndDepartment.getDate("start_date").toLocalDate());
				departmentNameList.add(resultSetToRetrieveDateAndDepartment.getString("department_name"));
				System.out.println("Start date and Department name Retrieved : YES");
			}
			if(!startDateList.contains(startDate) || !departmentNameList.contains(departmentName))  {
				String sqlToInsert = String.format("insert into employee_department (start_date, department_id, employee_id) values "
										 + "( '%s', %s, %s);", Date.valueOf(startDate), departmentId, employeeId);
				int employeeDepartmentRowsUpdated = statement.executeUpdate(sqlToInsert, statement.RETURN_GENERATED_KEYS);
				if(employeeDepartmentRowsUpdated == 1) {
					isUpdated = true;
					startDateList.add(startDate);
					departmentNameList.add(departmentName);
					System.out.println("Start date and Department name Inserted : YES");
				}
			}
			else {
				isUpdated = true;
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return isUpdated;
	}

	public boolean insertIntoPayrollTable(Connection connection, int employeeId, double salary) throws DBException {
		boolean isUpdated = false;
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double incomeTax = (salary - deductions)* 0.1;
			String sql = String.format("insert into payroll (employee_id, basic_pay, deductions, income_tax) values"
									 + "(%s, %s, %s, %s)",employeeId, salary, deductions, incomeTax);
			int payrollRowsUpdated = statement.executeUpdate(sql);
			if(payrollRowsUpdated == 1) {
				isUpdated = true;
				System.out.println("Payroll Table Updated");
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return isUpdated;
	}

	public List<EmployeePayrollData> deleteEmployee(String employeeName) throws DBException {
		String sqlToDeleteEmployee = String.format("update employee set is_active = false where name = '%s';", employeeName);
		try(Connection connection = this.establishConnection()){
			Statement statementToDeleteEmployee = connection.createStatement();
			int resultSetToDeleteEmployee = statementToDeleteEmployee.executeUpdate(sqlToDeleteEmployee);
			return this.readData();
		} catch (SQLException e) {
			throw new DBException("Cannot establish connection", DBException.ExceptionType.CONNECTION_FAIL);
		}
	}
}
