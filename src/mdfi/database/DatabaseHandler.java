package mdfi.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.conditions.rightHandedSide.BinaryRightHandSide;
import mdfi.incompletitudeFinder.ExtractionContextBuilder;
import mdfi.incompletitudeFinder.QueryFlattener;
import mdfi.query.AggregateFunction;
import mdfi.query.Attribute;
import mdfi.query.Query;

/*
 * Esta clase sirve de adaptador entre la base de datos o el componente de software 
 * que gestione la Base de Datos y el MDFI.
 */
public class DatabaseHandler {

	static Logger log = Logger.getLogger(DatabaseHandler.class.getName());
	
	public static final String NULL = "NULL";

	public static final String SUM = "SUM";
	public static final String AVG = "AVG";
	public static final String MAX = "MAX";
	public static final String MIN = "MIN";
	
	private List<DatabaseUnit> databaseUnits;
	private Attribute primaryKey;
	
	public DatabaseHandler (String databaseFile){
		DatabaseXMLReader reader = new DatabaseXMLReader(databaseFile);
		primaryKey = reader.getPrimaryKey();
		databaseUnits = reader.getDatabaseUnits();	
		
//		for (Iterator <DatabaseUnit> iterator = databaseUnits.iterator(); iterator.hasNext();) {
//			DatabaseUnit dUnit = iterator.next();
//			
//			System.out.println("\nConcept\n\n");
//			List<FieldValue> fValues = dUnit.getValues().get(0).getFieldValues();
//			
//			for (Iterator iterator2 = fValues.iterator(); iterator2.hasNext();) {
//				FieldValue fieldValue = (FieldValue) iterator2.next();
//				
//				System.out.println(fieldValue.getTextValue());
//			}
//		}
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
		
		
		if (query.hasAggregateFunction()){
			return processAggregate(results,
					query.getAggregateFunction().get(0).getAggregateFunction());
		} else{
			return results;
		}
		
		
	}
	
	private List<DatabaseResult> processAggregate(List<DatabaseResult> input,
			int aggregateFunction) {
		
		// To be implemented.
		List<DatabaseResult> results = new ArrayList<DatabaseResult>();
		
		if (aggregateFunction==AggregateFunction.AVG){
			DatabaseResult sum = processAggregate(input, aggregateFunction).get(0);
			
			return divideResult(sum,input.size());
			
		} 
		
		DatabaseResult aggregate = null;
		
		for (Iterator <DatabaseResult> iterator = input.iterator(); iterator.hasNext();) {
			DatabaseResult result = (DatabaseResult) iterator.next();
			
			
			switch (aggregateFunction) {
			case AggregateFunction.SUM:
				aggregate = applySum(aggregate,result);
				break;
		
			case AggregateFunction.MAX:
				aggregate = applyMax(aggregate,result);
				break;	
				
			case AggregateFunction.MIN:
				aggregate = applyMin(aggregate,result);
				break;					
			default:
				break;
			}
			
		}
		
		results.add(aggregate);
		return results;
	}
	
	private DatabaseResult applyMin(DatabaseResult aggregate,
			DatabaseResult result) {
		
		if (aggregate==null){
			return result;
		}
		
		FieldValue op1 = aggregate.getResults().get(0).getFieldValues().get(0);
		FieldValue op2 = result.getResults().get(0).getFieldValues().get(0);
		
		FieldValue val;
		
		if (QueryEvaluator.testLessThan(op1, op2)){
			val = op1;
		}else{
			val = op2;
		}
		
		List<FieldInformation> results = new ArrayList<FieldInformation>();
		List<FieldValue> values = new ArrayList<FieldValue>();
		values.add(val);
		
		results.add(new FieldInformation(aggregate.getResults().get(0).getFieldName(), values));
		
		return new DatabaseResult(results);
		
	}

	private DatabaseResult applyMax(DatabaseResult aggregate,
			DatabaseResult result) {
		
		if (aggregate==null){
			return result;
		}
		
		FieldValue op1 = aggregate.getResults().get(0).getFieldValues().get(0);
		FieldValue op2 = result.getResults().get(0).getFieldValues().get(0);
		
		FieldValue val;
		
		if (QueryEvaluator.testGreaterThan(op1, op2)){		
			val = op1;
		}else{
			val = op2;
		}
		
		List<FieldInformation> results = new ArrayList<FieldInformation>();
		List<FieldValue> values = new ArrayList<FieldValue>();
		values.add(val);
		
		results.add(new FieldInformation(aggregate.getResults().get(0).getFieldName(), values));
		
		return new DatabaseResult(results);
		
	}

	private DatabaseResult applySum(DatabaseResult aggregate,
			DatabaseResult result) {
		
		if (aggregate==null){
			return result;
		}
		
		FieldValue op1 = aggregate.getResults().get(0).getFieldValues().get(0);
		FieldValue op2 = result.getResults().get(0).getFieldValues().get(0);
		
		FieldValue val = QueryFlattener.operateBRHS(op1,op2,BinaryRightHandSide.OP_SUM).getValues().get(0);
		
		List<FieldInformation> results = new ArrayList<FieldInformation>();
		List<FieldValue> values = new ArrayList<FieldValue>();
		values.add(val);
		
		results.add(new FieldInformation(aggregate.getResults().get(0).getFieldName(), values));
		
		return new DatabaseResult(results);
		
	}

	private List<DatabaseResult> divideResult(DatabaseResult sum, int size) {
		
		FieldValue op1 = sum.getResults().get(0).getFieldValues().get(0);
		String rep = Integer.toString(size)+ ".0";
		
		FieldValue op2 = new FieldValue(rep, FieldDescriptor.DOUBLE);
		
		FieldValue val = QueryFlattener.operateBRHS(op1,op2,BinaryRightHandSide.OP_DIVIDED).getValues().get(0);
		
		List<FieldInformation> results = new ArrayList<FieldInformation>();
		List<FieldValue> values = new ArrayList<FieldValue>();
		values.add(val);
		
		results.add(new FieldInformation(SUM, values));
		
		List<DatabaseResult> resultsDef = new ArrayList<DatabaseResult>();
		
		resultsDef.add(new DatabaseResult(results));
		
		return resultsDef;
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
