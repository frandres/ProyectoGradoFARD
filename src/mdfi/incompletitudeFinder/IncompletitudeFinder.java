package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mbfi.focalizedExtractor.FocalizedExtractor;
import mdfi.conditions.Atom;
import mdfi.conditions.Formula;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.database.DatabaseHandler;
import mdfi.database.DatabaseResult;
import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Logger;

import common.FocalizedExtractorResult;

public class IncompletitudeFinder {

	String configFilePath;
	Configuration configuration;
	DatabaseHandler dbHandler;
	
//	private List<FieldInformation> extractionCache;
	
	static Logger log = Logger.getLogger(IncompletitudeFinder.class.getName());
	
	
	public IncompletitudeFinder(String configFilePath, DatabaseHandler dbHandler) {
		super();
		this.configFilePath = configFilePath;
//		XMLReader reader = new XMLReader(configFilePath);
		
		this.configuration = new Configuration(configFilePath);
//		this.extractionCache = new ArrayList<FieldInformation>();
		this.dbHandler = dbHandler;
	}


	private boolean processConditionAttributes (Query query){
		boolean foundValues = false;
		
		System.out.println("Processing condition attributes.");

		if (query.getConditionAttributes()== null || 
		    query.getConditionAttributes().size()==0){
			return false;
		}
			
		List<Attribute> conditionAttributes = query.getConditionAttributes();
		
		for (Iterator <Attribute> iterator = conditionAttributes.iterator(); iterator
				.hasNext();) {
			Attribute attribute = iterator.next();
			foundValues = processAttribute(query,attribute) || foundValues;
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
			foundValues = processAttribute(query,attribute) || foundValues;
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
			
			System.out.println("Iteration");
			// Las funciones agregadas ya se manejan en processRequiredAttributes.
			
			foundValues = foundValues || processRequiredAttributes(query);
//			System.out.println("Processed required attributes.");
			foundValues = processConditionAttributes(query) || foundValues;
			foundValues = processNestedQueries(query) || foundValues;
			foundValues = processAggregates(query) || foundValues;
			
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
			foundValues = processQuery(nQuery) || foundValues;
		}
		return foundValues;
	}


