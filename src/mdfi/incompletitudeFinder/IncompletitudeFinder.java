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
	Configuration configuration;
	DatabaseHandler dbHandler;
	
	static Logger log = Logger.getLogger(IncompletitudeFinder.class.getName());
	
	
	public IncompletitudeFinder(String configFilePath, double minimumHitRadio,
			Configuration configuration,DatabaseHandler dbHandler) {
		super();
		this.configFilePath = configFilePath;
		this.minimumHitRadio = minimumHitRadio;
		this.configuration = configuration;
		this.dbHandler = dbHandler;
	}


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
		
		QueryFlattener flatter = new QueryFlattener(getDbHandler());
		
		Query flattenedQuery = flatter.flattenQuery(query,attribute);
		
		ExtractionContextBuilder builder = new ExtractionContextBuilder(flattenedQuery, attribute,getConfiguration(),getDbHandler());
		
		ExtractionContext extContext = builder.buildExtContext();
		
		FieldValue primaryKeyValue = extContext.getFieldInformationByName(getDbHandler().getPrimaryKey().getName()).getFieldValues().get(0);
		
		FocalizedExtractor focalizedExtractor = new FocalizedExtractor(getConfigFilePath(), 
																	   extContext, 
																	   getMinimumHitRadio());
		List<FieldValue> values = focalizedExtractor.findFieldValue(attribute.getConcept());
		
		foundValues = (values.size()>0);

		for (Iterator <FieldValue> iterator = values.iterator(); iterator.hasNext();) {
			FieldValue fieldValue = iterator.next();
			getDbHandler().insertValuesIntoOntology(attribute.getConcept(),
													primaryKeyValue,
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


	public Configuration getConfiguration() {
		return configuration;
	}


	private DatabaseHandler getDbHandler() {
		return dbHandler;
	}
	
	
	
}
