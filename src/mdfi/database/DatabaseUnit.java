package mdfi.database;

import java.util.Iterator;
import java.util.List;

import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;

public class DatabaseUnit {

	private List<FieldInformation> values;
	
	public DatabaseUnit(List<FieldInformation> values) {
		super();
		this.values = values;
	}
	
	public FieldValue getFieldValueByFieldName(String fieldName){
		FieldValue result = null;
		
		for (Iterator <FieldInformation> iterator = values.iterator(); iterator.hasNext();) {
			FieldInformation fInfo = iterator.next();
			
			if (fInfo.getFieldName().compareTo(fieldName)==0){
				result = fInfo.getFieldValues().get(0);
			}
		}
		
		return result;
		
	}
	
	public List<FieldInformation> getValues() {
		return values;
	}

	public void insertValue(FieldInformation fInfo) {
		values.add(fInfo);
	}
}
