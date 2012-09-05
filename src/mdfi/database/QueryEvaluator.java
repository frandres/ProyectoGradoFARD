package mdfi.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.conditions.Atom;
import mdfi.conditions.BinaryFormula;
import mdfi.conditions.Formula;
import mdfi.conditions.NegativeFormula;
import mdfi.conditions.NullCondition;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.DateManipulator;

public class QueryEvaluator {

	static Logger log = Logger.getLogger(QueryEvaluator.class.getName());
	
	private Query query;
	private List<DatabaseUnit> databaseUnits;
	
	public QueryEvaluator(Query query, List<DatabaseUnit> dbUnits) {
		super();
		this.query = query;
		this.databaseUnits = new ArrayList<DatabaseUnit>();
		
		for (Iterator <DatabaseUnit> iterator = dbUnits.iterator(); iterator.hasNext();) {
			DatabaseUnit databaseUnit = iterator.next();
			
			this.databaseUnits.add(databaseUnit);
			
		}
	}
	
	public List<DatabaseUnit> evaluateQuery(){
		
		List<DatabaseUnit> results = new ArrayList<DatabaseUnit>();
		
		List<Formula> conditions = query.getCondition().toNCF();
		
		boolean admitted;
		
		for (Iterator <DatabaseUnit> iterator = databaseUnits.iterator(); iterator.hasNext();) {
			admitted = true;
			DatabaseUnit unit = iterator.next();
			
			for (Iterator <Formula> iterator2 = conditions.iterator(); iterator2
					.hasNext() && admitted;) {
				
				Formula formula = iterator2.next();
				admitted = admitted && evaluateCondition(formula,unit);
				
			}
			
			
			if (admitted){
				results.add(unit);
			}
		}
			
		return results;
		
	}

	
	private boolean evaluateCondition(Formula condition, DatabaseUnit unit) {
		
		if (condition == null){
			log.log(Level.WARN, "Null Condition: ");
			return false;
		}
		if (condition instanceof BinaryFormula){
			BinaryFormula binCondition = (BinaryFormula) condition;

			return evaluateCondition(binCondition.getLeftSide(),unit) &&
				   evaluateCondition(binCondition.getRightSide(),unit) ;
		}
		
		if (condition instanceof NegativeFormula){
			NegativeFormula negCondition = (NegativeFormula) condition;
			
			return !evaluateCondition(negCondition,unit);
		}
		
		if (condition instanceof Atom){
			Atom atom = (Atom) condition;
			//log.log(Level.TRACE, atom.getConditionText(false));
			
			return evaluateAtom(atom,unit);
		}
		
		if (condition instanceof NullCondition){

			return true;
		}
		
		log.log(Level.WARN, "Condition: " + condition.toString() + "no tiene tipo reconocido");
		return false;
	}

	private boolean evaluateAtom(Atom atom, DatabaseUnit unit) {
		
		FieldValue leftHandValue = resolveLeftHandValue(atom.getAttributes().get(0),unit);

		
		RightHandSide rhs = atom.getRhs();
		
		if (!(rhs instanceof SimpleValue)){
			log.log(Level.ERROR,"RHS not instance of SimpleValue");
			return false;
		}
		
		SimpleValue sValue = (SimpleValue) rhs;
		
		List<FieldValue> rightHandValues = sValue.getValues();
		
		for (Iterator <FieldValue> iterator = rightHandValues.iterator(); iterator.hasNext();) {
			FieldValue righthandValue = iterator.next();
			if (evaluate(leftHandValue,righthandValue,atom.getComparationOperation())){
				
				return true;
			}
		}
		
		return false;
		
	}

	private boolean evaluate(FieldValue leftHandValue,
			FieldValue righthandValue, int comparationOperation) {
		
		if (verifyTypeCompability(leftHandValue.getType(),righthandValue.getType(),comparationOperation)){
			
			return evaluateWithoutTypeChecks(leftHandValue,righthandValue,comparationOperation);
		} else{
			log.log(Level.ERROR, "Trying to operate 2 values of incompatible types: " 
					  + leftHandValue.getType() + " and " + righthandValue.getType());
			return false;
		}
	}

	private boolean evaluateWithoutTypeChecks(FieldValue leftHandValue,
			FieldValue righthandValue, int comparationOperation) {
		
		switch (comparationOperation) {
		case Atom.OP_EQUALS:
			
			return testEquality(leftHandValue,righthandValue);
		case Atom.OP_DIFFERENT_THAN:
			return !testEquality(leftHandValue,righthandValue);
		case Atom.OP_LESS_THAN:
			return testLessThan(leftHandValue,righthandValue);
		case Atom.OP_GREATER_THAN:
			return testGreaterThan(leftHandValue,righthandValue);
		case Atom.OP_LESS_OR_EQUAL:	
			return testEquality(leftHandValue,righthandValue)||
				   testLessThan(leftHandValue,righthandValue);
		case Atom.OP_GREATER_OR_EQUAL:
			return testEquality(leftHandValue,righthandValue)||
			   testGreaterThan(leftHandValue,righthandValue);
		case Atom.OP_IN:
			return testEquality(leftHandValue,righthandValue);
		default:
			break;
		}
		return false;
	}

	public static boolean testEquality(FieldValue op1, FieldValue op2) {
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

	public static boolean testLessThan(FieldValue op1, FieldValue op2) {
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

	public static boolean testGreaterThan(FieldValue op1, FieldValue op2) {
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

	public static boolean verifyTypeCompability(int type, int type2,
			int comparationOperation) {
		
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

	private FieldValue resolveLeftHandValue(Attribute attribute,
			DatabaseUnit unit) {
		
		
		return unit.getFieldValueByFieldName(attribute.getIdentifier());
	}
	
	
	
}
