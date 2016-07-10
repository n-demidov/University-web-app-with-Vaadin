package com.demidov.university.presentation.views;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demidov.university.model.domain.StudentsDomain;
import com.demidov.university.model.exceptions.persistence.PersistException;
import com.demidov.university.model.parser.NumberParser;
import com.demidov.university.model.persistence.dao.filter.StudentFilterParams;
import com.demidov.university.model.persistence.entity.Student;
import com.demidov.university.presentation.windows.StudentEditDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class StudentsView extends AbstractView<Student> {

	private static final int GRID_ROWS = 12;
	private static final String FACULTY_NAME_HEADER = "Название факультета";
	private static final String GROUP_NUMBER_HEADER = "Номер группы";
	private static final String BIRTH_DATE_HEADER = "Дата рождения";
	private static final String MIDDLENAME_HEADER = "Отчество";
	private static final String LASTNAME_HEADER = "Фамилия";
	private static final String NAME_HEADER = "Имя";
	private static final String BIRTH_DATE_COLUMN = "birthDate";
	private static final String MIDDLENAME_COLUMN = "middleName";
	private static final String LASTNAME_COLUMN = "lastName";
	private static final String NAME_COLUMN = "name";
	private static final String FACULTY_NAME_COLUMN = "group.facultyName";
	private static final String GROUP_NUMBER_COLUMN = "group.number";

	private static final String PAGE_TITLE = "Студенты - Institute";
	private static final String FILTER_BY_LASTNAME = "Поиск по фамилии...",
			FILTER_BY_GROUP_NAME = "Поиск по номеру группы...";
	private static final String CLEAR_FILTER = "Очистить параметры поиска";
	private static final String FILTER_GROUP_ERR = "Неверный формат номера группы";
	private static final Integer FILTER_GROUP_NUMBER_MIN = 1, FILTER_GROUP_NUMBER_MAX = Integer.MAX_VALUE;

	private final TextField txtFilterLastname = new TextField();
	private final TextField txtFilterGroupNumber = new TextField();
	private final Button btnClearFilterLastname = new Button(FontAwesome.TIMES);
	private final Button btnClearFilterGroupNumber = new Button(FontAwesome.TIMES);

	private StudentsDomain studentsDomain;
	private NumberParser numberParser;
	private static final Logger logger = Logger.getLogger(StudentsView.class.getName());

	public StudentsView() {
		super();
		
		initFields();
		initLayouts();
		initListeners();
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		super.enter(event);
		Page.getCurrent().setTitle(PAGE_TITLE);
		
		updateList();
	}

	/**
	 * Called when object was changed in the dialog window. Updates UI.
	 */
	@Override
	public void updateUI(final Student object) {
		updateList();
		try {
			gridObjects.select(object);
		} catch (final IllegalArgumentException e) {
		}
	}

	@Override
	protected void addClicked() {
		super.addClicked();
		showGroupEditDialog(new Student());
	}

	@Override
	protected void deleteSelectedClicked() {
		try {
			final Student group = getSelectedObject();
			if (group != null) {
				studentsDomain.delete(group.getId());
				updateList();
			}
		} catch (final PersistException e) {
			logger.log(Level.SEVERE, null, e);
			showErrorToUI(e);
		}
	}

	// Fetch list of groups from and assign it to grid
	private void updateList() {
		try {
			// Read filtering parameters
			final String filterLastname = txtFilterLastname.getValue();
			final Integer filterGroupNumber = numberParser.parseNumberOrNull(txtFilterGroupNumber.getValue());

			// Make StudentFilterParams
			final StudentFilterParams filterParams = new StudentFilterParams(filterLastname, filterGroupNumber);

			// Get list of students
			final List<Student> students;

			students = studentsDomain.filter(filterParams);

			final BeanItemContainer<Student> studentsContainer = new BeanItemContainer<>(Student.class, students);
			studentsContainer.addNestedContainerProperty(GROUP_NUMBER_COLUMN);
			studentsContainer.addNestedContainerProperty(FACULTY_NAME_COLUMN);

			gridObjects.setContainerDataSource(studentsContainer);
		} catch (final PersistException e) {
			logger.log(Level.SEVERE, null, e);
			showErrorToUI(e);
		}
	}

	private void initFields() {
		editDialogWindow = new StudentEditDialog(this);

		studentsDomain = StudentsDomain.getInstance();
		numberParser = NumberParser.getInstance();
	}

	private void initLayouts() {
		editDialogWindow.setModal(true);

		gridObjects.setColumns(NAME_COLUMN, LASTNAME_COLUMN, MIDDLENAME_COLUMN, BIRTH_DATE_COLUMN, GROUP_NUMBER_COLUMN,
				FACULTY_NAME_COLUMN);

		gridObjects.setHeightMode(HeightMode.ROW);
		gridObjects.setHeightByRows(GRID_ROWS);

		setColumnHeaders();
		createFilterPanel();

		final CssLayout pnlFilterLastname = new CssLayout();
		pnlFilterLastname.addComponents(txtFilterLastname, btnClearFilterLastname);
		pnlFilterLastname.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		final CssLayout pnlFilterGroupNumber = new CssLayout();
		pnlFilterGroupNumber.addComponents(txtFilterGroupNumber, btnClearFilterGroupNumber);
		pnlFilterGroupNumber.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		final HorizontalLayout filteringPanel = new HorizontalLayout();
		filteringPanel.addComponents(pnlFilterLastname, pnlFilterGroupNumber);

		filteringPanel.setSpacing(true);
		filteringPanel.setMargin(new MarginInfo(false, false, true, false));

		addComponents(actionButtons, filteringPanel, gridObjects);
	}

	private void setColumnHeaders() {
		gridObjects.getColumn(NAME_COLUMN).setHeaderCaption(NAME_HEADER);
		gridObjects.getColumn(LASTNAME_COLUMN).setHeaderCaption(LASTNAME_HEADER);
		gridObjects.getColumn(MIDDLENAME_COLUMN).setHeaderCaption(MIDDLENAME_HEADER);
		gridObjects.getColumn(BIRTH_DATE_COLUMN).setHeaderCaption(BIRTH_DATE_HEADER);
		gridObjects.getColumn(GROUP_NUMBER_COLUMN).setHeaderCaption(GROUP_NUMBER_HEADER);
		gridObjects.getColumn(FACULTY_NAME_COLUMN).setHeaderCaption(FACULTY_NAME_HEADER);
	}

	private void createFilterPanel() {
		txtFilterLastname.setInputPrompt(FILTER_BY_LASTNAME);
		txtFilterGroupNumber.setInputPrompt(FILTER_BY_GROUP_NAME);

		txtFilterGroupNumber.setConverter(new StringToIntegerConverter());
		txtFilterGroupNumber.addValidator(
				new IntegerRangeValidator(FILTER_GROUP_ERR, FILTER_GROUP_NUMBER_MIN, FILTER_GROUP_NUMBER_MAX));
		txtFilterGroupNumber.setNullRepresentation("");

		btnClearFilterLastname.setDescription(CLEAR_FILTER);
		btnClearFilterGroupNumber.setDescription(CLEAR_FILTER);
	}

	private void initListeners() {
		txtFilterLastname.addTextChangeListener(e -> {
			txtFilterLastname.setValue(e.getText());
			updateList();
		});
		txtFilterGroupNumber.addTextChangeListener(e -> {
			txtFilterGroupNumber.setValue(e.getText());
			updateList();
		});

		btnClearFilterLastname.addClickListener(e -> {
			txtFilterLastname.clear();
			updateList();
		});
		btnClearFilterGroupNumber.addClickListener(e -> {
			txtFilterGroupNumber.clear();
			updateList();
		});
	}

}
