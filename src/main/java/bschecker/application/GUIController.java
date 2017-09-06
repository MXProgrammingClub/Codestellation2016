package bschecker.application;

import java.io.File;
import java.io.IOException;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.jfoenix.controls.JFXButton;

import bschecker.bluesheets.Bluesheet;
import bschecker.bluesheets.Bluesheets;
import bschecker.reference.Paths;
import bschecker.reference.Settings;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.TextImport;
import bschecker.util.UtilityMethods;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;

/**
 * Connects the main GUI of the Application with the rest of the program.
 * 
 * @author Luke Giacalone
 * @author JeremiahDeGreeff
 */

public class GUIController {
	
	@FXML
	private CheckMenuItem menuBluesheet1;
	@FXML
	private CheckMenuItem menuBluesheet2;
	@FXML
	private CheckMenuItem menuBluesheet3;
	@FXML
	private CheckMenuItem menuBluesheet4;
	@FXML
	private CheckMenuItem menuBluesheet5;
	@FXML
	private CheckMenuItem menuBluesheet6;
	@FXML
	private CheckMenuItem menuBluesheet7;
	@FXML
	private CheckMenuItem menuBluesheet8;
	@FXML
	private CheckMenuItem menuBluesheet9;
	@FXML
	private CheckMenuItem menuBluesheet10;
	@FXML
	private CheckMenuItem menuBluesheet11;
	@FXML
	private CheckMenuItem menuBluesheet12;
	@FXML
	private CheckMenuItem menuBluesheet13;
	@FXML
	private CheckMenuItem menuBluesheet14;
	
	@FXML
	private StyleClassedTextArea essayBox;
	@FXML
	private StyleClassedTextArea errorBox;
	@FXML
	private StyleClassedTextArea noteBox;
	
	@FXML
	private JFXButton analyzeButton;
	
	private ErrorList errors = new ErrorList(null, false);
	private int currError = 0;
	
	private File file;
	private String clipboard = "";
	
