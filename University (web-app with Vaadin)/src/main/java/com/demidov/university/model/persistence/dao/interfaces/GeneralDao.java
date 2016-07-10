package com.demidov.university.model.persistence.dao.interfaces;

import java.io.Serializable;
import java.util.List;

import com.demidov.university.model.exceptions.persistence.NoSuchPersistedEntityException;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;

public interface GeneralDao<T, PK extends Serializable> {
	
	public List<T> getAll() throws PersistException;
	
	public T get(final PK pk) throws PersistException, NoSuchPersistedEntityException;

	public void update(final T entity) throws PersistException, ValidException;
	
	public void create(final T entity) throws PersistException, ValidException;
	
	public void delete(final PK pk) throws PersistException;
	
	public void createOrUpdate(final T entity) throws PersistException, ValidException;

}
