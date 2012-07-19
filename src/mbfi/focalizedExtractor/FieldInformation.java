package mbfi.focalizedExtractor;

public class FieldInformation implements Comparable<FieldInformation>{

	private String fieldName;
	private String fieldValue;

	public FieldInformation(String fieldName, String fieldValue) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public String getFieldValue() {
		return fieldValue;
	}

	@Override
	public int compareTo(FieldInformation o) {
		return getFieldName().compareTo(o.getFieldName());
	}

	
	
	
}
