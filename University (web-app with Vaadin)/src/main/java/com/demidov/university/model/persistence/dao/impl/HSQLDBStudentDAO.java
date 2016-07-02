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
import com.demidov.university.model.persistence.dao.interfaces.StudentDAO;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.model.persistence.entity.Student;

public class HSQLDBStudentDAO extends AbstractJDBCDAO implements StudentDAO{
	
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
	
	private static final String NO_SUCH_ENTITY_IN_DB = "В базе данных нет студента с id = %d";

	private static HSQLDBStudentDAO instance;
	private static final Logger logger = Logger.getLogger(HSQLDBStudentDAO.class.getName());

	public static synchronized HSQLDBStudentDAO getInstance() {
		if (instance == null)
			instance = new HSQLDBStudentDAO();
		return instance;
	}

	private HSQLDBStudentDAO() {
		super();
	}

	/**
	 * Find all objects
	 * @return
	 * @throws SQLException
	 */
	@Override
	public List<Student> getAll() throws PersistException {
		final List<Student> result = new ArrayList<>();

		try (final PreparedStatement statement = connection.prepareStatement(SELECT);
			 final ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				result.add(readResultSet(rs));
			}
		} catch (final SQLException e) {
			processSQLException(e);
		}

		return result;
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
	public Student get(final long id) throws NoSuchPersistedEntityException, PersistException {
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
	 * Flexible filter by student.lastname and student.group.number.
	 * If one of this is parameter specify as null - then no filtering require for this parameter.
	 * @throws PersistException 
	 */
	@Override
	public List<Student> filter(final StudentFilterParams filterParams) throws PersistException {
		final List<Student> result = new ArrayList<>();
		
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
            while (rs.next())
            {
            	result.add(readResultSet(rs));
            }
        } catch (final SQLException e) {
        	processSQLException(e);
		} finally
        {
            closeResultSet(rs);
        }
		
		return result;
	}

	/**
	 * Update state of object
	 * @param student
	 * @throws PersistException 
	 * @throws java.sql.SQLException
	 * @throws edu.library.exceptions.ValidationException
	 */
	public void update(final Student student) throws ValidException, PersistException {
		 validate(student);
		
		try (final PreparedStatement statement = connection.prepareStatement(UPDATE)) {
			statement.setString(1, student.getName());
			statement.setString(2, student.getLastName());
			statement.setString(3, student.getMiddleName());
			statement.setDate(4, new java.sql.Date(student.getBirthDate().getTime()));
			statement.setLong(5, student.getGroup().getId());
			
			statement.setLong(6, student.getId());

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
	public void create(final Student student) throws ValidException, PersistException {
		 validate(student);

		try (final PreparedStatement statement = connection.prepareStatement(INSERT)) {
			statement.setString(1, student.getName());
			statement.setString(2, student.getLastName());
			statement.setString(3, student.getMiddleName());
			statement.setDate(4, new java.sql.Date(student.getBirthDate().getTime()));
			statement.setLong(5, student.getGroup().getId());

			statement.executeUpdate();
		} catch (final SQLException e) {
			processSQLException(e);
		}
	}
	
	/**
	 * Create (if id == null) or update (if id != null)
	 * @param student
	 * @throws ValidException
	 * @throws PersistException 
	 * @throws SQLException
	 */
	@Override
	public void createOrUpdate(final Student student) throws ValidException, PersistException {
		if (student.getId() == null) {
			create(student);
		} else {
			update(student);
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
			processSQLException(e);
		}
	}
	
	@Override
	protected void processSQLException(final SQLException e) throws PersistException {
		super.processSQLException(e);
	}

	// Read and create instance of object from ResultSet object
	private Student readResultSet(final ResultSet rs) throws SQLException {
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

		return student;
	}

}
