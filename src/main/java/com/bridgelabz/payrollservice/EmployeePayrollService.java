package com.bridgelabz.payrollservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bridgelabz.exceptions.DBException;
import com.bridgelabz.ioservice.DatabaseIOService;
import com.bridgelabz.ioservice.FileIOService;
import com.bridgelabz.model.EmployeePayrollData;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	public static final Scanner SC = new Scanner(System.in);
	private List<EmployeePayrollData> employeePayrollList;
	private DatabaseIOService employeePayrollDBService;

	public EmployeePayrollService() {
		this.employeePayrollList = new ArrayList<EmployeePayrollData>();
		employeePayrollDBService = DatabaseIOService.getInstatnce();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeeList) {
		this();
		this.employeePayrollList = employeeList;
	}

	public int sizeOfEmployeeList() {
		return this.employeePayrollList.size();
	}

	public void readEmployeeData(IOService ioType) {
		if (ioType.equals(IOService.CONSOLE_IO)) {
			System.out.println("Enter employee id:");
			int employeeId = SC.nextInt();
			System.out.println("Enter employee name:");
			SC.nextLine();
			String employeeName = SC.nextLine();
			System.out.println("Enter employee salary:");
			double employeeSalary = SC.nextDouble();
			EmployeePayrollData newEmployee = new EmployeePayrollData(employeeId, employeeName, employeeSalary);
			employeePayrollList.add(newEmployee);
		} else if (ioType.equals(IOService.FILE_IO))
			this.employeePayrollList = new FileIOService().readData();
		else if (ioType.equals(IOService.DB_IO)) {
			try {
				this.employeePayrollList = employeePayrollDBService.readData();
			} catch (DBException e) {
				e.printStackTrace();
			}
		}
	}

	public List<EmployeePayrollData> readEmployeeDataForDateRange(IOService ioType, LocalDate startDate,LocalDate endDate) {
		if (ioType.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.readEmployeeDataForDateRange(startDate, endDate);
			} catch (DBException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioType) {
		if (ioType.equals(IOService.DB_IO))
			try {
				return employeePayrollDBService.readAverageSalaryByGender();
			} catch (DBException e) {
				e.printStackTrace();
			}
		return null;
	}

	public void updateEmployeeSalary(String name, double salary) throws DBException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if(result == 0)
			throw new DBException("Cannot update the employee payroll data", DBException.ExceptionType.UPDATE_ERROR);
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null)
			employeePayrollData.setSalary(salary);
		else
			throw new DBException("Cannot find the employee payroll data", DBException.ExceptionType.INVALID_PAYROLL_DATA);
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				   .filter(employeeData -> employeeData.getEmployeeName().equals(name))
				   .findFirst()
				   .orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws DBException {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmplyoeePayrollDataUsingName(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public void writeEmployeeData(IOService ioType) {
		if (ioType.equals(IOService.CONSOLE_IO)) {
			for (EmployeePayrollData o : employeePayrollList)
				System.out.println(o.toString());
		} else if (ioType.equals(IOService.FILE_IO)) {
			new FileIOService().writeData(employeePayrollList);
		}
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		try {
			if(this.getEmployeePayrollData(name) == null)
				this.employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	public long countEnteries(IOService ioType) {
		if (ioType.equals(IOService.FILE_IO))
			return new FileIOService().countEntries();
		return 0;
	}

	public void printEmployeePayrollData(IOService ioType) {
		if (ioType.equals(IOService.FILE_IO))
			new FileIOService().printEmployeePayrolls();
		else
			this.employeePayrollList.stream().forEach(employeeData -> System.out.println(employeeData.toString()));
	}
}
