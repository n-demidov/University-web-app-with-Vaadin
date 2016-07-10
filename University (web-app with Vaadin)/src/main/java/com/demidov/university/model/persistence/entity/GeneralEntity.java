package com.demidov.university.model.persistence.entity;

import java.io.Serializable;

public abstract class GeneralEntity<PK extends Serializable> {

	/**
	 * Return entity's primary key
	 */
	public abstract PK getPk();
	
}
