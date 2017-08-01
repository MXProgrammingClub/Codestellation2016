package bschecker.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bschecker.bluesheets.Bluesheet;
import bschecker.bluesheets.Bluesheets;

public class LogHelper {
	
	private static Logger[] loggers = {
			LogManager.getLogger("Init"),
			LogManager.getLogger("Past Tense"),
			LogManager.getLogger("Fragment/Run-On/Comma-Splice"),
			LogManager.getLogger("First/Second Person"),
			LogManager.getLogger("Vague \"this\" or \"which\""),
			LogManager.getLogger("Subject-Verb Disagreement"),
			LogManager.getLogger("Pronoun Case"),
			LogManager.getLogger("Ambiguous Pronoun"),
			LogManager.getLogger("Apostrophe Error"),
			LogManager.getLogger("Passive Voice"),
			LogManager.getLogger("Dangling Participle"),
			LogManager.getLogger("Faulty Parallelism"),
			LogManager.getLogger("Progressive Tense"),
			LogManager.getLogger("Incorrect Use of Gerund/Possessive"),
			LogManager.getLogger("Quotation Error"),
			LogManager.getLogger("Application"),
			LogManager.getLogger("I/O"),
			LogManager.getLogger("Analyze"),
			LogManager.getLogger("Parse")
	};
	
	/**
	 * returns the logger at the passed index
	 * @param number the number of the desired logger:
	 * 0 for Init
	 * 1-14 for each bluesheet
	 * 15 for Application
	 * 16 for I/O
	 * 17 for Analyze
	 * 18 for Parse
	 * @return the desired logger
	 * @throws IllegalArgumentException if number is not [0, 18]
	 */
	public static Logger getLogger(int number) {
		if(number > 18 || number < 0)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [0, 18].");
		return loggers[number];
	}
	
	/**
	 * returns the logger associated with the passed Bluesheet object
	 * @param object the Bluesheet object whose logger should be returned
	 * @return the logger corresponding to this Bluesheet
	 */
	public static Logger getLogger(Bluesheet object) {
		return loggers[Bluesheets.getNumber(object)];
	}
	
}
