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
import com.demidov.university.model.persistence.dao.interfaces.GroupDao;
import com.demidov.university.model.persistence.entity.Group;

public class HSQLDBGroupDao extends GeneralJDBCDao<Group, Long> implements GroupDao {

	private static final String STUDENTS_TABLE = "students";
	private static final String GROUP_ID = "id", GROUP_NUMBER = "group_number", GROUP_FACULTY_NAME = "faculty_name";
	private static final String DELETE_GROUP_WHEN_STUDENTS_REFER_ERR_MSG = "Группа не может быть удалена, так как на неё ссылается 1 или более студентов";
	
	private static final String SELECT = "SELECT g.id, g.group_number, g.faculty_name FROM groups g";
	private static final String SELECT_ONE = SELECT + " WHERE g.id = ?";
	private static final String INSERT = "INSERT INTO groups (group_number, faculty_name) VALUES(?, ?)";
	private static final String UPDATE = "UPDATE groups SET group_number = ?, faculty_name = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM groups WHERE id = ?";

	private static HSQLDBGroupDao instance;
	private static final Logger logger = Logger.getLogger(HSQLDBGroupDao.class.getName());

	public static synchronized HSQLDBGroupDao getInstance() {
		if (instance == null)
			instance = new HSQLDBGroupDao();
		return instance;
	}

	private HSQLDBGroupDao() {
		super();
	}

	@Override
	protected List<Group> parseResultSet(final ResultSet rs) throws SQLException {
		final List<Group> groups = new ArrayList<>();

		while (rs.next()) {
			final Group group = new Group();

			group.setId(rs.getLong(GROUP_ID));
			group.setNumber(rs.getInt(GROUP_NUMBER));
			group.setFacultyName(rs.getString(GROUP_FACULTY_NAME));

			groups.add(group);
		}

		return groups;
	}
	
	@Override
	protected String getSelectQuery() {
		return SELECT;
	}

	@Override
	protected String getSelectOneQuery() {
		return SELECT_ONE;
	}

	@Override
	protected String getCreateQuery() {
		return INSERT;
	}

	@Override
	protected String getUpdateQuery() {
		return UPDATE;
	}

	@Override
	protected String getDeleteQuery() {
		return DELETE;
	}
	
	/**
	 * Handle exception on delete method
	 */
	@Override
	protected void processDeleteException(final SQLException e) throws PersistException {
		// If one or more students refer to group - show readable message. Else - show standard message.
		final String exc = e.getLocalizedMessage().toLowerCase();
		
		if (isConstraintViolation(e)) {
			if (exc.contains(STUDENTS_TABLE.toLowerCase())) {
				throw new PersistException(DELETE_GROUP_WHEN_STUDENTS_REFER_ERR_MSG);
			}
	    }
		
		processSQLException(e);
	}
	
	@Override
	protected void prepareStatementForCreate(final PreparedStatement statement,
			final Group group) throws SQLException {
		prepareStatement(statement, group);
	}
	
	@Override
	protected void prepareStatementForUpdate(final PreparedStatement statement,
			final Group group) throws SQLException {
		final int i = prepareStatement(statement, group);
		
		statement.setLong(i, group.getId());
	}
	
	// Fill PreparedStatement by parameters of entity
	private int prepareStatement(final PreparedStatement statement,
			final Group group) throws SQLException {
		int i = 1;
		
		statement.setInt(i++, group.getNumber());
		statement.setString(i++, group.getFacultyName());
		
		return i;
	}
	
}
