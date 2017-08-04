package bschecker.bluesheets;

import bschecker.util.ErrorList;
import opennlp.tools.parser.Parse;

/**
 * WIP
 * Finds errors with verbs which don't agree in number with their subjects
 * and pronouns which don't agree in number with their antecedents. (5)
 * @author
 */
public class NumberDisagreement extends Bluesheet {
	
	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses){
//		String sentences[] = Tools.getSentenceDetector().sentDetect(line);
//		ArrayList<Parse> parses = new ArrayList<Parse>();
//		ArrayList<int[]> arr = new ArrayList<int[]>();
//		for(String s: sentences){
//			ParserTool.parseLine(s.substring(0, s.length()-1), Tools.getParser(), 1)[0].show();
//			correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), Tools.getParser(), 1)[0]);
//			arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), UtilityMethods.getParser(), 1)[0]));
//		}
		ErrorList errors = new ErrorList(line);
		return errors;
	}
//	public static ArrayList<int[]> correctParse(Parse p){
//		System.out.println(p);
//		SentenceTree head = new SentenceTree(null,p.getChildren()[0]);
//		SentenceTree tree = head.fix();
//		return new ArrayList<int[]>();
//	}
	
}
