package com.demidov.university.model.exceptions.persistence;

public class NoSuchPersistedEntityException extends PersistException {

	public NoSuchPersistedEntityException(final String message) {
		super(message);
	}

}
