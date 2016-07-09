package com.demidov.university.model.persistence.factory.interfaces;

import com.demidov.university.model.persistence.dao.interfaces.GroupDao;
import com.demidov.university.model.persistence.dao.interfaces.StudentDao;

public interface DAOFactory {

	public StudentDao getStudentDAO();
	
	public GroupDao getGroupDAO();
	
}
