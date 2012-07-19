package mbfi.focalizedExtractor;

import java.util.ArrayList;
import java.util.List;

public class FieldDescriptor implements Comparable<FieldDescriptor>{

	private String fieldName;
//	private String documentRegExp;
	private List <String> specificRegExp;
	private double weight;
	
	private int type;
	
	
	public final static int STRING = 0;
	public final static int DEFAULT = 0;
	public final static int INTEGER = 1;
	public final static int DOUBLE = 2;
	public final static int BOOLEAN = 3;
	public final static int DATE = 4;
	
	public FieldDescriptor(String fieldName,
			List <String> specificRegExp, double weight, int type) {
		super();
		this.fieldName = fieldName;
		this.specificRegExp = specificRegExp;
		this.weight = weight;
		this.type = type;
	}
	
	public FieldDescriptor(String fieldName) {
		super();
		this.fieldName = fieldName;
		this.specificRegExp = null;
		this.weight = 0;
		this.type=DEFAULT;
	}

	protected String getFieldName() {
		return fieldName;
	}


	public int getType() {
		return type;
	}

	protected List <String> getSpecificRegExp() {
		return specificRegExp;
	}

	public boolean isDate(){
		return (this.type == DATE);
	}
	
	public double getWeight() {
		return weight;
	}

	@Override
	public int compareTo(FieldDescriptor o) {
		return fieldName.compareTo(o.getFieldName());
	}

	public List<String> getPossibleValues(String fieldValue) {
		
		
		if (isDate()){
			DateManipulator dMan = new DateManipulator(fieldValue);
			return dMan.getEquivalentDates();
			
		} else {
			ArrayList<String> possibleValues = new ArrayList<String>();
			possibleValues.add(fieldValue);
			return possibleValues;
		}
		
	}

}
