package com.demidov.university.model.persistence.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.demidov.university.model.exceptions.persistence.NoSuchPersistedEntityException;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.dao.interfaces.GroupDAO;
import com.demidov.university.model.persistence.entity.Group;

public class HSQLDBGroupDAO extends AbstractJDBCDAO implements GroupDAO {

	private static final String STUDENTS_TABLE = "students";
	private static final String GROUP_ID = "id", GROUP_NUMBER = "group_number", GROUP_FACULTY_NAME = "faculty_name";
	private static final String DELETE_GROUP_WHEN_STUDENTS_REFER_ERR_MSG = "Группа не может быть удалена, так как на неё ссылается 1 или более студентов";
	
	private static final String SELECT = "SELECT g.id, g.group_number, g.faculty_name FROM groups g";
	private static final String SELECT_ONE = SELECT + " WHERE g.id = ?";
	private static final String UPDATE = "UPDATE groups SET group_number = ?, faculty_name = ? WHERE id = ?";
	private static final String INSERT = "INSERT INTO groups (group_number, faculty_name) VALUES(?, ?)";
	private static final String DELETE = "DELETE FROM groups WHERE id = ?";

	private static final String NO_SUCH_ENTITY_IN_DB = "В базе данных нет группы с id = %d";

	private static HSQLDBGroupDAO instance;
	private static final Logger logger = Logger.getLogger(HSQLDBGroupDAO.class.getName());

	public static synchronized HSQLDBGroupDAO getInstance() {
		if (instance == null)
			instance = new HSQLDBGroupDAO();
		return instance;
	}

	private HSQLDBGroupDAO() {
		super();
	}

	/**
	 * Find all objects
	 * @return
	 * @throws PersistException 
	 * @throws SQLException
	 */
	@Override
	public List<Group> getAll() throws PersistException {
		final List<Group> Groups = new ArrayList<>();

		try (final PreparedStatement statement = connection.prepareStatement(SELECT);
				final ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				Groups.add(readResultSet(rs));
			}
		} catch (final SQLException e) {
			processSQLException(e);
		}

		return Groups;
	}

	/**
	 * Find object with such id in DB
	 * @param id
	 * @return
	 * @throws PersistException 
	 * @throws java.sql.SQLException
	 * @throws NoSuchPersistedEntityException.library.exceptions.db.NoSuchEntityInDB
	 */
	@Override
	public Group get(final long id) throws NoSuchPersistedEntityException, PersistException {
		ResultSet rs = null;

		try (final PreparedStatement statement = connection.prepareStatement(SELECT_ONE)) {
			statement.setLong(1, id);

			rs = statement.executeQuery();

			if (rs.next()) {
				return readResultSet(rs);
			} else {
				throw new NoSuchPersistedEntityException(String.format(NO_SUCH_ENTITY_IN_DB, id));
			}
		} catch (final SQLException e) {
			processSQLException(e);
		} finally {
			closeResultSet(rs);
		}
		
		throw new PersistException(UNEXPECTED_POINT_EXECUTION);
	}

	/**
	 * Update state of object
	 * @param group
	 * @throws PersistException 
	 * @throws java.sql.SQLException
	 * @throws edu.library.exceptions.ValidationException
	 */
	@Override
	public void update(final Group group) throws ValidException, PersistException {
		validate(group);

		try (final PreparedStatement statement = connection.prepareStatement(UPDATE)) {
			statement.setInt(1, group.getNumber());
			statement.setString(2, group.getFacultyName());
			statement.setLong(3, group.getId());

			statement.executeUpdate();
		} catch (final SQLException e) {
			processSQLException(e);
		}
	}

	/**
	 * Create new record. Also change primary key of object.
	 * @param group
	 * @throws PersistException 
	 * @throws SQLException
	 * @throws edu.library.exceptions.ValidationException
	 */
	@Override
	public void create(final Group group) throws ValidException, PersistException {
		validate(group);

		try (final PreparedStatement statement = connection.prepareStatement(INSERT)) {
			statement.setInt(1, group.getNumber());
			statement.setString(2, group.getFacultyName());

			statement.executeUpdate();
		} catch (final SQLException e) {
			processSQLException(e);
		}
	}
	
	/**
	 * Create (if id == null) or update (if id != null)
	 * @param group
	 * @throws ValidException
	 * @throws PersistException 
	 * @throws SQLException
	 */
	@Override
	public void createOrUpdate(final Group group) throws ValidException, PersistException {
		if (group.getId() == null) {
			create(group);
		} else {
			update(group);
		}
	}

	/**
	 * Delete record about object from DB
	 * @param id
	 * @throws PersistException 
	 * @throws java.sql.SQLException
	 */
	@Override
	public void delete(final long id) throws PersistException {
		try (final PreparedStatement statement = connection.prepareStatement(DELETE)) {
			statement.setLong(1, id);
			statement.execute();
		} catch (final SQLException e) {
			processDeleteException(e);
		}
	}
	
	@Override
	protected void processSQLException(final SQLException e) throws PersistException {
		super.processSQLException(e);
	}

	// Read and create instance of object from ResultSet object
	private Group readResultSet(final ResultSet rs) throws SQLException {
		final Group group = new Group();

		group.setId(rs.getLong(GROUP_ID));
		group.setNumber(rs.getInt(GROUP_NUMBER));
		group.setFacultyName(rs.getString(GROUP_FACULTY_NAME));

		return group;
	}
	
	// Handle exception on delete method
	private void processDeleteException(final SQLException e) throws PersistException {
		// If one or more students refer to group - show readable message. Else - show standard message.
		final String exc = e.getLocalizedMessage().toLowerCase();
		
		if (isConstraintViolation(e)) {
			if (exc.contains(STUDENTS_TABLE.toLowerCase())) {
				throw new PersistException(DELETE_GROUP_WHEN_STUDENTS_REFER_ERR_MSG);
			}
	    }
		
		processSQLException(e);
	}
	
}
