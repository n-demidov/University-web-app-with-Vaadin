package com.demidov.university.model.persistence.factory.interfaces;

import com.demidov.university.model.persistence.dao.interfaces.GroupDAO;
import com.demidov.university.model.persistence.dao.interfaces.StudentDAO;

public interface DAOFactory {

	public StudentDAO getStudentDAO();
	
	public GroupDAO getGroupDAO();
	
}
