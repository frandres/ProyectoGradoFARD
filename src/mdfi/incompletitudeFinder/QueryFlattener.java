package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.List;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.conditions.Atom;
import mdfi.conditions.BinaryFormula;
import mdfi.conditions.Formula;
import mdfi.conditions.NegativeFormula;
import mdfi.conditions.NullCondition;
import mdfi.conditions.rightHandedSide.BinaryRightHandSide;
import mdfi.conditions.rightHandedSide.NestedQuery;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.database.DatabaseHandler;
import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class QueryFlattener {
	Logger log = Logger.getLogger(QueryFlattener.class.getName());
	DatabaseHandler dbHandler;
	
	public QueryFlattener(DatabaseHandler dbHandler) {
		super();
		this.dbHandler = dbHandler;
	}
	
	public  Query flattenQuery(Query query) {
		return flattenQuery(query,null);
	}
	public  Query flattenQuery(Query query, Attribute at) {
		
		Query workingCopy = query.clone();
		
		if (at!= null){
			workingCopy.removeAttribute(at);
			//System.out.println(workingCopy.toString());
		} else{
			workingCopy.setCondition(workingCopy.getCondition().toNCFSF());
		}
		
		
		Formula condition = workingCopy.getCondition();
		condition = flattenCondition(condition);
		
		Query newQuery = new Query(null, workingCopy.getRequestedAttributes(), condition);
		return newQuery;
	}

	private Formula flattenCondition(Formula condition) {
		
		if (condition == null){
			log.log(Level.WARN, "Null Condition: ");
			return null;
		}
		if (condition instanceof BinaryFormula){
			BinaryFormula binCondition = (BinaryFormula) condition;
			binCondition.setLeftSide(flattenCondition(binCondition.getLeftSide()));
			binCondition.setRightSide(flattenCondition(binCondition.getRightSide()));
			return binCondition;
		}
		
		if (condition instanceof NegativeFormula){
			NegativeFormula negCondition = (NegativeFormula) condition;
			
			Formula newFormula = negCondition.negateCondition();
						
			newFormula = flattenCondition(newFormula);
			return newFormula;
		}
		
		if (condition instanceof Atom){
			Atom atom = (Atom) condition;
			RightHandSide rhs = atom.getRhs();
			atom.setRhs(flattenRHS(rhs));
			
			return atom;
		}
		
		if (condition instanceof NullCondition){

			return condition;
		}
		
		log.log(Level.WARN, "Condition: " + condition.toString() + "no tiene tipo reconocido");
		// TODO Auto-generated method stub
		return null;
	}

	private DatabaseHandler getDatabaseHandler(){
		return dbHandler;
	}
	private SimpleValue flattenRHS(RightHandSide rhs) {
		
		if (rhs instanceof NestedQuery){
			NestedQuery nQuery = (NestedQuery) rhs;
			SimpleValue rhsSV = new SimpleValue(getDatabaseHandler().getQuerySingleResult(nQuery.getNestedQuery()));
			return rhsSV;
		}
		
		if (rhs instanceof BinaryRightHandSide){
			BinaryRightHandSide brhs = (BinaryRightHandSide) rhs;
			
			SimpleValue rhsSV= flattenBRHS(flattenRHS(brhs.getLrhs()),flattenRHS(brhs.getRrhs()),brhs.getOperator());
			return rhsSV;
		}

		if (rhs instanceof SimpleValue){
			return (SimpleValue)rhs;
		}
		
		log.log(Level.WARN, "RHS: " + rhs.toString() + "no tiene tipo reconocido");
		
		return null;
	}

	private SimpleValue flattenBRHS(SimpleValue flattenRHS,
			SimpleValue flattenRHS2,int operation) {
		FieldValue val1 = flattenRHS.getValues().get(0);
		FieldValue val2 = flattenRHS2.getValues().get(0);
		
		if (verifyTypeCompability(val1.getType(),val2.getType(),operation)){
			return operateBRHS(val1,val2,operation);
		}
		else{
			log.log(Level.ERROR, "Trying to operate 2 values of incompatible types: " 
								  + val1.getType() + " and " + val2.getType());
			return null;
		}
		
	}
	
	private SimpleValue operateBRHS (FieldValue op1,
			FieldValue op2,int operation) {
		
		List<FieldValue> values = new ArrayList<FieldValue>();
		SimpleValue simpleValue = new SimpleValue(values);
		
		
		switch (op1.getType()) {
		case FieldDescriptor.STRING:
			values.add(operateStrings(op1, op2, operation));
			break;

		case FieldDescriptor.INTEGER:
			
			if (op2.getType()==FieldDescriptor.DOUBLE){
				values.add(operateIntAndDouble(op1, op2, operation));
			}
			
			if (op2.getType()==FieldDescriptor.INTEGER){
				values.add(operateInts(op1, op2, operation));
			}
			
			break;
			
		case FieldDescriptor.DOUBLE:
			
			if (op2.getType()==FieldDescriptor.DOUBLE){
				values.add(operateDoubles(op1, op2, operation));
			}
			
			if (op2.getType()==FieldDescriptor.INTEGER){
				values.add(operateIntAndDouble(op1, op2, operation));
			}

			break;
			
		case FieldDescriptor.BOOLEAN:
			values.add(operateBooleans(op1, op2, operation));
			break;
			
			
		case FieldDescriptor.DATE:
			log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
			break;				
		default:
			break;
		}
		return simpleValue;
	}
	
	private FieldValue operateInts (FieldValue op1, FieldValue op2, int operation){
		switch (operation) {
		case BinaryRightHandSide.OP_SUM:
			return new FieldValue(Integer.toString(op1.getIntValue()+op2.getIntValue()),FieldDescriptor.INTEGER);
		case BinaryRightHandSide.OP_MINUS:
			return new FieldValue(Integer.toString(op1.getIntValue()-op2.getIntValue()),FieldDescriptor.INTEGER);
		case BinaryRightHandSide.OP_TIMES:
			return new FieldValue(Integer.toString(op1.getIntValue()*op2.getIntValue()),FieldDescriptor.INTEGER);
		case BinaryRightHandSide.OP_DIVIDED:
			return new FieldValue(Integer.toString(op1.getIntValue()/op2.getIntValue()),FieldDescriptor.INTEGER);
		case BinaryRightHandSide.OP_MOD:
			return new FieldValue(Integer.toString(op1.getIntValue()%op2.getIntValue()),FieldDescriptor.INTEGER);
		default:
			break;
		}
		
		log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
		return null;
		
	}
	
	private FieldValue operateDoubles (FieldValue op1, FieldValue op2, int operation){
		switch (operation) {
		case BinaryRightHandSide.OP_SUM:
			return new FieldValue(Double.toString(op1.getDouble()+op2.getDouble()),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_MINUS:
			return new FieldValue(Double.toString(op1.getDouble()-op2.getDouble()),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_TIMES:
			return new FieldValue(Double.toString(op1.getDouble()*op2.getDouble()),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_DIVIDED:
			return new FieldValue(Double.toString(op1.getDouble()/op2.getDouble()),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_MOD:
			return new FieldValue(Double.toString(op1.getDouble()%op2.getDouble()),FieldDescriptor.DOUBLE);
		default:
			break;
		}
		
		log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
		return null;
		
	}
	
	private FieldValue operateIntAndDouble (FieldValue op1, FieldValue op2, int operation){
		
		double a,b;
	
		if (op1.getType()==FieldDescriptor.INTEGER){
			a = (double) op1.getIntValue();
			b = op2.getDouble();
		}else{
			a = op1.getDouble();
			b = (double) op2.getIntValue();
		}
	
		switch (operation) {
		case BinaryRightHandSide.OP_SUM:
			return new FieldValue(Double.toString(a + b),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_MINUS:
			return new FieldValue(Double.toString(a - b),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_TIMES:
			return new FieldValue(Double.toString(a * b),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_DIVIDED:
			return new FieldValue(Double.toString(a / b),FieldDescriptor.DOUBLE);
		case BinaryRightHandSide.OP_MOD:
			return new FieldValue(Double.toString(a % b),FieldDescriptor.DOUBLE);
		default:
			break;
		}
		
		log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
		return null;
		
	}
	
	private FieldValue operateStrings (FieldValue op1, FieldValue op2, int operation){
		switch (operation) {
		case BinaryRightHandSide.OP_SUM:
			return new FieldValue(op1.getStringValue()+op2.getStringValue(), 
									  FieldDescriptor.STRING);
			
		default:
			break;
		}
		
		log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
		return null;
		
	}
	
	private FieldValue operateBooleans (FieldValue op1, FieldValue op2, int operation){
		switch (operation) {
		case BinaryRightHandSide.OP_AND:
			return new FieldValue(Boolean.toString(op1.getBooleanValue() && op2.getBooleanValue()), 
									FieldDescriptor.BOOLEAN);
		case BinaryRightHandSide.OP_OR:
			return new FieldValue(Boolean.toString(op1.getBooleanValue() || op2.getBooleanValue()), 
									FieldDescriptor.BOOLEAN);			
		default:
			
			break;
		}
				
		log.log(Level.ERROR, "Operation: " + operation + " not defined for type:" + op1.getTextValue());
		return null;
		
	}
	
	private boolean verifyTypeCompability(int type, int type2, int operation) {
		
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
			return false;			
		default:
			break;
		}
		log.log(Level.WARN, "TYPE: " + type + "no tiene tipo reconocido");
		return false;
	}
}
