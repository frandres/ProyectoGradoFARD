package mbfi.focalizedExtractor;

public class RegExpWithPriority implements Comparable<RegExpWithPriority>{

	private double priority;
	private String regExp;
	
	
	public double getPriority() {
		return priority;
	}


	public String getRegExp() {
		return regExp;
	}

	public RegExpWithPriority(double priority, String regExp) {
		super();
		this.priority = priority;
		this.regExp = regExp;
	}


	@Override
	public int compareTo(RegExpWithPriority o) {

		if (getPriority() == o.getPriority()){
			return 0;
		}
		
		if (getPriority() > o.getPriority()){
			return -1;
		}
		
		if (getPriority() < o.getPriority()){
			return 1;
		}
		
		return 0;
	}
	
	
	
}
