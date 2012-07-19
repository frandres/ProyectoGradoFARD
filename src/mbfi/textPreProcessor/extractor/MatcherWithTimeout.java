package mbfi.textPreProcessor.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherWithTimeout extends Thread {

	private String result;
	private String input;
	private Pattern pattern;

	public MatcherWithTimeout(String input,  Pattern pattern) {
		super();
		this.input = input;
		this.pattern = pattern;
		result = null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		result = "";
		
		Matcher matcher = pattern.matcher(input);
		try{
			while(matcher.find()){				
				result +=System.getProperty("line.separator") + matcher.group(1);
			}
		}
		catch (IllegalStateException e) {
			return;		
		}
		
	}

	protected String getResult() {
		return result;
	}
	
	
}
