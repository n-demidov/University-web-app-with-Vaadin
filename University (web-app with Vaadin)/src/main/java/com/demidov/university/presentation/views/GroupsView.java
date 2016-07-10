package com.demidov.university.presentation.views;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.domain.GroupsDomain;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.presentation.windows.GroupEditDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;

public class GroupsView extends AbstractView<Group> {
	
	private static final int GRID_ROWS = 14;
	private static final String FACULTY_NAME_HEADER = "Название факультета";
	private static final String NUMBER_HEADER = "Номер группы";
	private static final String FACULTY_NAME_COLUMN = "facultyName";
	private static final String NUMBER_COLUMN = "number";

	private static final String PAGE_TITLE = "Группы - Institute";
	
	private GroupsDomain groupsDomain;
	private static final Logger logger = Logger.getLogger(GroupsView.class.getName());
	
    public GroupsView() {
		super();
		
		initFields();
		initLayouts();
	}

	@Override
    public void enter(final ViewChangeEvent event) {
		super.enter(event);
		Page.getCurrent().setTitle(PAGE_TITLE);
		
		updateList();
    }
	
	/**
 	 * Called when object was changed in the dialog window.
 	 * Updates UI.
 	 */
 	@Override
	public void updateUI(final Group object) {
 		updateList();
 		try {
 			gridObjects.select(object);
 		} catch (final IllegalArgumentException e) {}
	}
    
 	@Override
 	protected void addClicked() {
 		super.addClicked();
 		showGroupEditDialog(new Group());
	}
 	
 	@Override
 	protected void deleteSelectedClicked() {
 		try {
 			final Group group = getSelectedObject();
 			if (group != null) {
 				groupsDomain.delete(group.getId());
 				updateList();
 			}
		} catch (final PersistException e) {
			showErrorToUI(e);
		}
 	}
 	
 	/**
     * Fetch list of groups from and assign it to grid
     */
 	private void updateList() {
 		final List<Group> groups;
 		try {
 			groups = groupsDomain.getAll();
 			gridObjects.setContainerDataSource(new BeanItemContainer<>(Group.class, groups));
 		} catch (final PersistException e) {
 			logger.log(Level.SEVERE, null, e);
 			showErrorToUI(e);
 		}
 	}
 	
 	private void initFields() {
		groupsDomain = GroupsDomain.getInstance();
		editDialogWindow = new GroupEditDialog(this);
	}

 	private void initLayouts() {
 		editDialogWindow.setModal(true);
 		
 		gridObjects.setColumns(NUMBER_COLUMN, FACULTY_NAME_COLUMN);
 		gridObjects.setHeightMode(HeightMode.ROW);
		gridObjects.setHeightByRows(GRID_ROWS);
 		
 		setColumnHeaders();
 		
 		addComponents(actionButtons, gridObjects);
 	}
 	
 	private void setColumnHeaders() {
		gridObjects.getColumn(NUMBER_COLUMN).setHeaderCaption(NUMBER_HEADER);
		gridObjects.getColumn(FACULTY_NAME_COLUMN).setHeaderCaption(FACULTY_NAME_HEADER);
	}
 	
}
