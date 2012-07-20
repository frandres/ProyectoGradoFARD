package mdfi.incompletitudeFinder;

import java.util.List;

import mbfi.focalizedExtractor.FieldValue;

public class IncompletitudeFieldDescriptor {

	String conceptName;
	String fieldName;
	String type;
	String domainType;
	List<FieldValue>possibleValues;
	boolean generateValues;
	public IncompletitudeFieldDescriptor(String conceptName, String fieldName,
			String type, String domainType, List<FieldValue> possibleValues,
			boolean generateValues) {
		super();
		this.conceptName = conceptName;
		this.fieldName = fieldName;
		this.type = type;
		this.domainType = domainType;
		this.possibleValues = possibleValues;
		this.generateValues = generateValues;
	}
	
	public String getConceptName() {
		return conceptName;
	}
	public String getFieldName() {
		return fieldName;
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
	
	
}