	public GUIController(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.APPLICATION_FXML));
		loader.setController(this);
		Parent root = null;
		try {root = loader.load();}
		catch(IOException e) {
			LogHelper.getLogger(15).fatal("Application failed to load - program terminating.");
			e.printStackTrace();
			System.exit(1);
		}
		Scene scene = new Scene(root, 1000, 656);
		scene.getStylesheets().add(getClass().getResource(Paths.APPLICATION_STYLESHEET).toExternalForm());
		
		essayBox.replaceText("Insert Essay Here");
		errorBox.replaceText("No Error Selected");
		noteBox.replaceText("No Error Selected");
		loadSettings();
		
		primaryStage.setTitle("BSChecker");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/**
	 * The method that will be called when the analyze button is clicked
	 */
	@FXML
	private void analyzeButtonClick() {
		LogHelper.getLogger(17).info("Analyze Button Clicked");
		String text = UtilityMethods.replaceInvalidChars(essayBox.getText());
		essayBox.replaceText(text);
		runAnalyze(text);
	}
	
	/**
	 * The method that will be called when the left arrow is clicked
	 */
	@FXML
	private void leftArrowClick() {previousError();}
	
	/**
	 * The method that will be called when the right arrow is clicked
	 */
	@FXML
	private void rightArrowClick() {nextError();}
	
	
	/**
	 * The method that will be called when the File->Open is clicked. It takes the file and puts the contents into the essay box.
	 */
	@FXML
	private void menuOpenClick() {
		file = TextImport.chooseFile();
		if(file == null) {
			LogHelper.getLogger(16).error("Invalid file selection - aborting.");
			alert(AlertType.ERROR, "Selection Error", "There was an error in the file selection. The file selected is invalid.");
			return;
		}
		String text = TextImport.openFile(file);
		if(text == null) {
			LogHelper.getLogger(16).error("Unable to read any text from the file - aborting.");
			alert(AlertType.ERROR, "Selection Error", "There was an error in the file selection. No text was able to be extracted from the file.");
			return;
		}
		essayBox.replaceText(text);
		LogHelper.getLogger(16).info(file.getName() + " was loaded successfully.");
	}

	/**
	 * The method that will be called when the File->Save is clicked
	 */
	@FXML
	private void menuSaveClick() {
		if(file == null)
			TextImport.saveAs(essayBox.getText());
		else if(TextImport.saveText(file, essayBox.getText()))
			LogHelper.getLogger(16).info(file.getName() + " was saved successfully");
		else
			alert(AlertType.ERROR, "Saving Error", "There was an error in saving your file. It may be in use or moved from its original location.");
	}

	/**
	 * The method that will be called when the File->Save As is clicked
	 */
	@FXML
	private void menuSaveAsClick() {file = TextImport.saveAs(essayBox.getText());}

	/**
	 * The method that will be called when the Edit->Undo is clicked
	 */
	@FXML
	private void menuUndoClick() {/* EDIT->UNDO ACTION */}

	/**
	 * The method that will be called when the Edit->Redo is clicked
	 */
	@FXML
	private void menuRedoClick() {/* EDIT->REDO ACTION */}

	/**
	 * The method that will be called when the Edit->Cut is clicked
	 */
	@FXML
	private void menuCutClick() {
		String temp = essayBox.getSelectedText();
		if(!temp.equals("")) {
			clipboard = temp;
			essayBox.deleteText(essayBox.getSelection());
		}
	}

	/**
	 * The method that will be called when the Edit->Copy is clicked
	 */
	@FXML
	private void menuCopyClick() {
		String temp = essayBox.getSelectedText();
		if(!temp.equals(""))
			clipboard = temp;
	}

	/**
	 * The method that will be called when the Edit->Paste is clicked
	 */
	@FXML
	private void menuPasteClick() {essayBox.insertText(essayBox.getSelection().getEnd(), clipboard);}

	/**
	 * The method that will be called when the Edit->Select All is clicked
	 */
	@FXML
	private void menuSelectAllClick() {essayBox.selectAll();}

	/**
	 * The method that will be called when the View->Next Error is clicked
	 */
	@FXML
	private void menuNextErrorClick() {nextError();}
	
	/**
	 * The method that will be called when the View->Previous Error is clicked
	 */
	@FXML
	private void menuPreviousErrorClick() {previousError();}
	
	/**
	 * The method that will be called when the Bluesheets->Past Tense (1) is clicked
	 */
	@FXML
	private void menuBluesheet1Click() {menuBluesheetClick(1);}
	
	/**
	 * The method that will be called when the Bluesheets->Incomplete Sentence (2) is clicked
	 */
	@FXML
	private void menuBluesheet2Click() {menuBluesheetClick(2);}
	
	/**
	 * The method that will be called when the Bluesheets->First/Second Person (3) is clicked
	 */
	@FXML
	private void menuBluesheet3Click() {menuBluesheetClick(3);}
	
	/**
	 * The method that will be called when the Bluesheets->Vague This/Which (4) is clicked
	 */
	@FXML
	private void menuBluesheet4Click() {menuBluesheetClick(4);}
	
	/**
	 * The method that will be called when the Bluesheets->Subject-Verb Disagreement (5) is clicked
	 */
	@FXML
	private void menuBluesheet5Click() {menuBluesheetClick(5);}
	
	/**
	 * The method that will be called when the Bluesheets->Pronoun Case (6) is clicked
	 */
	@FXML
	private void menuBluesheet6Click() {menuBluesheetClick(6);}
	
	/**
	 * The method that will be called when the Bluesheets->Ambiguous Pronoun (7) is clicked
	 */
	@FXML
	private void menuBluesheet7Click() {menuBluesheetClick(7);}
	
	/**
	 * The method that will be called when the Bluesheets->Apostrophe Error (8) is clicked
	 */
	@FXML
	private void menuBluesheet8Click() {menuBluesheetClick(8);}
	
	/**
	 * The method that will be called when the Bluesheets->Passive Voice (9) is clicked
	 */
	@FXML
	private void menuBluesheet9Click() {menuBluesheetClick(9);}
	
	/**
	 * The method that will be called when the Bluesheets->Dangling Modifier (10) is clicked
	 */
	@FXML
	private void menuBluesheet10Click() {menuBluesheetClick(10);}
	
	/**
	 * The method that will be called when the Bluesheets->Faulty Parallelism (11) is clicked
	 */
	@FXML
	private void menuBluesheet11Click() {menuBluesheetClick(11);}
	
	/**
	 * The method that will be called when the Bluesheets->Progressive Tense (12) is clicked
	 */
	@FXML
	private void menuBluesheet12Click() {menuBluesheetClick(12);}
	
	/**
	 * The method that will be called when the Bluesheets->Gerund Possesive (13) is clicked
	 */
	@FXML
	private void menuBluesheet13Click() {menuBluesheetClick(13);}
	
	/**
	 * The method that will be called when the Bluesheets->Quotation Form (14) is clicked
	 */
	@FXML
	private void menuBluesheet14Click() {menuBluesheetClick(14);}
	
	/**
	 * The method that will be called when the Bluesheets->Default Settings is clicked
	 */
	@FXML
	private void menuDefaultSettingsClick() {
		Settings.writeDefaultSettings();
		this.loadSettings();
	}
	
	/**
	 * The method that will be called when the Bluesheets->Select All Bluesheets is clicked
	 */
	@FXML
	private void menuSelectAllBluesheetsClick() {
		for(int i = 1; i <= 14; i++)
			if(!getMenuBluesheet(i).isSelected()) {
				getMenuBluesheet(i).setSelected(true);
				menuBluesheetClick(i);
			}
	}
	
	/**
	 * The method that will be called when the Bluesheets->Deselect All Bluesheets is clicked
	 */
	@FXML
	private void menuDeselectAllBluesheetsClick() {
		for(int i = 1; i <= 14; i++)
			if(getMenuBluesheet(i).isSelected()) {
				getMenuBluesheet(i).setSelected(false);
				menuBluesheetClick(i);
			}
	}

	/**
	 * The method that will be called when the Help->About is clicked
	 */
	@FXML
	private void menuAboutClick() {/* HELP->ABOUT ACTION */}
	
	/**
	 * Accessor for a bluesheet's CheckMenuItem based on its number.
	 * 
	 * @param number the number of the bluesheet
	 * @return the CheckMenuItem for that bluesheet's setting
	 */
	private CheckMenuItem getMenuBluesheet(int number) {
		switch(number) {
		case 1: return menuBluesheet1;
		case 2: return menuBluesheet2;
		case 3: return menuBluesheet3;
		case 4: return menuBluesheet4;
		case 5: return menuBluesheet5;
		case 6: return menuBluesheet6;
		case 7: return menuBluesheet7;
		case 8: return menuBluesheet8;
		case 9: return menuBluesheet9;
		case 10: return menuBluesheet10;
		case 11: return menuBluesheet11;
		case 12: return menuBluesheet12;
		case 13: return menuBluesheet13;
		case 14: return menuBluesheet14;
		default: return null;
		}
	}
	
	/**
	 * This method is called whenever a bluesheet's CheckMenuItem is clicked.
	 * It reverses the corresponding setting in {@link Settings} and gives a warning if the bluesheet is not fully available.
	 * 
	 * @param number the number corresponding to the bluesheet which was clicked
	 */
	private void menuBluesheetClick(int number) {
		Settings.reverseSetting(number);
		if(Bluesheets.getBluesheetFromNumber(number).getAvailabilityWarning() != null && getMenuBluesheet(number).isSelected())
			alert(AlertType.WARNING, "Warning", Bluesheets.getBluesheetFromNumber(number).getAvailabilityWarning());
	}
	
	/**
	 * Updates the selected text to the next error.
	 */
	private void nextError() {
		if(errors.size() != 0) {
			resetCurrentColor();
			currError++;
			if(currError >= errors.size()) {
				alert(AlertType.INFORMATION, "Notice", "Wrapping search to beginning of passage.");
				currError = 0;
			}
			displayError();
		}
	}
	
	/**
	 * Updates the selected text to the previous error.
	 */
	private void previousError() {
		if(errors.size() != 0) {
			resetCurrentColor();
			currError--;
			if(currError < 0) {
				alert(AlertType.INFORMATION, "Notice", "Wrapping search to end of passage.");
				currError = errors.size() - 1;
			}
			displayError();
		}
	}
	
	/**
	 * Runs the analysis of the passed text using a Task on a separate thread.
	 * 
	 * @param text the text to analyze
	 */
	private void runAnalyze(final String text) {
		ProgressDialogController dialog = new ProgressDialogController();
		Task<ErrorList> task = new Task<ErrorList>() {
			@Override
			public ErrorList call() {
				int numLines = UtilityMethods.countOccurences(text, "\n");
				final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");
				progress.getReadOnlyProperty().addListener((obs, oldProgress, newProgress) -> updateProgress((double) newProgress, (double) numLines));
				return Bluesheet.findAllErrors(text, false, progress);
			}
		};
		
		dialog.activateProgressBar(task);
		
		task.setOnRunning(event -> {analyzeButton.setDisable(true);});
		
		task.setOnSucceeded(event -> {
			LogHelper.getLogger(17).info("Analyze Successful");
			essayBox.setStyleClass(0, essayBox.getLength(), null);
			dialog.close();
			errors = task.getValue();
			if(errors.size() == 0)
				errorBox.replaceText("No Errors Found!");
			else {
				for(Error error : errors)
					essayBox.setStyleClass(error.getStartIndex(), error.getEndIndex() + 1, "light-red");
				currError = 0;
				displayError();
			}
			analyzeButton.setDisable(false);
		});
		
		task.setOnCancelled(event -> {
			LogHelper.getLogger(17).warn("Analyze Canceled");
			analyzeButton.setDisable(false);
		});
		
		Thread thread = new Thread(task, "Analyze");
		thread.start();
	}
	
	/**
	 * Displays the current error.
	 */
	private void displayError() {
		essayBox.positionCaret(errors.get(currError).getStartIndex());
		essayBox.setStyleClass(errors.get(currError).getStartIndex(), errors.get(currError).getEndIndex() + 1, "dark-red");
		Bluesheets b = Bluesheets.getBluesheetFromNumber(errors.get(currError).getBluesheetNumber());
		errorBox.replaceText(b.getName() + "\n\n" + b.getDescription() + "\n\n" + b.getExample());
		noteBox.replaceText(errors.get(currError).getNote().equals("") ? "No note was found for this error." : errors.get(currError).getNote());
	}
	
	/**
	 * Resets the color of the current error to the lighter color.
	 */
	private void resetCurrentColor() {
		essayBox.setStyleClass(errors.get(currError).getStartIndex(), errors.get(currError).getEndIndex() + 1, "light-red");
	}
	
	/**
	 * Loads the settings into the checkedMenuItems for each bluesheet from {@link Settings}.
	 */
	private void loadSettings() {
		LogHelper.getLogger(0).info("Loading settings into the menu");
		boolean[] settings = Settings.getSettings();
		for(int i = 0; i < 14; i++)
			getMenuBluesheet(i + 1).setSelected(settings[i]);
	}
	
	/**
	 * Creates an alert.
	 * 
	 * @param type the type of the alert
	 * @param title the title of the alert
	 * @param content the body text of the alert
	 */
	private void alert(AlertType type, String title, String content) {
		Alert a = new Alert(type);
		a.setTitle(title);
		a.setHeaderText(null);
		a.setContentText(content);
		a.showAndWait();
	}
	
}
