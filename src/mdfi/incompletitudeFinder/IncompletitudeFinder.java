package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mbfi.focalizedExtractor.DateManipulator;
import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mbfi.focalizedExtractor.FocalizedExtractor;
import mdfi.conditions.Atom;
import mdfi.conditions.BinaryFormula;
import mdfi.conditions.Formula;
import mdfi.conditions.NegativeFormula;
import mdfi.conditions.rightHandedSide.BinaryRightHandSide;
import mdfi.conditions.rightHandedSide.NestedQuery;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.database.DatabaseHandler;
import mdfi.query.*;

public class IncompletitudeFinder {

	String configFilePath;
	double minimumHitRadio;
	
	static Logger log = Logger.getLogger(IncompletitudeFinder.class.getName());
	
	public void processQuery (Query query){
		
		boolean foundValues = true;
		// Hallar incompletitudes.
		
		// Para cada atributo, armar contexto, extracción focalizada, insertar.
		
		do {
			foundValues = false;
			List<Attribute> conditionAttributes = query.getConditionAttributes();
			
			for (Iterator <Attribute> iterator = conditionAttributes.iterator(); iterator
					.hasNext();) {
				Attribute attribute = iterator.next();
				foundValues = foundValues || processAttribute(query,attribute);
			}
			
			List<Attribute> requiredAttributes = query.getQualifierAttributes();
			
			for (Iterator <Attribute> iterator = requiredAttributes.iterator(); iterator
			.hasNext();) {
				Attribute attribute = iterator.next();
				foundValues = foundValues || processAttribute(query,attribute);
			}
			
		}while (foundValues);
		
	}
	
	public boolean processAttribute (Query query,Attribute attribute){
		boolean foundValues;
		
		ExtractionContext extContext = buildExtContext(query, attribute);
		
		FocalizedExtractor focalizedExtractor = new FocalizedExtractor(getConfigFilePath(), 
																	   extContext, 
																	   getMinimumHitRadio());
		List<FieldValue> values = focalizedExtractor.findFieldValue(attribute.getConcept());
		foundValues = (values.size()>0);
		
		for (Iterator <FieldValue> iterator = values.iterator(); iterator.hasNext();) {
			FieldValue fieldValue = iterator.next();
			DatabaseHandler.insertValuesIntoOntology(attribute.getConcept(), 
													 attribute.getName(), 
													 fieldValue);
		}
		
		return foundValues;
	}
	
	private ExtractionContext buildExtContext (Query query,Attribute attribute){
		List<FieldInformation> fieldsInformation = new ArrayList<FieldInformation>();
		 
		Query flattenedQuery = queryFlattener.flattenQuery(query);
		
		Formula condition = flattenedQuery.getCondition();
		
		fieldsInformation.addAll(getConditionFieldInformation(condition));
	
		return new ExtractionContext(fieldsInformation);
	}

	private Collection<FieldInformation> getConditionFieldInformation(
			Formula condition) {
		// TODO Auto-generated method stub
		return null;
	}

	

	public String getConfigFilePath() {
		return configFilePath;
	}

	public double getMinimumHitRadio() {
		return minimumHitRadio;
	}
	
	
}
