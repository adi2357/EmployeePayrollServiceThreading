package com.bridgelabz.payrollservicetest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.bridgelabz.model.EmployeePayrollData;
import com.bridgelabz.payrollservice.EmployeePayrollService;
import com.bridgelabz.payrollservice.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {

	@Ignore
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = {
				new EmployeePayrollData(1, "Aditya Verma", 800000.0),
				new EmployeePayrollData(2, "Akhil Singh", 850000.0),
				new EmployeePayrollData(3, "Anamika Bhatt", 900000.0) 
				};

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		payrollServiceObject.writeEmployeeData(IOService.FILE_IO);
		payrollServiceObject.printEmployeePayrollData(IOService.FILE_IO);
		Assert.assertEquals(3, payrollServiceObject.countEnteries(IOService.FILE_IO));
	}

	@Ignore
	@Test
	public void given3EmployeesWhenReadFromFileShouldMatchNumberOfEmployeeEntries() {

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.FILE_IO);
		int countOfEntriesRead = payrollServiceObject.sizeOfEmployeeList();
		Assert.assertEquals(3, countOfEntriesRead);
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		int countOfEntriesRetrieved = payrollServiceObject.sizeOfEmployeeList();
		Assert.assertEquals(5, countOfEntriesRetrieved);
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		try {
			EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
			payrollServiceObject.readEmployeeData(IOService.DB_IO);
			payrollServiceObject.updateEmployeeSalary("Teresa", 3000000.0);
			boolean result = payrollServiceObject.checkEmployeePayrollInSyncWithDB("Teresa");
			Assert.assertTrue(result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = payrollServiceObject.readEmployeeDataForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(2, employeePayrollData.size());
	}

	@Test
	public void givenPayrollDataInDB_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = payrollServiceObject.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(1500000.0) && 
						  averageSalaryByGender.get("F").equals(3000000.0));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		try {
			EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
			payrollServiceObject.readEmployeeData(IOService.DB_IO);
			payrollServiceObject.addEmployeeToPayroll("Mark", 3000000.0, LocalDate.now(), "M", "Oracle", "7896543214", "Research & Development");
			boolean result = payrollServiceObject.checkEmployeePayrollInSyncWithDB("Mark");
			Assert.assertTrue(result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenEmployeeName_WhenRemoved_ShouldRemoveEmployeeFromListAndDB() {
		try {
			EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
			payrollServiceObject.readEmployeeData(IOService.DB_IO);
			payrollServiceObject.deleteEmployee("Mark");
			boolean result = payrollServiceObject.isDeleted("Mark");
			Assert.assertTrue(result);
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	@Test
	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeCount() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		EmployeePayrollData[] arrayOfEmployees = {
				new EmployeePayrollData("Tyson", 2000000.0, LocalDate.of(2017,01,01), "M", "Oracle", "7878456214", "Research & Development"),
				new EmployeePayrollData("Mayira", 4000000.0, LocalDate.of(2017,01,01), "F", "Bridgelabz", "9989956214", "Marketing"),
				new EmployeePayrollData("Gabriel", 4000000.0, LocalDate.of(2019,01,01), "M", "Capgemini", "9299956219", "Business & Ethics"),
				new EmployeePayrollData("Abel", 2000000.0, LocalDate.of(2018,01,01), "F", "Capgemini", "7889896214", "Human Resource"),
				new EmployeePayrollData("Rahel", 3000000.0, LocalDate.of(2019,01,01), "F", "Capgemini", "8889898214", "Sales")
				};
		Instant start = Instant.now();
		payrollServiceObject.addEmployeeListToPayroll(Arrays.asList(arrayOfEmployees));
		Instant end = Instant.now();
		System.out.println("Duration without Threading : " + Duration.between(start, end));
		Assert.assertEquals(10, payrollServiceObject.sizeOfEmployeeList());
	}

	@Test
	public void given6Employees_WhenAddedToDBUsingThreads_ShouldMatchEmployeeCount() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		EmployeePayrollData[] arrayOfEmployees = {
				new EmployeePayrollData("Tyson", 2000000.0, LocalDate.of(2017,01,01), "M", "Oracle", "7878456214", "Research & Development"),
				new EmployeePayrollData("Mayira", 4000000.0, LocalDate.of(2017,01,01), "F", "Bridgelabz", "9989956214", "Marketing"),
				new EmployeePayrollData("Gabriel", 4000000.0, LocalDate.of(2019,01,01), "M", "Capgemini", "9299956219", "Business & Ethics"),
				new EmployeePayrollData("Abel", 2000000.0, LocalDate.of(2018,01,01), "F", "Capgemini", "7889896214", "Human Resource"),
				new EmployeePayrollData("Rahel", 3000000.0, LocalDate.of(2019,01,01), "F", "Capgemini", "8889898214", "Sales")
				};
		Instant start = Instant.now();
		payrollServiceObject.addEmployeeListToPayrollUsingThreads(Arrays.asList(arrayOfEmployees));
		Instant end = Instant.now();
		System.out.println("Duration without Threading : " + Duration.between(start, end));
		Assert.assertEquals(10, payrollServiceObject.sizeOfEmployeeList());
	}
}
