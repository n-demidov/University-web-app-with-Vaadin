package com.demidov.university.model.parser;

import java.util.logging.Logger;

public class NumberParser {
	
	private static final String NON_BREAKING_SPACE = " ", SPACE = " ";

	private static NumberParser instance;
	private static final Logger logger = Logger.getLogger(NumberParser.class.getName());

	public static synchronized NumberParser getInstance() {
		if (instance == null)
			instance = new NumberParser();
		return instance;
	}

	private NumberParser() {
		super();
	}
	
	/**
	 * Parse string value to number value or return null
	 * @param string
	 * @return
	 */
	public Integer parseNumberOrNull(final String string) {
 		if (string == null || string.trim().isEmpty()) {
 			return null;
 		}

 		final String newStr = string
 				.replace(NON_BREAKING_SPACE, "")
 				.replace(SPACE, "");

		try {
			return Integer.parseInt(newStr);
		} catch (final NumberFormatException e) {
			return null;
		}
    }
	
}
