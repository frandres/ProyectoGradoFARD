package mdfi.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javassist.bytecode.FieldInfo;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.incompletitudeFinder.ExtractionContextBuilder;
import mdfi.incompletitudeFinder.QueryFlattener;
import mdfi.query.Attribute;
import mdfi.query.Query;

/*
 * Esta clase sirve de adaptador entre la base de datos o el componente de software 
 * que gestione la Base de Datos y el MDFI.
 */
public class DatabaseHandler {

	static Logger log = Logger.getLogger(DatabaseHandler.class.getName());
	
	public static final String NULL = "NULL";

	private List<DatabaseUnit> databaseUnits;
	private Attribute primaryKey;
	
	public DatabaseHandler (String databaseFile){
		DatabaseXMLReader reader = new DatabaseXMLReader(databaseFile);
		primaryKey = reader.getPrimaryKey();
		databaseUnits = reader.getDatabaseUnits();		
	}
	
	
	public Attribute getPrimaryKey (){
		return primaryKey;
	}
	
	public List<FieldValue> getQuerySingleResult (Query query){
		return getQueryResult (query).get(0).getResults().get(0).getFieldValues();
		
	}
	
	public List<DatabaseResult> getQueryResult (Query query){
		List<DatabaseResult> results = new ArrayList<DatabaseResult>();
		QueryFlattener flatter = new QueryFlattener(this);
		Query wCopy = flatter.flattenQuery(query);
		
		QueryEvaluator evaluator = new QueryEvaluator(wCopy, databaseUnits);
		
		List<DatabaseUnit> units = evaluator.evaluateQuery();
		
		results = getResults (units,query);
		
		return results;
		
	}
	
	private List<DatabaseResult> getResults(List<DatabaseUnit> units,
			Query query) {
		
		List<DatabaseResult> results = new ArrayList<DatabaseResult>();
		
		List<FieldInformation> fieldInfos;
		List<FieldValue> fValues;
		List<Attribute> atts;
		FieldInformation fInfo;
		
		for (Iterator <DatabaseUnit> iterator = units.iterator(); iterator.hasNext();) {
			DatabaseUnit databaseUnit = (DatabaseUnit) iterator.next();
			fieldInfos = new ArrayList<FieldInformation>();
			atts = query.getRequestedAttributes();
			
			for (Iterator <Attribute> iterator2 = atts.iterator(); iterator2.hasNext();) {
				Attribute attribute = iterator2.next();
				fValues = new ArrayList<FieldValue>();
				fValues.add(databaseUnit.getFieldValueByFieldName(attribute.getIdentifier()));
				fInfo = new FieldInformation(attribute.getIdentifier(), fValues);
				
				fieldInfos.add(fInfo);
			}
			DatabaseResult result = new DatabaseResult(fieldInfos);
			results.add(result);
		}
		
		return results;
	}

	public void insertValuesIntoOntology(String conceptName,
										 FieldValue primaryKeyValue,
												String attribute, 
												List<FieldValue> values){
		
		for (Iterator <DatabaseUnit> iterator = getDatabaseUnits().iterator(); iterator.hasNext();) {
			DatabaseUnit dUnit = iterator.next();

			if (QueryEvaluator.testEquality(primaryKeyValue, 
										dUnit.getFieldValueByFieldName(getPrimaryKey().getIdentifier()))){
				FieldInformation fInfo = new FieldInformation(attribute, values);
				dUnit.insertValue(fInfo);
				
				log.log(Level.TRACE, "Inserted value into ontology");
			}
			
		}
	}

	public void insertValuesIntoOntology(String conceptName,
			 FieldValue primaryKeyValue,
					String attribute, 
					FieldValue value){
		List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		fieldValues.add(value);
		insertValuesIntoOntology(conceptName, primaryKeyValue,attribute,fieldValues);
	}
	
	private List<DatabaseUnit> getDatabaseUnits() {
		return databaseUnits;
	}
}
