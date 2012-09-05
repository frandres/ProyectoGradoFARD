package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
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
import mdfi.database.DatabaseResult;
import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.DateManipulator;

public class ExtractionContextBuilder {
	
	static Logger log = Logger.getLogger(ExtractionContextBuilder.class.getName());
	
	Query query;
	Attribute attribute;
	Configuration configuration;
	DatabaseHandler dbHandler;
	
	public ExtractionContextBuilder(Query query, 
									Attribute attribute, 
									Configuration configuration,
									DatabaseHandler dbHandler) {
		this.query= query;
		this.attribute= attribute;
		this.configuration = configuration;
		this.dbHandler = dbHandler;
	}

	private List<FieldInformation> getPrimaryKeyContext(){
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(getDBHandler().getPrimaryKey());
		
		
		if (!attribute.equals(getDBHandler().getPrimaryKey())){
			attributes.add(attribute);
		}

		int numAttributes = attributes.size();
		
		Query incompletitudeFinderQuery = query.clone();
		incompletitudeFinderQuery.setRequestedAttributes(attributes);
		List<DatabaseResult> queryResults = getDBHandler().getQueryResult(incompletitudeFinderQuery);
		
		List<FieldInformation> primaryKeyContext = new ArrayList<FieldInformation>(numAttributes-1);
		
		for (Iterator <DatabaseResult> iterator = queryResults.iterator(); iterator.hasNext();) {
			DatabaseResult databaseResult = (DatabaseResult) iterator.next();
			List<FieldInformation> results = databaseResult.getResults(); 
			if (results.get(numAttributes-1).getFieldValues().get(0).getStringValue()!=DatabaseHandler.NULL){
				int cont = 0;
				for (Iterator<FieldInformation> iterator2 = primaryKeyContext.iterator(); iterator2
						.hasNext();) {
					FieldInformation fieldInformation = iterator2.next();
					fieldInformation.addValue(results.get(cont++).getFieldValues().get(0));
					
				}
			}
		}
		
		System.out.println(primaryKeyContext.size());
		return primaryKeyContext;
		
	}
	
	private DatabaseHandler getDBHandler() {
		return dbHandler;
	}

	public ExtractionContext buildExtContext (){
		
		// TODO Aqui está el problema.
		
		List<FieldInformation> primaryKeyInformation =  getPrimaryKeyContext();
		
		List<FieldInformation> conditionFieldInformation =  getConditionFieldInformation();
		List<FieldInformation> fInfo = primaryKeyInformation;
		
		fInfo.addAll(conditionFieldInformation);
		

		return new ExtractionContext(fInfo);
	}

	private List<FieldInformation> getConditionFieldInformation() {

		List<Atom> atoms = getAtoms();
		List<IncompletitudeFieldDescriptor> fDescriptors = getFieldDescriptors(atoms);
		List<FieldInformation> fInfo = buildFieldInfos(fDescriptors,atoms);

		return fInfo;
	}
	
	private List<FieldInformation> buildFieldInfos(
			List<IncompletitudeFieldDescriptor> fDescriptors, List<Atom> atoms) {
		
		List <Attribute> conditionAttributes;
		IncompletitudeFieldDescriptor fDescriptor;
		for (Iterator <Atom> iterator = atoms.iterator(); iterator.hasNext();) {
			Atom atom = iterator.next();
			conditionAttributes = atom.getAllAttributes();
			
			for (Iterator <Attribute> iterator2 = conditionAttributes.iterator(); iterator2
					.hasNext();) {
				Attribute attribute = iterator2.next();
				fDescriptor = getFieldDescriptorByAtribute(attribute, fDescriptors);
				fDescriptor.setPossibleValues(filterByCondition(atom, fDescriptor.getPossibleValues()));
			}
		}
		
		// All FDescriptors have list which have been filtered.
		
		List<FieldInformation> fieldInfoList = new ArrayList<FieldInformation>();
		
		for (Iterator <IncompletitudeFieldDescriptor>iterator = fDescriptors.iterator(); iterator
				.hasNext();) {
			
			fDescriptor = iterator.next();
			fieldInfoList.add(new FieldInformation(fDescriptor.getFieldInformationName(), 
												   fDescriptor.getPossibleValues()));
		}
		
		return fieldInfoList;
	}

	private List<IncompletitudeFieldDescriptor> getFieldDescriptors(
			List<Atom> atoms) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<Attribute> atomAttributes;
		for (Iterator <Atom> iterator = atoms.iterator(); iterator.hasNext();) {
			Atom atom = iterator.next();
			atomAttributes = atom.getAllAttributes();
			attributes.addAll(getSetDifference(attributes,atomAttributes));
			
		}
		
