package mbfi.focalizedExtractor;

public class FieldValue {

	int type;
	String textValue;
	
	public FieldValue(String textValue,int type) {
		super();
		this.type = type;
		this.textValue = textValue;
	}

	public int getType() {
		return type;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	
	public boolean getBooleanValue(){
		if (type!=FieldDescriptor.BOOLEAN){
			return false;
		}
		
		return Boolean.parseBoolean(getTextValue());
	}
	
	public int getIntValue(){
		if (type!=FieldDescriptor.INTEGER){
			return -1;
		}
		
		return Integer.parseInt(getTextValue());
	}
	
	public double getDouble(){
		if (type!=FieldDescriptor.DOUBLE){
			return -1;
		}
		
		return Double.parseDouble(getTextValue());
	}
	
	public String getStringValue(){
		return getTextValue();
	}
	
	public FieldValue clone(){
		return new FieldValue(getTextValue(), getType());
	}
}
