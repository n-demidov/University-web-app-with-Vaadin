package com.demidov.university.model.persistence.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.connection.DBConnection;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class AbstractJDBCDao {

	private static final String INTEGRITY_CONSTRAINT_VIOLATION_CODE = "23";
	private static final String PEPRSIST_ERR_MSG = "Возникла ошибка при обращении к базе данных. Пожалуйста, проверьте входные параметры.";
	protected static final String UNEXPECTED_POINT_EXECUTION = "Unexpected point execution"; 
	
	protected Connection connection;

	private static final ValidatorFactory validationFactory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = validationFactory.getValidator();

	private static final Logger logger = Logger.getLogger(AbstractJDBCDao.class.getName());

	public AbstractJDBCDao() {
		try {
			connection = DBConnection.getInstance().getConnection();
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Close ResultSet
	 * @param rs
	 */
	protected void closeResultSet(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
	}

	/**
	 * Close PreparedStatement
	 * @param ps
	 */
	protected void closePreparedStatement(final PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
	}

	/**
	 * Check entity for validity.
	 * Throw exception on first Constraint Violation
	 * @param object
	 * @throws edu.library.exceptions.ValidationException
	 */
	protected void validate(final Object object) throws ValidException {
		for (final ConstraintViolation<Object> cv : validator.validate(object)) {
			throw new ValidException(cv.getMessage());
		}
	}
	
	/**
	 * Process SQL exception.
	 * Log and then throw custom PersistException with some message instead of SQLException
	 * @param e
	 * @throws PersistException
	 */
	protected void processSQLException(final SQLException e) throws PersistException {
		logger.log(Level.SEVERE, null, e);
		throw new PersistException(PEPRSIST_ERR_MSG);
	}
	
	/**
	 * Return true if SQLException is an Integrity Constraint Violation Exception
	 * @param e
	 * @return
	 */
	protected static boolean isConstraintViolation(final SQLException e) {
	    return e.getSQLState().startsWith(INTEGRITY_CONSTRAINT_VIOLATION_CODE);
	}

}
