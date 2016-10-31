package bsChecker;

import java.util.ArrayList;

/**
 * @author tedpyne
 * Finds errors with vague use of this or which. (4)
 */
public class ErrorVagueThisWhich extends Error {
	private static final int ERROR_NUMBER = 4;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Error.setupOpenNLP();
		String input = "Hi, my name I hate this; cars are fun.";
		printErrors(new ErrorVagueThisWhich().findErrors(input), input);
	}
	
	@Override
	public ArrayList<int[]> findErrors(String line){
		ArrayList<int[]> found = new ArrayList<int[]>();
		int totLen = 0;
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		int wFound = 0, tFound = 0;
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].equalsIgnoreCase("this")){
				if(isVague(tokens,tags,i)){
					int[] err = {totLen+locationOf(line,tokens[i],tFound)-1,
							totLen+locationOf(line,tokens[i],tFound)+tokens[i].length()-1,ERROR_NUMBER};
					found.add(err);	
				}
				tFound++;
			}
			if(tokens[i].equalsIgnoreCase("which")){
				if(i == 0 || (tags[i-1].charAt(0)!='N' && tags[i-1].charAt(0)!='I')){
					int[] err = {totLen+locationOf(line,tokens[i],wFound)-1,
							totLen+locationOf(line,tokens[i],wFound)+tokens[i].length()-1,ERROR_NUMBER};
					found.add(err);	
				}
				wFound++;
			}
		}
		totLen+=line.length()+1;

		return found;
	}

	private boolean isVague(String[] tokens, String[] tags, int i) {
		if(i==tokens.length-1) return true;
		for(int j = i+1; j < tokens.length; j++){
//			System.out.println(tags[j]);
			if(tags[j].charAt(0)=='N') return false;
			if(tags[j].charAt(0)=='V' || tags[j].charAt(0)=='.' || tags[j].charAt(0)==':') return true;
		}
		return true;
	}
}