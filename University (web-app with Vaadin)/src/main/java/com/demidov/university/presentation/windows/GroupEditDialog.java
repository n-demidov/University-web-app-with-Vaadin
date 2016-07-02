package com.demidov.university.presentation.windows;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.domain.GroupsDomain;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.exceptions.persistence.ValidException;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.presentation.views.UpdatableUI;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.TextField;

public class GroupEditDialog extends AbstractEditDialog<Group> {

	private static final String TITLE_ADD = "Добавить группу", TITLE_EDIT = "Редактировать группу";
	private static final String NUMBER = "Номер группы", FACULTY_NAME = "Название факультета";
	
	private TextField number;
	private TextField facultyName;
	
	private final GroupsDomain groupDomain;
	
	private static final Logger logger = Logger.getLogger(GroupEditDialog.class.getName());
	
	public GroupEditDialog(final UpdatableUI<Group> updatebaleUI) {
        super(updatebaleUI);
        
	    this.groupDomain = GroupsDomain.getInstance();
    }

	@Override
	public void setEditingEntity(final Group group) {
		super.setEditingEntity(group);

		updateFormFields();
		updateFormFieldsStyle();

		setDialogCaption(group);
	}

	@Override
	protected void okClicked() {
		try {
			binder.commit();
			
			groupDomain.createOrUpdate(currentEntity);
			updatebaleUI.updateUI(currentEntity);
			closeWindow();
		} catch (final CommitException e) {
		} catch (final ValidException | PersistException e) {
			logger.log(Level.SEVERE, null, e);
			showErrorToUI(e);
		}
	}
	
	private void updateFormFields() {
		number = binder.buildAndBind(NUMBER, "number", TextField.class);
		facultyName = binder.buildAndBind(FACULTY_NAME, "facultyName", TextField.class);
		
		form.removeAllComponents();
		form.addComponents(number, facultyName);
		
		number.focus();
	}
	
	private void updateFormFieldsStyle() {
	    number.setWidth(FIELD_WIDTH);
	    facultyName.setWidth(FIELD_WIDTH);
	    
	    number.setNullRepresentation("");
	    facultyName.setNullRepresentation("");
	}

	// Set dialog caption
	private void setDialogCaption(final Group group) {
		if (group.getId() == null) {
			setCaption(TITLE_ADD);
		} else {
			setCaption(TITLE_EDIT);
		}
	}

}
