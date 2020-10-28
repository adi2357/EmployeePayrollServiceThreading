package com.bridgelabz.ioservice;

import java.util.ArrayList;
import java.util.List;

import com.bridgelabz.exceptions.DBException;
import com.bridgelabz.model.EmployeePayrollData;

public class DatabaseIOService {

	public List<EmployeePayrollData> readData() throws DBException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		return employeePayrollList;
	}

}
