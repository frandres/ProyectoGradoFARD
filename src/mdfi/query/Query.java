package mdfi.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mdfi.conditions.Formula;

public class Query {

	private Query parentQuery;
	private List<Attribute> requestedAttributes;
	private Formula condition;
	List<AggregateFunction> aggregateFunctions;
	
	public Query(Query parentQuery, List<Attribute> requestedAttributes,
			Formula condition, List<AggregateFunction> aggregateFunctions) {
		super();
		this.parentQuery = parentQuery;
		this.requestedAttributes = requestedAttributes;
		this.condition = condition;
		this.aggregateFunctions = aggregateFunctions;
	}

	static Logger log = Logger.getLogger(Query.class.getName());
	
	public Query(Query parentQuery, List<Attribute> requestedAttributes,
			Formula condition) {
		super();
		this.parentQuery = parentQuery;
		this.requestedAttributes = requestedAttributes;
		this.condition = condition;
		this.aggregateFunctions = null;
		
	}

	public List<Attribute>  getQualifierAttributes (){
		return condition.getAttributes();
	}
	
	
	
	public Formula getCondition() {
		
		return condition.clone();
	}

	
	public void setCondition(Formula condition) {
		this.condition = condition;
	}

	public List<Attribute> getConditionAttributes(){
		return condition.getAllAttributes();
	}
	
	public List<Attribute> getRequestedAttributes() {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		
		for (Iterator<Attribute> iterator = requestedAttributes.iterator(); iterator.hasNext();) {
			list.add(iterator.next());
			
		}
		
		if (hasAggregateFunction()){
			for (Iterator <AggregateFunction> iterator = getAggregateFunctions().iterator(); iterator.hasNext();) {
				AggregateFunction aFunction = iterator.next();
				
				list.add(aFunction.getAt());
			}
		}
		return list;
	}

	public void setRequestedAttributes(List<Attribute> requestedAttributes) {
		this.requestedAttributes = requestedAttributes;
	}

	public Query clone(){
		
		List<Attribute> reqAtClone = null;
		
		if (requestedAttributes != null){
		
			reqAtClone = new ArrayList<Attribute>();
			
			for (Iterator<Attribute> iterator = requestedAttributes.iterator(); iterator.hasNext();) {
				reqAtClone.add(iterator.next());
			}
		}
		
		List<AggregateFunction> agFunctionsClone = null;
		
		if (aggregateFunctions != null){
			
			agFunctionsClone = new ArrayList<AggregateFunction>();
			
			
			for (Iterator <AggregateFunction>iterator = aggregateFunctions.iterator(); iterator
					.hasNext();) {
				agFunctionsClone.add(iterator.next());
			}
		}
		
		Query pQClone = null;
		
		if (parentQuery !=null){
			pQClone = parentQuery.clone();
		}
		
		Formula cClone = null;
		
		if (condition!=null){
			cClone = condition.clone();
		}
		return new Query(pQClone, reqAtClone, cClone,agFunctionsClone);
	}
	
	private final static String keyword []= new String[10];
	{
		keyword[0] = "LIST ";
		keyword[1] = " INSTANCES ";
		keyword[2] = "SUCH THAT ";

	}
	public String toString(){
		return toString(false);
	}
	
	private String addLineDelimitator(boolean add){
		if (add){
			return System.getProperty("line.separator");
		}else{
			return "";
		}
	}
	public String toString(boolean nestedQuery){
		
		String qString = "";
		if (nestedQuery){
			qString += "(";
		}
		qString += keyword[0];
		
		for (Iterator <Attribute> iterator = getRequestedAttributes().iterator(); iterator.hasNext();) {
			qString += iterator.next();
			
			if (iterator.hasNext()){
				qString+=",";
			}
		}
		
		qString+= keyword[1];
		qString+= keyword[2]+addLineDelimitator(!nestedQuery);
		
		qString+=getCondition().getConditionText(true);
		
		qString+= addLineDelimitator(!nestedQuery);
		
		if (nestedQuery){
			qString += ")";
		}
		
		return qString;
		
	}

	/*
	 * Remove the given attribute from the Query (meaning requested attributes) and
	 * puts the condition in the NCF, removing conditions which have the given attribute.
	 * 
	 */
	public void removeAttribute(Attribute attribute) {
		
		for (Iterator<Attribute> iterator = requestedAttributes.iterator(); iterator.hasNext();) {
			Attribute at = iterator.next();
			if (at.equals(attribute)){
//				log.log(Level.INFO, "Found attribute");
				iterator.remove();
				
			}
		}
		
		if (hasAggregateFunction()){

			for (Iterator <AggregateFunction> iterator = getAggregateFunctions().iterator(); iterator.hasNext();) {
				AggregateFunction aFunction = iterator.next();
				
				if (aFunction.getAt().equals(attribute)){
					iterator.remove();
				}
				
			}
		}
		setCondition(condition.removeAttribute(attribute));
	}


	public List<AggregateFunction> getAggregateFunctions() {
		return aggregateFunctions;
	}

	public boolean hasAttribute(Attribute at) {
		for (Iterator <Attribute> iterator = getConditionAttributes().iterator(); iterator.hasNext();) {
			Attribute at2 = iterator.next();
			if (at.equals(at2)){
				return true;
			}
		}
		
		for (Iterator <Attribute> iterator = getQualifierAttributes().iterator(); iterator.hasNext();) {
			Attribute at2 = iterator.next();
			if (at.equals(at2)){
				return true;
			}
		}
		
		for (Iterator <AggregateFunction> iterator = getAggregateFunctions().iterator(); iterator.hasNext();) {
			AggregateFunction aFunction = iterator.next();
			
			if (aFunction.getAt().equals(at)){
				return true;
			}
			
		}
		return false;
	}
	
	public boolean hasAggregateFunction(){
		return (getAggregateFunctions()!= null && getAggregateFunctions().size()>0);
	}

	public List<AggregateFunction> getAggregateFunction() {
		return aggregateFunctions;
	}

	public List<Query> getNestedQueries() {
		return condition.getNestedQueries();
	}
}