		List<IncompletitudeFieldDescriptor> fDescriptors = new ArrayList<IncompletitudeFieldDescriptor>();
		List<IncompletitudeFieldDescriptor> allFDescriptors = configuration.getfDescriptors();
		
		for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext();) {
			Attribute at = iterator.next();
			fDescriptors.add(getFieldDescriptorByAtribute(at,allFDescriptors));
		}

		return fDescriptors;
	}

	private IncompletitudeFieldDescriptor getFieldDescriptorByAtribute(Attribute at,List<IncompletitudeFieldDescriptor> fds) {
		
		
		for (Iterator<IncompletitudeFieldDescriptor> iterator = fds.iterator(); iterator.hasNext();) {
			IncompletitudeFieldDescriptor incompletitudeFieldDescriptor = iterator.next();
			
			if (at.equals(incompletitudeFieldDescriptor.getAttribute())) {
				return incompletitudeFieldDescriptor;
			}
			
		}
		
		log.log(Level.WARN, "Warning, inc field descriptor not found.");
		return null;
	}

	private List<Attribute> getSetDifference(List<Attribute> set1,
											 List<Attribute> set2){
		boolean notPresent;
		List<Attribute> difference = new ArrayList<Attribute>();
		for (Iterator <Attribute>iterator = set2.iterator(); iterator.hasNext();) {
			Attribute attribute = iterator.next();
			notPresent = true;
			for (Iterator  <Attribute> iterator2 = set1.iterator(); iterator2.hasNext();) {
				Attribute attribute2 = (Attribute) iterator2.next();
				if (attribute.equals(attribute2)){
					notPresent = false;
				}
				
			}
			
			if (notPresent){
				difference.add(attribute);
			}
		}
		
		return difference;
	}
	
	private List<Atom> getAtoms(){
		List<Formula> forms = query.getCondition().toNCF();
		
		List<Atom> atoms = new ArrayList<Atom>();
		
		for (Iterator <Formula> iterator = forms.iterator(); iterator.hasNext();) {
			Formula formula = iterator.next();
			atoms.addAll(getConditionAtoms(formula));
		}
		
		return atoms;
	}
	
	private List<Atom> getConditionAtoms(Formula condition){
		List<Atom> atoms = new ArrayList<Atom>();
		
		if (condition == null){
			log.log(Level.WARN, "Null Condition: ");
			return null;
		}
		
		if (condition instanceof BinaryFormula){
			BinaryFormula binCondition = (BinaryFormula) condition;
			atoms.addAll(getConditionAtoms(binCondition.getLeftSide()));
			atoms.addAll(getConditionAtoms(binCondition.getRightSide()));
			return atoms;
		}
		
		if (condition instanceof NegativeFormula){
			NegativeFormula negCondition = (NegativeFormula) condition;
			log.log(Level.WARN, "Warning: working on non flattened query");
			
			return getConditionAtoms(negCondition.getnFormula());
		}
		
		if (condition instanceof Atom){
			Atom atom = (Atom) condition;
			atoms.add(atom);
			return atoms;
		}
		
		if (condition instanceof NullCondition){
			log.log(Level.WARN, "Warning: working on non flattened query");
			return atoms;
		}
		
		log.log(Level.WARN, "Condition: " + condition.toString() + "no tiene tipo reconocido");
		return null;
	}
	
	private List<FieldValue> filterByCondition(Atom condition, List<FieldValue> possibleValues){
		
		List<FieldValue> condValue = null;
		
		if (condition.getRhs() instanceof SimpleValue){
		
			SimpleValue sValue = (SimpleValue) condition.getRhs();
			
			condValue = sValue.getValues();
		}
		
		for (Iterator <FieldValue> iterator = possibleValues.iterator(); iterator.hasNext();) {
			FieldValue sValue = iterator.next();
			if (conditionFails(sValue,condition)){
				iterator.remove();
			}
			
		}
		
		List<FieldValue> tempList = new ArrayList<FieldValue>();
		if (condValue != null){
			boolean present;
			for (Iterator <FieldValue> iterator = condValue.iterator(); iterator.hasNext();) {
				FieldValue condFieldValue = iterator.next();
				present = false;
				for (Iterator <FieldValue> iterator2 = possibleValues.iterator(); iterator.hasNext();) {
					FieldValue possibleValue = iterator.next();
					
					present = present || (condFieldValue.equals(possibleValue));
				}
				
				if (!present){
					tempList.add(condFieldValue);
				}
			}
			
		}
		
		possibleValues.addAll(tempList);
		
		return possibleValues;
		
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
