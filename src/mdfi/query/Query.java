package mdfi.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mdfi.conditions.Formula;

public class Query {

	private Query parentQuery;
	private List<Attribute> requestedAttributes;
	private Formula condition;
	
	
	public Query(Query parentQuery, List<Attribute> requestedAttributes,
			Formula condition) {
		super();
		this.parentQuery = parentQuery;
		this.requestedAttributes = requestedAttributes;
		this.condition = condition;
	}

	public List<Attribute>  getQualifierAttributes (){
		return condition.getAttributes();
	}
	
	
	
	public Formula getCondition() {
		return condition.clone();
	}

	public List<Attribute> getConditionAttributes(){
		return condition.getAllAttributes();
	}
	
	public List<Attribute> getRequestedAttributes() {
		ArrayList<Attribute> list = new ArrayList<Attribute>();
		
		for (Iterator<Attribute> iterator = requestedAttributes.iterator(); iterator.hasNext();) {
			list.add(iterator.next());
			
		}
		
		return list;
	}

	public Query clone(){
		return new Query(parentQuery, requestedAttributes, condition);
	}
	
	private final static String keyword []= new String[10];
	{
		keyword[0] = "LIST ";
		keyword[1] = " INSTANCES\n";
		keyword[2] = "SUCH THAT ";
		keyword[3] = ";\n";

	}
	public String toString(){
		
		String qString = keyword[0];
		
		for (Iterator <Attribute> iterator = getRequestedAttributes().iterator(); iterator.hasNext();) {
			qString += iterator.next();
			
			if (iterator.hasNext()){
				qString+=",";
			}
		}
		
		qString+= keyword[1];
		qString+= keyword[2];
		
		qString+=getCondition().getConditionText(true);
		
		qString+= keyword[3];
		
		return qString;
		
	}
}
