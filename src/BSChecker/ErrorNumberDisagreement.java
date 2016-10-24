package BSChecker;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;

/**
 * WIP
 * @author
 * 
 */
public class ErrorNumberDisagreement extends Error{
	private final int ERROR_NUMBER = 5;
	
	public static void main(String[] args){
		ArrayList<int[]> errs = new ErrorNumberDisagreement().findErrors("They eat the man. It is delicious. It are enjoyable. To eat men is enjoyable.");
	}
	public ArrayList<int[]> findErrors(String text){
		String sentences[] = sentenceDetector.sentDetect(text);
		ArrayList<Parse> parses = new ArrayList<Parse>();
		ArrayList<int[]> arr = new ArrayList<int[]>();
		for(String s: sentences){
			ParserTool.parseLine(s.substring(0, s.length()-1), parser, 1)[0].show();
			correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), parser, 1)[0]);
//			arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), parser, 1)[0]));
		}
		return null;
	}
	public static ArrayList<int[]> correctParse(Parse p){
//		System.out.println(p);
		SentenceTree head = new SentenceTree(null,p.getChildren()[0]);
		SentenceTree tree = head.fix();
		return new ArrayList<int[]>();
	}
}
