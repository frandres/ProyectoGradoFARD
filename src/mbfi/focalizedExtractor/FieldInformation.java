package mbfi.focalizedExtractor;

import java.util.List;

public class FieldInformation implements Comparable<FieldInformation>{

	private String fieldName;
	private List<String> fieldValues;

	public FieldInformation(String fieldName, List<String> fieldValues) {
		super();
		this.fieldName = fieldName;
		this.fieldValues = fieldValues;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public List<String> getFieldValues() {
		return fieldValues;
	}

	@Override
	public int compareTo(FieldInformation o) {
		return getFieldName().compareTo(o.getFieldName());
	}

	
	
	
}
