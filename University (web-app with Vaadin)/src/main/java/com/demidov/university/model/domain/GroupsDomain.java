package com.demidov.university.model.domain;

import java.util.List;
import java.util.logging.Logger;

import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.dao.interfaces.GroupDao;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.model.persistence.factory.impl.HsqldbDAOFactory;

/**
 * Domain object to manage with groups
 */
public class GroupsDomain {

	private final HsqldbDAOFactory daoFactory;
	private final GroupDao groupDAO;

	private static GroupsDomain instance;
	private static final Logger logger = Logger.getLogger(GroupsDomain.class.getName());

	public static synchronized GroupsDomain getInstance() {
		if (instance == null)
			instance = new GroupsDomain();
		return instance;
	}

	private GroupsDomain() {
		daoFactory = HsqldbDAOFactory.getInstance();
		groupDAO = daoFactory.getGroupDAO();
	}

	public List<Group> getAll() throws PersistException {
		return groupDAO.getAll();
	}

	public void createOrUpdate(final Group group) throws ValidException, PersistException {
		groupDAO.createOrUpdate(group);
	}

	public void delete(final long id) throws PersistException {
		groupDAO.delete(id);
	}

}
