package mbfi.textPreProcessor.extractor;
public class RegExpDescritor implements Comparable<RegExpDescritor> {

	private String regExp;
	private int priority;
	
	
	public RegExpDescritor(String regExp, int priority) {
		super();
		this.regExp = regExp;
		this.priority = priority;
	}
	
	protected String getRegExp() {
		return regExp;
	}
	protected int getPriority() {
		return priority;
	}

	@Override
	public int compareTo(RegExpDescritor o) {
		return this.getPriority()-o.getPriority();
			
	}
	
	
}
