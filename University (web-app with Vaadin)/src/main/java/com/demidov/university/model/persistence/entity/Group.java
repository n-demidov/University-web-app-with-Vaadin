package com.demidov.university.model.persistence.entity;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Group implements Serializable, Cloneable {

	private Long id;

	@NotNull(message="Поле должно быть задано")
	@Min(value=1, message="Поле должно быть не меньше 1")
	private int number;

	@NotNull(message="Поле должно быть задано")
	@Size(min = 1, max = 255, message="Поле должно быть от 1 до 255 символов")
	private String facultyName;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(final String facultyName) {
		this.facultyName = facultyName;
	}

	public String getCaption() {
		return facultyName;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", number=" + number + ", facultyName=" + facultyName + "]";
	}

	@Override
	public Group clone() throws CloneNotSupportedException {
		return (Group) super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
