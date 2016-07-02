package com.demidov.university.presentation.windows;

import java.lang.reflect.ParameterizedType;

import com.demidov.university.model.persistence.entity.Group;
import com.demidov.university.presentation.views.UpdatableUI;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Abstract class for edit dialog windows
 *
 * @param <T>
 */
public abstract class AbstractEditDialog<T> extends Window implements EditDialog<T> {

	protected static final String OK = "ОК";
	protected static final String CANCEL = "Отменить";
	protected static final String FIELD_WIDTH = "350px";
	
	protected final VerticalLayout content = new VerticalLayout();
	protected final VerticalLayout form = new VerticalLayout();
	protected HorizontalLayout buttonsLayout;
	
	private final Button btnOk = new Button(OK);
	private final Button btnCancel = new Button(CANCEL);
	
	protected final UpdatableUI<T> updatebaleUI;
	protected T currentEntity;
	protected BeanFieldGroup<T> binder;
	
	protected AbstractEditDialog(final UpdatableUI<T> updatebaleUI) {
		this.updatebaleUI = updatebaleUI;
		
		initWindowProps();
		initLayouts();
	    initStyles();
	    initListeners();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setEditingEntity(final T entity) {
		assert entity != null;
		
		this.currentEntity = entity;

        binder = new BeanFieldGroup<T>((Class<T>) ((ParameterizedType) getClass()
              .getGenericSuperclass()).getActualTypeArguments()[0]
             );
        binder.setItemDataSource(entity);
        binder.setBuffered(true);
	}
	
	protected void showErrorToUI(final Exception exception) {
		Notification.show(
				exception.getLocalizedMessage(),
				Notification.Type.WARNING_MESSAGE);
	}
	
	protected abstract void okClicked();
	
	protected void cancelClicked() {
		closeWindow();
	}
	
	protected void closeWindow() {
		close();
	}
	
	private void initWindowProps() {
		setSizeUndefined();
		center();
		setResizable(false);
		
		addCloseShortcut(KeyCode.ESCAPE, null);
	}
	
	private void initLayouts() {
		buttonsLayout = new HorizontalLayout(btnOk, btnCancel);
	    content.addComponents(form, buttonsLayout);
	    content.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
	    
	    setContent(content);
	}
	
	private void initStyles() {
		btnOk.setClickShortcut(KeyCode.ENTER);
	    
	    btnOk.setStyleName(ValoTheme.BUTTON_FRIENDLY);
	    btnCancel.setStyleName(ValoTheme.BUTTON_PRIMARY);
		
		buttonsLayout.setSpacing(true);
		form.setSpacing(true);
		
        content.setMargin(true);
        content.setSpacing(true);
	}
	
	private void initListeners() {
	    btnOk.addClickListener(e -> okClicked());
	    btnCancel.addClickListener(e -> cancelClicked());
	}
	
}
