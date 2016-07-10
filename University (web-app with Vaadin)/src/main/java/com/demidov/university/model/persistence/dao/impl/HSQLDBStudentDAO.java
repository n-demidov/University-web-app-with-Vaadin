package com.demidov.university.model.persistence.dao.impl;

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
import com.demidov.university.model.persistence.dao.filter.StudentFilterParams;
import com.demidov.university.model.persistence.dao.interfaces.StudentDao;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.model.persistence.entity.Student;

public class HSQLDBStudentDao extends GeneralJDBCDao<Student, Long> implements StudentDao{
	
	private static final String STUDENT_ID = "id", STUDENT_NAME = "name", STUDENT_LAST_NAME = "last_name",
			STUDENT_MIDDLE_NAME = "middle_name", STUDENT_BIRTH_DATE = "birth_date", STUDENT_GROUP_ID = "group_id";
	private static final String GROUP_NUMBER = "group_number", GROUP_FACULTY_NAME = "faculty_name";

	private static final String SELECT = "SELECT s.id, s.name, s.last_name, s.middle_name, s.birth_date, s.group_id"
			+ " , g.group_number, g.faculty_name"
			+ " FROM students s, groups g"
			+ " WHERE s.group_id = g.id";
	private static final String SELECT_ONE = SELECT + " WHERE s.id = ?";
	private static final String UPDATE = "UPDATE students SET"
			+ " name = ?, last_name = ?, middle_name = ?, birth_date = ?, group_id = ?"
			+ " WHERE id = ?";
	private static final String INSERT = "INSERT INTO students"
			+ " (name, last_name, middle_name, birth_date, group_id)"
			+ " VALUES(?, ?, ?, ?, ?)";
	private static final String DELETE = "DELETE FROM students WHERE id = ?";
	
	private static final String FILTER_BY_LASTNAME = " AND (LOWER(s.last_name) LIKE ?)";
	private static final String FILTER_BY_GROUP_NUMBER = " AND (g.group_number = ?)";
	private static final String ANY_CHARS = "%";

	private static HSQLDBStudentDao instance;
	private static final Logger logger = Logger.getLogger(HSQLDBStudentDao.class.getName());

	public static synchronized HSQLDBStudentDao getInstance() {
		if (instance == null)
			instance = new HSQLDBStudentDao();
		return instance;
	}

	private HSQLDBStudentDao() {
		super();
	}
	
	/**
	 * Flexible filter by student.lastname and student.group.number.
	 * If one of this is parameter specify as null - then no filtering require for this parameter.
	 * @throws PersistException 
	 */
	@Override
	public List<Student> filter(final StudentFilterParams filterParams) throws PersistException {
		List<Student> students = new ArrayList<>();
		
		ResultSet rs = null;
        int i = 1;
        
        // Forming SQL query
        String sql = SELECT;
        boolean isByLastname = false;
        boolean isByGroupNumber = false;

        if (filterParams != null) {
        	if (filterParams.getLastName() != null && !filterParams.getLastName().trim().isEmpty())
            {
            	isByLastname = true;
                sql += FILTER_BY_LASTNAME;
            }
            if (filterParams.getGroupNumber() != null)
            {
            	isByGroupNumber = true;
                sql += FILTER_BY_GROUP_NUMBER;
            }
        }
        
        // Execute SQL query
        try (final PreparedStatement statement = connection.prepareStatement(sql))
        {
        	// Fill SQL query with parameters
            if (isByLastname)
            {
                final String in = ANY_CHARS
                		+ filterParams.getLastName().trim().toLowerCase().replace(" ", ANY_CHARS)
                		+ ANY_CHARS;
                statement.setString(i++, in);
            }
            if (isByGroupNumber)
            {
                statement.setInt(i++, filterParams.getGroupNumber());
            }

            // Execute SQL query
            rs = statement.executeQuery();
            
            students = parseResultSet(rs);
        } catch (final SQLException e) {
        	processSQLException(e);
		} finally
        {
            closeResultSet(rs);
        }
		
		return students;
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
	 * Read and create instance of entity from ResultSet object
	 */
	@Override
	protected List<Student> parseResultSet(final ResultSet rs) throws SQLException {
		final List<Student> students = new ArrayList<>();

		while (rs.next()) {
			final Group group = new Group();
			group.setId(rs.getLong(STUDENT_GROUP_ID));
			group.setNumber(rs.getInt(GROUP_NUMBER));
			group.setFacultyName(rs.getString(GROUP_FACULTY_NAME));
			
			final Student student = new Student();
			student.setId(rs.getLong(STUDENT_ID));
			student.setName(rs.getString(STUDENT_NAME));
			student.setLastName(rs.getString(STUDENT_LAST_NAME));
			student.setMiddleName(rs.getString(STUDENT_MIDDLE_NAME));
			student.setBirthDate(rs.getDate(STUDENT_BIRTH_DATE));
			student.setGroup(group);

			students.add(student);
		}

		return students;
	}
	
	@Override
	protected void prepareStatementForCreate(final PreparedStatement statement,
			final Student student) throws SQLException {
		fillQuery(statement, student);
	}
	
	@Override
	protected void prepareStatementForUpdate(final PreparedStatement statement,
			final Student student) throws SQLException {
		final int i = fillQuery(statement, student);
		
		statement.setLong(i, student.getId());
	}
	
	// Fill PreparedStatement by parameters of entity
	private int fillQuery(final PreparedStatement statement,
			final Student student) throws SQLException {
		int i = 1;
		
		statement.setString(i++, student.getName());
		statement.setString(i++, student.getLastName());
		statement.setString(i++, student.getMiddleName());
		statement.setDate(i++, convertDate(student.getBirthDate()));
		statement.setLong(i++, student.getGroup().getId());
		
		return i;
	}

}
