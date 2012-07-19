package mbfi.focalizedExtractor;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ExtractionContext {

	static Logger log = Logger.getLogger(ExtractionContext.class.getName());
	
	private List<FieldInformation> fieldsInformation;
	
	public ExtractionContext(List<FieldInformation> fieldsInformation) {
		super();
		this.fieldsInformation = fieldsInformation;
		Collections.sort(this.fieldsInformation);
	}
	
	public List<FieldInformation> getFieldsInformation() {
		return fieldsInformation;
	}

	public FieldInformation getFieldInformationByName (String name){
		FieldInformation dummy = new FieldInformation(name, null);
		
		int iPoint = Collections.binarySearch(fieldsInformation, dummy);
		if (iPoint>fieldsInformation.size()){
			log.log(Level.INFO, "No se ha encontrado el campo: " + name + "entre los file descriptors");
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
	
}
