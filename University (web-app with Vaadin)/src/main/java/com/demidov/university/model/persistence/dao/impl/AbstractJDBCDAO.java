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

public abstract class AbstractJDBCDAO {

	private static final String PEPRSIST_ERR_MSG = "Возникла ошибка при обращении к базе данных. Пожалуйста, проверьте входные параметры.";
	protected static final String UNEXPECTED_POINT_EXECUTION = "Unexpected point execution"; 
	
	protected Connection connection;

	private static final ValidatorFactory validationFactory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = validationFactory.getValidator();

	private static final Logger logger = Logger.getLogger(AbstractJDBCDAO.class.getName());

	public AbstractJDBCDAO() {
		try {
			connection = DBConnection.getInstance().getConnection();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}

	// Закрывает ResultSet
	protected void closeResultSet(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
	}

	// Закрывает PreparedStatement
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
	 * Проверяет данные на валидность - показывает первую ошибку
	 * 
	 * @param object
	 * @throws edu.library.exceptions.ValidationException
	 */
	protected void validate(final Object object) throws ValidException {
		for (final ConstraintViolation<Object> cv : validator.validate(object)) {
			throw new ValidException(cv.getMessage());
		}
	}
	
	protected void processSQLException(final SQLException e) throws PersistException {
		logger.log(Level.SEVERE, null, e);
		throw new PersistException(PEPRSIST_ERR_MSG);
	}

}
