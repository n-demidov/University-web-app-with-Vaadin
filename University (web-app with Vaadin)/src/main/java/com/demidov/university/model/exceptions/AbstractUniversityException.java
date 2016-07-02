package com.demidov.university.model.exceptions;

public class AbstractUniversityException extends Exception {

	public AbstractUniversityException()
    {
        super();
    }

    public AbstractUniversityException(final String message)
    {
        super(message);
    }
    
    public AbstractUniversityException(final Throwable cause)
    {
        super(cause);
    }
	
}
