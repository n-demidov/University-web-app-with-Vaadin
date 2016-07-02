package com.demidov.university.model.persistence.factory.impl;

import java.util.logging.Logger;

import com.demidov.university.model.persistence.dao.impl.HSQLDBGroupDAO;
import com.demidov.university.model.persistence.dao.impl.HSQLDBStudentDAO;
import com.demidov.university.model.persistence.dao.interfaces.GroupDAO;
import com.demidov.university.model.persistence.dao.interfaces.StudentDAO;
import com.demidov.university.model.persistence.factory.interfaces.DAOFactory;

public class HsqldbDAOFactory implements DAOFactory {

	private static HsqldbDAOFactory instance;
	private static final Logger logger = Logger.getLogger(HsqldbDAOFactory.class.getName());

	public static synchronized HsqldbDAOFactory getInstance() {
		if (instance == null)
			instance = new HsqldbDAOFactory();
		return instance;
	}

	private HsqldbDAOFactory() {
		super();
	}

	@Override
	public StudentDAO getStudentDAO() {
		return HSQLDBStudentDAO.getInstance();
	}

	@Override
	public GroupDAO getGroupDAO() {
		return HSQLDBGroupDAO.getInstance();
	}
	
}
