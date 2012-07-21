package mdfi.incompletitudeFinder;

import java.util.List;

import mbfi.focalizedExtractor.FieldValue;
import mdfi.query.Attribute;

public class IncompletitudeFieldDescriptor {

	Attribute at;
	String fieldInformationName;
	String type;
	String domainType;
	List<FieldValue>possibleValues;
	boolean generateValues;
	public IncompletitudeFieldDescriptor(String conceptName, String fieldName,
			String type, String domainType, List<FieldValue> possibleValues,
			boolean generateValues, String fieldInformationName) {
		super();
		
		at = new Attribute(fieldName, conceptName);
		this.type = type;
		this.domainType = domainType;
		this.possibleValues = possibleValues;
		this.generateValues = generateValues;
		this.fieldInformationName = fieldInformationName;
	}
	
	public String getConceptName() {
		return getAttribute().getConcept();
	}
	public String getFieldName() {
		return getAttribute().getName();
	}
	public String getType() {
		return type;
	}
	public String getDomainType() {
		return domainType;
	}
	public List<FieldValue> getPossibleValues() {
		return possibleValues;
	}
	public boolean isGenerateValues() {
		return generateValues;
	}

	public Attribute getAttribute() {
		return at;
	}
	
	public void setPossibleValues(List<FieldValue> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public IncompletitudeFieldDescriptor clone() {
		return new IncompletitudeFieldDescriptor(getConceptName(), 
												   getFieldName(), 
												   getType(), 
												   getDomainType(), 
												   getPossibleValues(), 
												   isGenerateValues(),
												   getFieldInformationName());
	}

	public String getFieldInformationName() {
		return fieldInformationName;
	}
}
