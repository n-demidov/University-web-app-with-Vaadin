package com.demidov.university.presentation.windows;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.domain.GroupsDomain;
import com.demidov.university.model.domain.StudentsDomain;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.model.persistence.entity.Student;
import com.demidov.university.presentation.views.UpdatableUI;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class StudentEditDialog extends AbstractEditDialog<Student> {
	
	private static final String GROUP_FACULTY_NAME_PROPERTY = "facultyName";
	private static final String TITLE_ADD = "Добавить студента", TITLE_EDIT = "Редактировать студента";
	private static final String NAME = "Имя", LAST_NAME = "Фамилия", MIDDLE_NAME = "Отчество",
			BIRTH_DATE = "Дата рождения", GROUP = "Группа";
	
	private TextField name, lastName, middleName;
	private Field birthDate;
	private NativeSelect group;
	
	private final StudentsDomain studentsDomain;
	private final GroupsDomain groupsDomain;
	
	private static final Logger logger = Logger.getLogger(StudentEditDialog.class.getName());
	
	public StudentEditDialog(final UpdatableUI<Student> updatebaleUI) {
        super(updatebaleUI);
        
	    studentsDomain = StudentsDomain.getInstance();
	    groupsDomain = GroupsDomain.getInstance();
    }

	@Override
	public void setEditingEntity(final Student student) {
		super.setEditingEntity(student);
		
		updateFormFields();
		updateFormFieldsStyle();

		setDialogCaption(student);
	}
	
	@Override
	protected void okClicked() {
		try {
			binder.commit();
			
			studentsDomain.createOrUpdate(currentEntity);
			updatebaleUI.updateUI(currentEntity);
			closeWindow();
		} catch (final CommitException e) {
		} catch (final ValidException | PersistException e) {
			logger.log(Level.SEVERE, null, e);
			showErrorToUI(e);
		}
	}
	
	private void updateFormFields() {
		try {
			name = binder.buildAndBind(NAME, "name", TextField.class);
			lastName = binder.buildAndBind(LAST_NAME, "lastName", TextField.class);
			middleName = binder.buildAndBind(MIDDLE_NAME, "middleName", TextField.class);
			birthDate = binder.buildAndBind(BIRTH_DATE, "birthDate");
			
			// Create list of groups and bind it to 'student.group' property
			group = new NativeSelect(GROUP);
			group.setContainerDataSource(new BeanItemContainer<>(
					Group.class, groupsDomain.getAll()));
			group.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		    group.setItemCaptionPropertyId(GROUP_FACULTY_NAME_PROPERTY);
		    
		    binder.bind(group, "group");
		    
		    form.removeAllComponents();
			form.addComponents(name, lastName, middleName, birthDate, group);
			
			name.focus();
		} catch (final PersistException e) {
			logger.log(Level.SEVERE, null, e);
			showErrorToUI(e);
		}
	}
	
	private void updateFormFieldsStyle() {
		name.setWidth(FIELD_WIDTH);
	    lastName.setWidth(FIELD_WIDTH);
	    middleName.setWidth(FIELD_WIDTH);
	    birthDate.setWidth(FIELD_WIDTH);
	    group.setWidth(FIELD_WIDTH);
	    
	    name.setNullRepresentation("");
	    lastName.setNullRepresentation("");
	    middleName.setNullRepresentation("");
	}
	
	// Set dialog caption
	private void setDialogCaption(final Student student) {
		if (student.getId() == null) {
			setCaption(TITLE_ADD);
		} else {
			setCaption(TITLE_EDIT);
		}
	}
	
}
