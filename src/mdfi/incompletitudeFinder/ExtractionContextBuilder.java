package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.DateManipulator;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.conditions.Atom;
import mdfi.conditions.Formula;
import mdfi.conditions.rightHandedSide.BinaryRightHandSide;
import mdfi.conditions.rightHandedSide.NestedQuery;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.database.DatabaseHandler;
import mdfi.database.DatabaseResult;
import mdfi.query.Attribute;
import mdfi.query.Query;

public class ExtractionContextBuilder {
	
	static Logger log = Logger.getLogger(ExtractionContextBuilder.class.getName());
	
	Query query;
	Attribute attribute;
	
	public ExtractionContextBuilder(Query query, Attribute attribute) {
		this.query= query;
		this.attribute= attribute;
	}

	private List<FieldInformation> getPrimaryKeyContext(){
		List<Attribute> attributes = DatabaseHandler.getPrimaryKey(attribute.getConcept());
		attributes.add(attribute);
		
		int numAttributes = attributes.size(); 
		Query incompletitudeFinderQuery = query.clone();
		incompletitudeFinderQuery.setRequestedAttributes(attributes);
		List<DatabaseResult> queryResults = DatabaseHandler.getQueryResult(incompletitudeFinderQuery);
		
		List<FieldInformation> primaryKeyContext = new ArrayList<FieldInformation>(numAttributes-1);
		
		for (Iterator <DatabaseResult> iterator = queryResults.iterator(); iterator.hasNext();) {
			DatabaseResult databaseResult = (DatabaseResult) iterator.next();
			List<FieldInformation> results = databaseResult.getResults();
			if (results.get(numAttributes-1).getFieldValues().get(0).getStringValue()==DatabaseHandler.NULL){
				int cont = 0;
				for (Iterator<FieldInformation> iterator2 = primaryKeyContext.iterator(); iterator2
						.hasNext();) {
					FieldInformation fieldInformation = iterator2.next();
					fieldInformation.addValue(results.get(cont++).getFieldValues().get(0));
					
				}
			}
		}
		return primaryKeyContext;
		
	}
	
	public ExtractionContext buildExtContext (){
		
		List<FieldInformation> primaryKeyInformation =  getPrimaryKeyContext();
		List<FieldInformation> conditionFieldInformation =  getConditionFieldInformation();
		
		primaryKeyInformation.addAll(conditionFieldInformation);
		return new ExtractionContext(primaryKeyInformation);
	}

	private List<FieldInformation> getConditionFieldInformation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<SimpleValue> filterByCondition(Atom condition, List<SimpleValue> possibleValues){
		for (Iterator <SimpleValue> iterator = possibleValues.iterator(); iterator.hasNext();) {
			SimpleValue sValue = iterator.next();
			if (conditionFails(sValue.getValues().get(0),condition)){
				iterator.remove();
			}
			
		}
		
	}

	private boolean conditionFails(FieldValue fieldValue, Atom condition) {
		
		RightHandSide rhs = condition.getRhs();
		
		if (rhs instanceof NestedQuery || rhs instanceof BinaryRightHandSide){
			log.log(Level.ERROR, "Received non flattened query");
		}
		SimpleValue rhsSV = (SimpleValue) rhs;
		FieldValue rhsFieldValue = rhsSV.getValues().get(0);
		if (!verifyTypeCompability(rhsSV.getValues().get(0).getType(), fieldValue.getType())){
			log.log(Level.WARN, "Attempting to compare values of different types");
			return false;
		}
		switch (condition.getComparationOperation()) {
		case Atom.OP_EQUALS:
			return testEquality(fieldValue,rhsFieldValue);
		case Atom.OP_DIFFERENT_THAN:
			return !testEquality(fieldValue,rhsFieldValue);
		case Atom.OP_LESS_THAN:
			return testLessThan(fieldValue,rhsFieldValue);
		case Atom.OP_GREATER_THAN:
			return testGreaterThan(fieldValue,rhsFieldValue);
		case Atom.OP_LESS_OR_EQUAL:	
			return testEquality(fieldValue,rhsFieldValue)||
				   testLessThan(fieldValue,rhsFieldValue);
		case Atom.OP_GREATER_OR_EQUAL:
			return testEquality(fieldValue,rhsFieldValue)||
			   testGreaterThan(fieldValue,rhsFieldValue);
		case Atom.OP_IN:
			for (Iterator <FieldValue> iterator = rhsSV.getValues().iterator(); iterator.hasNext();) {
				FieldValue fValue = iterator.next();
				if (testEquality(fieldValue,fValue)){
					return true;
				}		
			}
		default:
			break;
		}
		
		log.log(Level.ERROR, "Error: undefined operator");
		return false;
	}
	
