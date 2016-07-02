package com.demidov.university.presentation;

import javax.servlet.annotation.WebServlet;

import com.demidov.university.presentation.views.GroupsView;
import com.demidov.university.presentation.views.StartView;
import com.demidov.university.presentation.views.StudentsView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@Widgetset("com.demidov.university.MyAppWidgetset")
public class MainUI extends UI {
	
	private static final String INSTITUTE_MENU = "Институт", STUDENTS_ITEM = "Студенты", GROUPS_ITEM = "Группы";
	private static final String HEADER_TEXT = "Онлайн Институт";
	private static final String START_VIEW = "", STUDENTS_VIEW = "students", GROUPS_VIEW = "groups";

	private final MenuBar menuMain = new MenuBar();
	private Navigator navigator;
	private VerticalLayout content;
	
	@Override
	protected void init(final VaadinRequest vaadinRequest) {
		getPage().setTitle("init page");
		
		initMainMenu();
		
		content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
		
        final VerticalLayout all = new VerticalLayout(menuMain, content);
        setContent(all);
		
		initNavigator();
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}

	private void initMainMenu() {
		// Menu item listeners
		final Command studentsCommand = new Command() {
	        public void menuSelected(final MenuItem selectedItem) {
	            navigator.navigateTo(STUDENTS_VIEW);
	        }
	    };
	    
	    final Command groupsCommand = new Command() {
	        public void menuSelected(final MenuItem selectedItem) {
	            navigator.navigateTo(GROUPS_VIEW);
	        }
	    };
	    
	    // Menu items
	    menuMain.addItem(HEADER_TEXT, null);
		final MenuItem institute = menuMain.addItem(INSTITUTE_MENU, null);
		institute.addItem(STUDENTS_ITEM, studentsCommand);
		institute.addItem(GROUPS_ITEM, groupsCommand);
		
		// Style menu
		menuMain.setWidth("100%");
	}
	
	private void initNavigator() {
        navigator = new Navigator(this, content);
		
        // Create and register the views
        navigator.addView(START_VIEW, new StartView());
        navigator.addView(STUDENTS_VIEW, new StudentsView());
        navigator.addView(GROUPS_VIEW, new GroupsView());
	}

}
