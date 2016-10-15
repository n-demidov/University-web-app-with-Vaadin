package com.demidov.university.model.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Student extends GeneralEntity<Long> implements Serializable, Cloneable {

	private Long id;

	@NotEmpty(message = "{common.set.error}")
	@Size(min = 2, max = 60, message = "{common.size.error}")
	private String name;

	@NotEmpty(message = "{common.set.error}")
	@Size(min = 2, max = 70, message = "{common.size.error}")
	private String lastName;

	@NotEmpty(message = "{common.set.error}")
	@Size(min = 2, max = 60, message = "{common.size.error}")
	private String middleName;

	@NotNull(message = "{common.set.error}")
	private Date birthDate;

	@NotNull(message = "{common.set.error}")
	private Group group;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final Date birthDate) {
		this.birthDate = birthDate;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(final Group group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", lastName=" + lastName + ", middleName=" + middleName + ", birthDate=" + birthDate + ", group=" + group + "]";
	}

	@Override
	public Student clone() throws CloneNotSupportedException {
		return (Student) super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Student)) {
			return false;
		}
		final Student other = (Student) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public Long getPk() {
		return getId();
	}

}
