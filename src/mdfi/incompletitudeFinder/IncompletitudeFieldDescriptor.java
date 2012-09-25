package mdfi.incompletitudeFinder;

import java.util.List;

import mbfi.focalizedExtractor.FieldValue;
import mdfi.query.Attribute;

public class IncompletitudeFieldDescriptor {

	public static final int CONTINUOUS =0;
	public static final int DISCRETE =1 ;
	
	Attribute at;
	String fieldInformationName;
	int type;
	int domainType;
	List<FieldValue>possibleValues;
	
	String increment;
	
	String implicitDomainMinimumValue;
	String implicitDomainMaxmumValue;
	
	boolean generateValues;
	public IncompletitudeFieldDescriptor(String conceptName, String fieldName,
			int type, int domainType, List<FieldValue> possibleValues,
			boolean generateValues, String fieldInformationName,
			String implicitDomainMinimumValue, String implicitDomainMaxmumValue,
			String increment) {
		super();
		
		at = new Attribute(fieldName, conceptName);
		this.type = type;
		this.domainType = domainType;
		this.possibleValues = possibleValues;
		this.generateValues = generateValues;
		this.fieldInformationName = fieldInformationName;
		this.implicitDomainMinimumValue = implicitDomainMinimumValue;
		this.implicitDomainMaxmumValue = implicitDomainMaxmumValue;
		this.increment = increment;
	}
	
	public String getConceptName() {
		return getAttribute().getConcept();
	}
	public String getFieldName() {
		return getAttribute().getName();
	}
	public int getType() {
		return type;
	}
	public int getDomainType() {
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
												   getFieldInformationName(),
												   getImplicitDomainMinimumValue(),
												   getImplicitDomainMaxmumValue(),
												   getIncrement());
	}

	public String getFieldInformationName() {
		return fieldInformationName;
	}

	public String getImplicitDomainMinimumValue() {
		return implicitDomainMinimumValue;
	}

	public String getImplicitDomainMaxmumValue() {
		return implicitDomainMaxmumValue;
	}

	public String getIncrement() {
		return increment;
	}
	
	
}
