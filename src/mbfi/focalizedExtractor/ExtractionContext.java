package mbfi.focalizedExtractor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mdfi.query.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ucar.units.RaiseException;

public class ExtractionContext {

	static Logger log = Logger.getLogger(ExtractionContext.class.getName());
	
	private List<FieldInformation> fieldsInformation;
	private Attribute pkey;
	
	public ExtractionContext(List<FieldInformation> fieldsInformation, Attribute pkey) {
		super();
		this.fieldsInformation = fieldsInformation;
		Collections.sort(this.fieldsInformation);
		this.pkey = pkey;
	}
	
	public List<FieldInformation> getFieldsInformation() {
		return fieldsInformation;
	}

	public FieldInformation getFieldInformationByName (String name){
		FieldInformation dummy = new FieldInformation(name, null);
//		System.out.println("--");
//		System.out.println("L:"+name);
		for (Iterator <FieldInformation> iterator = fieldsInformation.iterator(); iterator.hasNext();) {
			FieldInformation fInfo= (FieldInformation) iterator.next();
//			System.out.println("P:"+fInfo.getFieldName());
		}
		
		int iPoint = Collections.binarySearch(fieldsInformation, dummy);
		if (iPoint>fieldsInformation.size()||iPoint<0){
			log.log(Level.INFO, "No se ha encontrado el campo: " + name + "entre los file descriptors");
//			System.out.println(1/0);
			return null;
		}
		
		FieldInformation possibleMatch = fieldsInformation.get(iPoint);
		
		if (possibleMatch.getFieldName().compareTo(name)==0){
			return possibleMatch;
		}else {
			log.log(Level.INFO, "No se ha encontrado el campo: " + name + "entre los file descriptors");
			return null;
		}	
	}

	public Attribute getPrimaryKey() {
		return pkey.clone();
	}

	
}
