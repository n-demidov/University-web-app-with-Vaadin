package com.demidov.university.model.persistence.factory.impl;

import java.util.logging.Logger;

import com.demidov.university.model.persistence.dao.impl.HSQLDBGroupDao;
import com.demidov.university.model.persistence.dao.impl.HSQLDBStudentDao;
import com.demidov.university.model.persistence.dao.interfaces.GroupDao;
import com.demidov.university.model.persistence.dao.interfaces.StudentDao;
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
	public StudentDao getStudentDAO() {
		return HSQLDBStudentDao.getInstance();
	}

	@Override
	public GroupDao getGroupDAO() {
		return HSQLDBGroupDao.getInstance();
	}
	
}
