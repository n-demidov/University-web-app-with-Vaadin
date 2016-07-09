package com.demidov.university.model.domain;

import java.util.List;
import java.util.logging.Logger;

import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.dao.filter.StudentFilterParams;
import com.demidov.university.model.persistence.dao.interfaces.StudentDao;
import com.demidov.university.model.persistence.entity.Student;
import com.demidov.university.model.persistence.factory.impl.HsqldbDAOFactory;

/**
 * Domain object to manage with students
 */
public class StudentsDomain {

	private final HsqldbDAOFactory daoFactory;
	private final StudentDao studentDAO;
	
	private static StudentsDomain instance;
	private static final Logger logger = Logger.getLogger(StudentsDomain.class.getName());

	public static synchronized StudentsDomain getInstance() {
		if (instance == null)
			instance = new StudentsDomain();
		return instance;
	}

	private StudentsDomain() {
		daoFactory = HsqldbDAOFactory.getInstance();
		studentDAO = daoFactory.getStudentDAO();
	}
	
	public List<Student> getAll() throws PersistException {
		return studentDAO.getAll();
	}
	
	public List<Student> filter(final StudentFilterParams filterParams) throws PersistException {
		return studentDAO.filter(filterParams);
	}

	public void createOrUpdate(final Student student) throws ValidException, PersistException {
		studentDAO.createOrUpdate(student);
	}
	
	public void delete(final long id) throws PersistException {
		studentDAO.delete(id);
	}
	
}
