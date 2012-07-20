package mdfi.conditions.rightHandedSide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mbfi.focalizedExtractor.FieldValue;
import mdfi.incompletitudeFinder.IncompletitudeFinder;
import mdfi.query.Query;

public class SimpleValue extends RightHandSide {
	
	static Logger log = Logger.getLogger(IncompletitudeFinder.class.getName());
	
	private List<FieldValue> values;

	public SimpleValue(List<FieldValue> values) {
		super();
		this.values = values;
	}

	public List<FieldValue> getValues() {
		List<FieldValue> newFieldValues = new ArrayList<FieldValue>();
		for (Iterator <FieldValue> iterator = values.iterator(); iterator.hasNext();) {
			FieldValue fValue = iterator.next();
			newFieldValues.add(fValue.clone());
			
		}
		return newFieldValues;
	}

	@Override
	public String toString() {
		String toString = "";
		if (values.size()>1){ 
			for (Iterator <FieldValue> iterator = values.iterator(); iterator.hasNext();) {
				FieldValue value = iterator.next();
				toString+=value.getTextValue() + ",";
			}
		}
		
		if (values.size()==1){ 
			toString= values.get(0).getTextValue();
		}
		
		if (values.size()==0){ 
			log.log(Level.WARN, "List of values is empty");
		}
		
		return toString;
	}
	
	@Override
	public List<Query> getQueries() {
		return new ArrayList<Query>();
	}

	@Override
	public RightHandSide clone() {
		return new SimpleValue(getValues());
	}
}
