package mbfi.focalizedExtractor;

import java.util.List;

public class FieldInformation implements Comparable<FieldInformation>{

	private String fieldName;
	private List<FieldValue> fieldValues;

	public FieldInformation(String fieldName, List<FieldValue> fieldValues) {
		super();
		this.fieldName = fieldName;
		this.fieldValues = fieldValues;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public List<FieldValue> getFieldValues() {
		return fieldValues;
	}

	@Override
	public int compareTo(FieldInformation o) {
		return getFieldName().compareTo(o.getFieldName());
	}

	public void addValue(FieldValue fieldValue) {
		fieldValues.add(fieldValue);
	}

	
	
	
}
