package com.bridgelabz.ioservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bridgelabz.exceptions.DBException;
import com.bridgelabz.model.EmployeePayrollData;

public class DatabaseIOService {

	public List<EmployeePayrollData> readData() throws DBException {
		String sql = "select * from employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try (Connection connection = this.establishConnection()) {
			System.out.println("Connection is successfull!!! " + connection);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			throw new DBException("Cannot establish connection",DBException.ExceptionType.CONNECTION_FAIL);
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) {
		// TODO Auto-generated method stub
		return 0;
	}

	private Connection establishConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String userName = "root";
		String password = "First12@";
		System.out.println("Establishing connection to database : " + jdbcURL);
		return DriverManager.getConnection(jdbcURL, userName, password);
	}
}