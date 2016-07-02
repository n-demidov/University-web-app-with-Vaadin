package com.demidov.university.model.persistence.dao.filter;

/**
 * Object represents parameters for filtering in StudentDAO class
 */
public class StudentFilterParams {

	private final String lastName;
	private final Integer groupNumber;
	
	public StudentFilterParams(final String lastName, final Integer groupNumber) {
		super();
		this.lastName = lastName;
		this.groupNumber = groupNumber;
	}

	public String getLastName() {
		return lastName;
	}

	public Integer getGroupNumber() {
		return groupNumber;
	}

	@Override
	public String toString() {
		return "StudentFilterParams [lastName=" + lastName + ", groupNumber=" + groupNumber + "]";
	}
	
}
