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
	Configuration configuration;
	DatabaseHandler dbHandler;
	
	static Logger log = Logger.getLogger(IncompletitudeFinder.class.getName());
	
	
	public IncompletitudeFinder(String configFilePath, DatabaseHandler dbHandler) {
		super();
		this.configFilePath = configFilePath;
//		XMLReader reader = new XMLReader(configFilePath);
		
		this.configuration = new Configuration(configFilePath);
		
		this.dbHandler = dbHandler;
	}


	private boolean processConditionAttributes (Query query){
		boolean foundValues = false;
		
		if (query.getConditionAttributes()== null || 
		    query.getConditionAttributes().size()==0){
			return false;
		}
			
		List<Attribute> conditionAttributes = query.getConditionAttributes();
		
		for (Iterator <Attribute> iterator = conditionAttributes.iterator(); iterator
				.hasNext();) {
			Attribute attribute = iterator.next();
			foundValues = foundValues || processAttribute(query,attribute);
		}
		
		return foundValues;
	}
	
	private boolean processRequiredAttributes (Query query){
		boolean foundValues = false;
		
		if (query.getRequestedAttributes()== null || 
			    query.getRequestedAttributes().size()==0){
				return false;
			}
		
		List<Attribute> requiredAttributes = query.getRequestedAttributes();
		
		for (Iterator <Attribute> iterator = requiredAttributes.iterator(); iterator
		.hasNext();) {
			Attribute attribute = iterator.next();
			foundValues = foundValues || processAttribute(query,attribute);
		}
		
		return foundValues;
	}
	
	public boolean processQuery (Query query){
		
		boolean foundValues,foundAtLeastOneValue;
		foundAtLeastOneValue = false;
		// Hallar incompletitudes.
		
		// Para cada atributo, armar contexto, extracción focalizada, insertar.
		
		do {
			foundValues = false;
			
			// Las funciones agregadas ya se manejan en processRequiredAttributes.
			
			foundValues = foundValues || processRequiredAttributes(query);
			foundValues = foundValues || processConditionAttributes(query);
			foundValues = foundValues || processNestedQueries(query);
			foundValues = foundValues || processAggregates(query);
			
			foundAtLeastOneValue = foundAtLeastOneValue || foundValues;
			
		} while (foundValues);
	
		return foundAtLeastOneValue;
	}
	
	
	private boolean processAggregates(Query query) {
		// This function has not been implemented.
		return false;
	}


	private boolean processNestedQueries(Query query) {
		boolean foundValues = false;
		
		List<Query> nestedQueries = query.getNestedQueries();
		
		if (nestedQueries== null || 
				nestedQueries.size()==0){
			return false;
		}
		
		for (Iterator iterator = nestedQueries.iterator(); iterator.hasNext();) {
			Query nQuery = (Query) iterator.next();
			foundValues = foundValues || processQuery(nQuery);
		}
		return foundValues;
	}


	public boolean processAttribute (Query query, Attribute attribute){
		boolean foundValues;
		
		QueryFlattener flatter = new QueryFlattener(getDbHandler());
		
		Query flattenedQuery = flatter.flattenQuery(query,attribute);
		
		if (queryIsNull(flattenedQuery)){
			return false;
		}else{
		}
		
		System.out.println("A:" + attribute);
		
		System.out.println("N: " + query.toString());
		
		System.out.println("F: " + flattenedQuery.toString());
		
		
		ExtractionContextBuilder builder = new ExtractionContextBuilder(flattenedQuery, attribute,getConfiguration(),getDbHandler());
		
		ExtractionContext extContext = builder.buildExtContext();
		
		FieldValue primaryKeyValue = null;
		
		FocalizedExtractor focalizedExtractor = new FocalizedExtractor(getExtractorConfigFile(), 
				   extContext, 
				   getMinimumHitRadio());
		
		FieldInformation fInfo = extContext.getFieldInformationByName(getDbHandler().getPrimaryKey().getIdentifier()); 
		
		if (fInfo!=null){
			primaryKeyValue = fInfo.getFieldValues().get(0);
		}
		
		if (primaryKeyValue ==null){
			List<FieldValue> pKValues = focalizedExtractor.findFieldValue(getDbHandler().getPrimaryKey().getIdentifier()); 
			if (pKValues != null &&
				pKValues.size()==1){
				primaryKeyValue = pKValues.get(0);
			}
			  
		}
			
		
		List<FieldValue> values = focalizedExtractor.findFieldValue(attribute.getIdentifier());
		
		foundValues = (values.size()>0);

		if (primaryKeyValue != null){
			
			for (Iterator <FieldValue> iterator = values.iterator(); iterator.hasNext();) {
				FieldValue fieldValue = iterator.next();
				getDbHandler().insertValuesIntoOntology(attribute.getConcept(),
														primaryKeyValue,
														 attribute.getName(), 
														 fieldValue);
			}
		}
		return foundValues;
	}
	
	

	private boolean queryIsNull(Query query) {
				
		return ((query.getRequestedAttributes() == null || query.getRequestedAttributes().size() ==0)
			  && (query.getQualifierAttributes() == null || query.getQualifierAttributes().size() ==0));
	}


	public String getExtractorConfigFile() {
		return configuration.getExtractorFilePath();
	}

	public double getMinimumHitRadio() {
		return configuration.getMinimumHitRadio();
	}


	public Configuration getConfiguration() {
		return configuration;
	}


	private DatabaseHandler getDbHandler() {
		return dbHandler;
	}
	
	
	
}
