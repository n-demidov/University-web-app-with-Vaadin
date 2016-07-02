package com.demidov.university.model.persistence.dao.interfaces;

import java.util.List;

import com.demidov.university.model.exceptions.persistence.NoSuchPersistedEntityException;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;

public interface GeneralDAO<T> {
	
	public List<T> getAll() throws PersistException;
	
	public T get(final long id) throws PersistException, NoSuchPersistedEntityException;

	public void update(final T entity) throws PersistException, ValidException;
	
	public void create(final T entity) throws PersistException, ValidException;
	
	public void delete(final long id) throws PersistException;
	
	public void createOrUpdate(final T entity) throws ValidException, PersistException;

}
