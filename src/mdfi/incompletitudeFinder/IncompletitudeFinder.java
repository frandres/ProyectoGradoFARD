package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mbfi.focalizedExtractor.FocalizedExtractor;
import mdfi.conditions.Formula;
import mdfi.database.DatabaseHandler;
import mdfi.database.DatabaseResult;
import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Logger;

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
		
		Query flattenedQuery = QueryFlattener.flattenQuery(query,attribute);
		
		ExtractionContextBuilder builder = new ExtractionContextBuilder(flattenedQuery, attribute);
		
		ExtractionContext extContext = builder.buildExtContext();
		
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
	
	

	public String getConfigFilePath() {
		return configFilePath;
	}

	public double getMinimumHitRadio() {
		return minimumHitRadio;
	}
	
	
}
