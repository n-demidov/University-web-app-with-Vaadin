package com.demidov.university.presentation.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class StartView extends VerticalLayout implements View {

	private static final String WELCOME_PHRASE = "Добро пожаловать на главную страницу Online Institute";
	private static final String DESCRIPTION
		= "Для перехода на страницы студентов и групп воспользуйтесь пунктами меню вверху экрана";

	public StartView() {
		setSizeFull();

		final Label lblInfo = new Label(WELCOME_PHRASE);
		final Label lblDescription = new Label(DESCRIPTION);

		addComponents(lblInfo, lblDescription);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		Notification.show(
				WELCOME_PHRASE,
				Notification.Type.HUMANIZED_MESSAGE);
	}

}