	private boolean testEquality(FieldValue op1, FieldValue op2) {
		switch (op1.getType()) {
		case FieldDescriptor.STRING:
			return (op1.getStringValue().compareTo(op2.getStringValue())==0);

		case FieldDescriptor.INTEGER:			
			return (op1.getIntValue() == op2.getIntValue());
			
		case FieldDescriptor.DOUBLE:
			
			return (op1.getDouble() == op2.getDouble());
			
		case FieldDescriptor.BOOLEAN:
			return (op1.getBooleanValue() == op2.getBooleanValue());
			
		case FieldDescriptor.DATE:
			
			DateManipulator dMan = new DateManipulator(op1.getTextValue());
			Date date1 = dMan.getDate();
			
			dMan = new DateManipulator(op1.getTextValue());
			Date date2 = dMan.getDate();
			
			return (date1.compareTo(date2) ==0);
		default:
			break;
		}
		return false;
	}

	private boolean testLessThan(FieldValue op1, FieldValue op2) {
		switch (op1.getType()) {
		case FieldDescriptor.STRING:
			return (op1.getStringValue().compareTo(op2.getStringValue())<0);

		case FieldDescriptor.INTEGER:			
			return (op1.getIntValue() < op2.getIntValue());
			
		case FieldDescriptor.DOUBLE:
			
			return (op1.getDouble() < op2.getDouble());
			
		case FieldDescriptor.BOOLEAN:
			break;
			
		case FieldDescriptor.DATE:
			
			DateManipulator dMan = new DateManipulator(op1.getTextValue());
			Date date1 = dMan.getDate();
			
			dMan = new DateManipulator(op1.getTextValue());
			Date date2 = dMan.getDate();
			
			return (date1.compareTo(date2) < 0);
		default:
			break;
		}
		return false;
	}

	private boolean testGreaterThan(FieldValue op1, FieldValue op2) {
		switch (op1.getType()) {
		case FieldDescriptor.STRING:
			return (op1.getStringValue().compareTo(op2.getStringValue())>0);

		case FieldDescriptor.INTEGER:			
			return (op1.getIntValue() > op2.getIntValue());
			
		case FieldDescriptor.DOUBLE:
			
			return (op1.getDouble() > op2.getDouble());
			
		case FieldDescriptor.BOOLEAN:
			break;
			
		case FieldDescriptor.DATE:
			
			DateManipulator dMan = new DateManipulator(op1.getTextValue());
			Date date1 = dMan.getDate();
			
			dMan = new DateManipulator(op1.getTextValue());
			Date date2 = dMan.getDate();
			
			return (date1.compareTo(date2) >0);
		default:
			break;
		}
		return false;
	}

	private boolean verifyTypeCompability(int type, int type2) {
		
		if (type2==FieldDescriptor.DATE){
			return false;
		}
		
		switch (type) {
		case FieldDescriptor.STRING:
			return (type2 == FieldDescriptor.STRING);

		case FieldDescriptor.INTEGER:
			return (type2 == FieldDescriptor.INTEGER || type2 == FieldDescriptor.DOUBLE);
			
		case FieldDescriptor.DOUBLE:
			return (type2 == FieldDescriptor.INTEGER || type2 == FieldDescriptor.DOUBLE);
			
		case FieldDescriptor.BOOLEAN:
			return (type2 == FieldDescriptor.BOOLEAN);
			
		case FieldDescriptor.DATE:
			return (type2 == FieldDescriptor.DATE);	
		default:
			break;
		}
		log.log(Level.WARN, "TYPE: " + type + "no tiene tipo reconocido");
		return false;
	}
}
