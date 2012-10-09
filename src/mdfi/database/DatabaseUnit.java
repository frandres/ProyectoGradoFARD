package mdfi.database;

import java.util.ArrayList;
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
		
//		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
//			FieldInformation fInfox = (FieldInformation) iterator.next();
//			System.out.println(fInfox.getFieldName()+": " + fInfox.getFieldValues().get(0).getTextValue());
//		}
	}
	
	public String toString(){
		String s = "";
		for (Iterator <FieldInformation> iterator = values.iterator(); iterator.hasNext();) {
			FieldInformation fInfo = (FieldInformation) iterator.next();
			s += (fInfo.getFieldName() + " has value: " + fInfo.getFieldValues().get(0).getTextValue() + System.getProperty("line.separator"));
		}
		
		return s;
		
		
	}
}
