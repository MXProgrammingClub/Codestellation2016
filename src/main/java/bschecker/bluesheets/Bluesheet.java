package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.reference.Settings;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;
import bschecker.util.UtilityMethods;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import opennlp.tools.parser.Parse;

/**
 * Defines abstract class for types of grammatical errors.
 * Provides a static method which finds bluesheet errors of all types referenced in {@link Bluesheets}.
 * References {@link Settings} to determine which bluesheets to check.
 * 
 * @author tedpyne
 * @author JeremiahDeGreeff
 * @see Bluesheets
 * @see Settings
 */
public abstract class Bluesheet {
	
	/**
	 * Finds errors of a specific type in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	protected abstract ErrorList findErrors(String line, Parse[] parses);
	
	/**
	 * Finds all errors within the given text.
	 * All Bluesheets referenced in {@link #Settings} with a value of {@code true} will be checked.
	 * 
	 * @param text the text to search
	 * @param logParses if true, all Parse trees will be logged to the console - should only be used for debugging
	 * @param progress if provided, this parameter will be updated as each line is processed - it is intended to be bound to a ProgressDialog
	 * @return a ErrorList which contains all the errors in the passage, referenced by character indices
	 */
	public static ErrorList findAllErrors(String text, boolean logParses, ReadOnlyDoubleWrapper progress) {
		PerformanceMonitor.start("analyze");
		if(!text.endsWith("\n"))
			text += "\n";
		ErrorList errors = new ErrorList(text, false);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			PerformanceMonitor.start("line");
			if(progress != null)
				progress.set((double) lineNum);
			line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			LogHelper.line();
			LogHelper.getLogger(17).info("Analyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			
			PerformanceMonitor.start("parse");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			String[] linePointer = new String[] {line};
			Parse[] parses = UtilityMethods.parseLine(linePointer, logParses, lineNum, charOffset, removedChars);
			line = linePointer[0];
			LogHelper.getLogger(18).info("Complete (" + PerformanceMonitor.stop("parse") + ")");
			
			ErrorList lineErrors = new ErrorList(line, true);
			for(Bluesheets b : Bluesheets.values())
				if(Settings.isSetToAnalyze(b.getNumber())) {
					PerformanceMonitor.start("bluesheet");
					LogHelper.getLogger(17).info("Looking for: " + b.getName() + "...");
					ErrorList bluesheetErrors = b.getObject().findErrors(line, parses);
					bluesheetErrors.setBluesheetNumber(b.getNumber());
					lineErrors.addAll(bluesheetErrors);
					LogHelper.getLogger(17).info(bluesheetErrors.size() + " Error" + (bluesheetErrors.size() == 1 ? "" : "s") + " Found (" + PerformanceMonitor.stop("bluesheet") + ")");
				}
			LogHelper.getLogger(17).info(lineErrors.size() + " Error" + (lineErrors.size() == 1 ? "" : "s") + " Found in line " + lineNum + " (" + PerformanceMonitor.stop("line") + ")");
			
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));
			
			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		LogHelper.line();
		LogHelper.getLogger(17).info("Passage analyzed in " + PerformanceMonitor.stop("analyze") + "\n\n" + errors);
		
		return errors;
	}
	
}
