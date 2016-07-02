package com.demidov.university.presentation.views;

import com.demidov.university.presentation.windows.AbstractEditDialog;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public abstract class AbstractView<T> extends VerticalLayout implements View, UpdatableUI<T> {

	protected static final String PAGE_TITLE = "Abstract view";
	private static final String ADD = "Добавить";
	private static final String CHANGE = "Изменить";
	private static final String DELETE = "Удалить";

	protected final HorizontalLayout actionButtons = new HorizontalLayout();
	protected final Grid gridObjects = new Grid();
	private final Button btnAdd = new Button(ADD);
	private final Button btnChange = new Button(CHANGE);
	private final Button btnDelete = new Button(DELETE);

	protected AbstractEditDialog<T> editDialogWindow;

	public AbstractView() {
		super();
		initStyles();
		initLayout();
		initListeners();
	}

	@Override
	public void enter(final ViewChangeEvent event) {
	}
	
	protected void addClicked() {
		gridObjects.select(null);
	}

	protected void changeSelectedClicked() {
		final T object = getSelectedObject();
		if (object != null) {
			showGroupEditDialog(object);
		}
	}

	protected abstract void deleteSelectedClicked();

	// Called when group selected in the grid
	private void objectSelected(final SelectionEvent event) {
		final boolean isSelected = !event.getSelected().isEmpty();
		setActionButtonsEnabled(isSelected);
	}

	// Return selected object in the grid
	protected <T> T getSelectedObject() {
		return (T) gridObjects.getSelectedRow();
	}

	// Show edit group dialog
	protected void showGroupEditDialog(final T object) {
		editDialogWindow.setEditingEntity(object);
		UI.getCurrent().addWindow(editDialogWindow);
	}
	
	protected void showErrorToUI(final Exception exception) {
		Notification.show(
				exception.getLocalizedMessage(),
				Notification.Type.WARNING_MESSAGE);
	}

	private void initLayout() {
		actionButtons.setSpacing(true);
		actionButtons.setMargin(new MarginInfo(false, false, true, false));
		actionButtons.addComponents(btnAdd, btnChange, btnDelete);
	}

	private void initStyles() {
		Page.getCurrent().setTitle(PAGE_TITLE);
		setSizeFull();

		gridObjects.setSizeFull();

		setActionButtonsStyle();
		setActionButtonsEnabled(false);
	}

	private void setActionButtonsStyle() {
		btnAdd.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnChange.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnDelete.setStyleName(ValoTheme.BUTTON_DANGER);
	}

	// Set 'enable' property for action buttons
	private void setActionButtonsEnabled(final boolean isEnable) {
		btnChange.setEnabled(isEnable);
		btnDelete.setEnabled(isEnable);
	}

	private void initListeners() {
		btnAdd.addClickListener(e -> addClicked());
		btnChange.addClickListener(e -> changeSelectedClicked());
		btnDelete.addClickListener(e -> deleteSelectedClicked());
		gridObjects.addSelectionListener(e -> objectSelected(e));
	}

}
