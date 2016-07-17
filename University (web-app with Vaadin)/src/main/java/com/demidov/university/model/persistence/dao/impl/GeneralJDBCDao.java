package com.demidov.university.model.persistence.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.exceptions.persistence.NoSuchPersistedEntityException;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.connection.DBConnection;
import com.demidov.university.model.persistence.dao.interfaces.GeneralDao;
import com.demidov.university.model.persistence.dao.interfaces.GroupDao;
import com.demidov.university.model.persistence.entity.GeneralEntity;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.model.persistence.entity.Student;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class GeneralJDBCDao<T extends GeneralEntity<PK>, PK extends Serializable>
		implements GeneralDao<T, PK> {

	private static final String RECEIVED_MORE_THAN_ONE_RECORD_ERR = "Received more than one record";
	private static final String ENTITY_IS_ALREADY_PERSIST = "Entity is already persist";
	private static final String NO_SUCH_ENTITY_IN_DB = "There isn't such entity with id = %d";
	private static final String PERSIST_MORE_THEN_1_RECORD_ERR = "On persist modify more then 1 record: %d",
			DELETE_MORE_THEN_1_RECORD_ERR = "On delete modify more then 1 record: %d",
			UPDATE_MORE_THEN_1_RECORD_ERR = "On update modify more then 1 record: %d";
	private static final String INTEGRITY_CONSTRAINT_VIOLATION_CODE = "23";
	private static final String PEPRSIST_ERR_MSG = "Возникла ошибка при обращении к базе данных. Пожалуйста, проверьте входные параметры.";
	
	protected Connection connection;

	private static final ValidatorFactory validationFactory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = validationFactory.getValidator();

	private static final Logger logger = Logger.getLogger(GeneralJDBCDao.class.getName());

	public GeneralJDBCDao() {
		try {
			connection = DBConnection.getInstance().getConnection();
		} catch (final SQLException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	/**
	 * Find all entities
	 * @param sqlReadAll
	 * @return
	 * @throws PersistException
	 */
	@Override
	public List<T> getAll() throws PersistException {
		List<T> entities = null;
        final String sql = getSelectQuery();
        
        try (final PreparedStatement statement = connection.prepareStatement(sql);
        		final ResultSet rs = statement.executeQuery()) {
        	return parseResultSet(rs);
        } catch (final SQLException e) {
        	processSQLException(e);
        }
        return entities;
	}
	
	/**
	 * Find entity with such pk in DB
	 * @param pk
	 * @return
	 * @throws NoSuchPersistedEntityException
	 * @throws PersistException
	 */
	@Override
	public T get(final PK pk) throws NoSuchPersistedEntityException, PersistException {
		assert pk != null;
		
		ResultSet rs = null;
		List<T> entities = null;
        final String sql = getSelectOneQuery();
        
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, pk);
            rs = statement.executeQuery();
            
            entities = parseResultSet(rs);
        } catch (final SQLException e) {
        	processSQLException(e);
        } finally {
			closeResultSet(rs);
		}
        
        if (entities == null || entities.size() == 0) {
        	throw new NoSuchPersistedEntityException(String.format(NO_SUCH_ENTITY_IN_DB, pk));
        }
        if (entities.size() > 1) {
        	logger.log(Level.WARNING, "get RECEIVED_MORE_THAN_ONE_RECORD_ERR pk=", pk);
        	throw new PersistException(RECEIVED_MORE_THAN_ONE_RECORD_ERR);
        }
        return entities.iterator().next();
	}
	
	/**
	 * Create (if id == null) or update (if id != null)
	 */
	@Override
	public void createOrUpdate(final T entity) throws ValidException, PersistException {
		if (entity.getPk() == null) {
			create(entity);
		} else {
			update(entity);
		}
	}
	
	/**
	 * Create new record.
	 * @param group
	 * @throws PersistException 
	 * @throws SQLException
	 * @throws edu.library.exceptions.ValidationException
	 */
	@Override
	public void create(final T entity) throws PersistException, ValidException {
		validate(entity);
		
	    if (entity.getPk() != null) {
	        throw new PersistException(ENTITY_IS_ALREADY_PERSIST);
	    }
		
	    final String sql = getCreateQuery();
	    
	    try (final PreparedStatement statement = connection.prepareStatement(sql)) {
	        prepareStatementForCreate(statement, entity);
	        
	        int count = statement.executeUpdate();
	        if (count != 1) {
	            throw new PersistException(
	            		String.format(PERSIST_MORE_THEN_1_RECORD_ERR, count));
	        }
	    } catch (final SQLException e) {
	    	processSQLException(e);
	    }
	}
	
	
	/**
	 * Update entity
	 */
	@Override
	public void update(T entity) throws PersistException, ValidException {
		validate(entity);
		
	    final String sql = getUpdateQuery();
	    try (final PreparedStatement statement = connection.prepareStatement(sql);) {
	        prepareStatementForUpdate(statement, entity);
	        
	        final int count = statement.executeUpdate();
	        if (count != 1) {
	            throw new PersistException(
	            		String.format(UPDATE_MORE_THEN_1_RECORD_ERR, count));
	        }
	    } catch (final SQLException e) {
	    	processSQLException(e);
	    }
	}
	
	/**
	 * The generic method to delete entity by pk from DB
	 */
	@Override
	public void delete(final PK pk) throws PersistException {
		assert pk != null;
	    final String sql = getDeleteQuery();
	    
	    try (final PreparedStatement statement = connection.prepareStatement(sql)) {
	    	statement.setObject(1, pk);

	        final int count = statement.executeUpdate();
	        if (count != 1) {
	            throw new PersistException(
	            		String.format(DELETE_MORE_THEN_1_RECORD_ERR, count));
	        }
	        statement.close();
	    } catch (final SQLException e) {
	    	processDeleteException(e);
	    }
	}
	
	protected abstract String getSelectQuery();
	
	protected abstract String getSelectOneQuery();
	
	protected abstract String getCreateQuery();
	
	protected abstract String getUpdateQuery();
	
	protected abstract String getDeleteQuery();
	
	/**
	 * Parse ResultSet and return the list of entities corresponding to ResultSet's content.
	 */
	protected abstract List<T> parseResultSet(final ResultSet rs) throws SQLException;
	
	/**
	 * Fill the PreparedStatement object by parameters of entity for create query
	 * @throws SQLException
	 */
	protected abstract void prepareStatementForCreate(final PreparedStatement statement,
			final T entity) throws SQLException;
	
	/**
	 * Fill the PreparedStatement object by parameters of entity for update query
	 * @throws SQLException
	 */
	protected abstract void prepareStatementForUpdate(final PreparedStatement statement,
			final T entity) throws SQLException;
	
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
	 * Invoke on SQLException when trying to remove.
	 * @param e
	 * @throws PersistException
	 */
	protected void processDeleteException(final SQLException e) throws PersistException {
		processSQLException(e);
	}
	
	/**
	 * Return true if SQLException is an Integrity Constraint Violation Exception
	 * @param e
	 * @return
	 */
	protected static boolean isConstraintViolation(final SQLException e) {
	    return e.getSQLState().startsWith(INTEGRITY_CONSTRAINT_VIOLATION_CODE);
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
	 * Convert java.util.Date to java.sql.Date
	 * @param date
	 * @return
	 */
	protected java.sql.Date convertDate(final java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
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
	
}