	public boolean processAttribute (Query query, Attribute attribute){
		boolean foundValues;
		
//		System.out.println("At: " + attribute);
		
		QueryFlattener flatter = new QueryFlattener(getDbHandler());
		
		Query flattenedQuery = flatter.flattenQuery(query,attribute);
		
		if (queryIsNull(flattenedQuery)){
			return false;
		}else{
		}
		
		ExtractionContextBuilder builder = new ExtractionContextBuilder(flattenedQuery, attribute,getConfiguration(),getDbHandler());
		
		ExtractionContext extContext = builder.buildExtContext();
		
		FocalizedExtractor focalizedExtractor = new FocalizedExtractor(getExtractorConfigFile(), 
				   extContext, 
				   getMinimumHitRadio());
		
//		System.out.println("Focalized Extractor initialized");
		
//		FieldInformation fInfo = extContext.getFieldInformationByName(getDbHandler().getPrimaryKey().getIdentifier()); 
		
//		if (fInfo!=null){
//			primaryKeyValue = fInfo.getFieldValues().get(0);
//		}
//		
//		if (primaryKeyValue ==null){
//			
//			List<FocalizedExtractorResult> pKValues = focalizedExtractor.findFieldValue(getDbHandler().getPrimaryKey().getIdentifier()); 
//		
//			if (pKValues != null &&
//				pKValues.size()==1){
//				primaryKeyValue = pKValues.get(0);
//			}
//			  
//		}
			
		List<FocalizedExtractorResult> values = focalizedExtractor.findFieldValue(attribute.getIdentifier());
		
//		System.out.println("Finished searching");
		
		foundValues = false;
		
		for (Iterator <FocalizedExtractorResult> iterator = values.iterator(); iterator.hasNext();) {
				
				FocalizedExtractorResult fExtractorResult = iterator.next();
				
				if (!isPresentInDB(attribute,
								   fExtractorResult.getAttributeValue(),
								   getDbHandler().getPrimaryKey(),
								   fExtractorResult.getPrimaryKeyValue())){
					
//					foundValues = true;
					
					System.out.println("Inserting: "+ fExtractorResult.getPrimaryKeyValue().getTextValue() + 
									   " con valor "+ fExtractorResult.getAttributeValue().getTextValue());
					
					foundValues = getDbHandler().insertValuesIntoOntology(attribute.getConcept(),
															fExtractorResult.getPrimaryKeyValue(),
															attribute.getIdentifier(), 
															fExtractorResult.getAttributeValue()) || foundValues;
					
//					foundValues = true;
				}
				
		}
		return foundValues;
//		return insertIntoCache(values,attribute);//insertIntoCache(values,attribute);
		 
	}
	
	

//	private boolean insertIntoCache(List<FieldValue> fValues, 
//									Attribute at) {
//		
//		List<FieldValue> existingValues = findFieldInformationInCache(at).getFieldValues();
//		return mergeCache(existingValues,fValues);
//		 
//	}
//
//	private boolean mergeCache(List<FieldValue> existingValues,
//			List<FieldValue> newValues) {
//		
//		boolean isThere;
//		boolean thereWasANewOne = false;
//		
//		for (Iterator <FieldValue> iterator = newValues.iterator(); iterator.hasNext();) {
//			FieldValue nFieldValue = (FieldValue) iterator.next();
//			
//			isThere = false;
//			
//			for (Iterator <FieldValue> iterator2 = existingValues.iterator(); iterator2.hasNext();) {
//				FieldValue eFieldValue = (FieldValue) iterator2.next();
//				
//				if (nFieldValue.equals(eFieldValue)){
//					isThere = true;
//				}
//			}
//			
//			if (!isThere){
//				thereWasANewOne = true;
//				existingValues.add(nFieldValue);
//			}
//			
//		}
//		
//		return thereWasANewOne;
//	}
//
//
//	private FieldInformation findFieldInformationInCache(Attribute at){
//		
//		for (Iterator <FieldInformation> iterator = extractionCache.iterator(); iterator.hasNext();) {
//			FieldInformation fInfo = (FieldInformation) iterator.next();
//			
//			if (fInfo.getFieldName().compareTo(at.getIdentifier())==0){
//				return fInfo;
//			}
//		}
//		
//		return null;
//	}
//	
	private boolean isPresentInDB(Attribute atValue, FieldValue fieldValue, Attribute pKey,
			FieldValue primaryKeyValue) {
		
		List<Attribute> requestedAttributes = new ArrayList<Attribute>();
		requestedAttributes.add(atValue);
		
		List<FieldValue> fValues = new ArrayList<FieldValue>();
		fValues.add(primaryKeyValue);
		
		RightHandSide rhs = new SimpleValue(fValues);
		Atom condition = new Atom(rhs, pKey, Atom.OP_EQUALS);
		
		Query query = new Query(null, requestedAttributes, condition);
		
//		System.out.println(query);
		
		List<DatabaseResult> results = getDbHandler().getQueryResult(query);

		if(results.size()==0 ||
		   results.get(0).getResults().size()==0 || 
		   results.get(0).getResults().get(0).getFieldValues().size()==0 ||
		   results.get(0).getResults().get(0).getFieldValues().get(0) == null ||
		   results.get(0).getResults().get(0).getFieldValues().get(0).getTextValue()== null ||
		   results.get(0).getResults().get(0).getFieldValues().get(0).getTextValue()== ""){
//			System.out.println("NO VALUES");
		}
		else{		

			for (Iterator iterator = results.iterator(); iterator.hasNext();) {
				DatabaseResult databaseResult = (DatabaseResult) iterator.next();
				List<FieldInformation> fInfos = databaseResult.getResults();
				
				for (Iterator iterator2 = fInfos.iterator(); iterator2.hasNext();) {
					FieldInformation fieldInformation = (FieldInformation) iterator2
							.next();
					List<FieldValue> rfValues = fieldInformation.getFieldValues();
					
					for (Iterator iterator3 = rfValues.iterator(); iterator3
							.hasNext();) {
						
						FieldValue queryResultFieldValue = (FieldValue) iterator3.next();
//						System.out.println("QUERY RESULT FIELD VALUE: " + queryResultFieldValue.getTextValue());
						if (queryResultFieldValue.equals(fieldValue)){
							return true;
						}
					}
				}
			}
		}
		return false;
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
