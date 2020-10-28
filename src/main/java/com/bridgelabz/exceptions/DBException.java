package com.bridgelabz.exceptions;

public class DBException extends Exception {

	public enum ExceptionType{
		CONNECTION_FAIL, SQL_ERROR, UPDATE_ERROR
	}
	ExceptionType type;

	public DBException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}
