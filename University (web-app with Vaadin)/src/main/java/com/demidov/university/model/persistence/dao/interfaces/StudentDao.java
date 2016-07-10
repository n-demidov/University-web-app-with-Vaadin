package com.demidov.university.model.persistence.dao.interfaces;

import java.util.List;

import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.persistence.dao.filter.StudentFilterParams;
import com.demidov.university.model.persistence.entity.Student;

public interface StudentDao extends GeneralDao<Student, Long> {

	public List<Student> filter(final StudentFilterParams filterParams) throws PersistException;
	
}
