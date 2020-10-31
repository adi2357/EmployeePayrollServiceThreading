package com.bridgelabz.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollData {

	private int employeeId;
	private String employeeName;
	private double salary;
	private List<LocalDate> startDates;
	private String gender;
	private String companyName;
	private List<String> phoneNumbers;
	private List<String> departmentNames;

	public EmployeePayrollData() {
	}

	public EmployeePayrollData(int employeeId, String employeeName, double salary) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.salary = salary;
	}

	public EmployeePayrollData(String employeeName, double salary, String gender, String companyName) {
		this.employeeName = employeeName;
		this.salary = salary;
		this.gender = gender;
		this.companyName = companyName;
	}

	public EmployeePayrollData(int employeeId, String employeeName, double salary, List<LocalDate> startDates) {
		this(employeeId, employeeName, salary);
		this.startDates = startDates;
	}

	
	public EmployeePayrollData(int employeeId, String employeeName, double salary, List<LocalDate> startDates, String gender) {
		this(employeeId, employeeName, salary, startDates);
		this.gender = gender;
	}
	

	public EmployeePayrollData(int employeeId, String employeeName, double salary, List<LocalDate> startDates,
			String gender, String companyName, List<String> phoneNumbers, List<String> departmentNames) {
		this(employeeId, employeeName, salary, startDates, gender);
		this.companyName = companyName;
		this.phoneNumbers = phoneNumbers;
		this.departmentNames = departmentNames;
	}

	public EmployeePayrollData(String employeeName, double salary, LocalDate startDate, String gender, String companyName, String phoneNumber,
			String departmentName) {
		this(employeeName, salary, gender, companyName);
		
		if (startDates != null)		this.startDates.add(0, startDate);
		else {
			this.startDates = new ArrayList<>();
			this.startDates.add(startDate);
		}
		if (phoneNumbers != null)	this.phoneNumbers.add(0, phoneNumber);
		else {
			this.phoneNumbers = new ArrayList<>();
			this.phoneNumbers.add(phoneNumber);
		}
		if (departmentNames != null)	this.departmentNames.add(0, departmentName);
		else {
			departmentNames = new ArrayList<>();
			departmentNames.add(departmentName);
		}
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<LocalDate> getStartDates() {
		return startDates;
	}

	public void setStartDates(List<LocalDate> startDates) {
		this.startDates = startDates;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<String> getDepartmentNames() {
		return departmentNames;
	}

	public void setDepartmentNames(List<String> departmentNames) {
		this.departmentNames = departmentNames;
	}

	@Override
	public String toString() {
		return "Id : " + employeeId + "\t" + "Name : " + employeeName + "\t" + "Gender : " + gender + "\t" + "Salary : "
				+ salary + "\t" + "Company Name : " + companyName;
	}

	public String printString() {
		return "EmployeePayrollData [employeeId=" + employeeId + ", employeeName=" + employeeName + ", salary=" + salary
				+ ", startDates=" + startDates + ", gender=" + gender + ", companyName=" + companyName
				+ ", phoneNumbers=" + phoneNumbers + ", departmentNames=" + departmentNames + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (employeeId != other.employeeId)
			return false;
		if (employeeName == null) {
			if (other.employeeName != null)
				return false;
		} else if (!employeeName.equals(other.employeeName))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		return true;
	}

	
}
