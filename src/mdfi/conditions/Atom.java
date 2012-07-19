package mdfi.conditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mdfi.conditions.rightHandedSide.NestedQuery;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.incompletitudeFinder.IncompletitudeFinder;
import mdfi.query.Attribute;
import mdfi.query.Query;

public class Atom extends Formula {

	static Logger log = Logger.getLogger(Atom.class.getName());
	
	private RightHandSide rhs;
	
	/* 
	 * Una lista de los atributos presentes en condiciones a este nivel y en
	 * niveles inferiores del arbol. 
	 */
	private List<Attribute> attributes;

	private int comparationOperation;
	
	public static final int OP_EQUALS =0; //==
	public static final int OP_DIFFERENT_THAN =1; //!=
	
	public static final int OP_LESS_THAN =2; // <
	public static final int OP_GREATER_THAN =3; // <
	
	public static final int OP_LESS_OR_EQUAL =4; // <
	public static final int OP_GREATER_OR_EQUAL =5; // <
	
	public static final int OP_IN =6; // IN
	public Atom(RightHandSide rhs, 
				List<Attribute> attributes,
				int comparationOperation) {
		super();
		this.rhs = rhs;
		
		if (attributes==null){
			attributes = new ArrayList<Attribute>();
		}
		
		this.attributes=attributes;
		this.comparationOperation = comparationOperation;
	}

	public Atom(RightHandSide rhs,
				Attribute attribute,
				int comparationOperation) {
		super();
		this.rhs = rhs;
		
		this.attributes = new ArrayList<Attribute>();
		this.attributes.add(attribute);
		
		this.comparationOperation = comparationOperation;
	}
	@Override
	public List<Formula> toNCF() {
		
		ArrayList<Formula> formulas = new ArrayList<Formula>();
		formulas.add(this.clone());
		
		return formulas;
	}

	@Override
	public String getConditionText(boolean isAndSeq) {
				
		if (attributes.size()==1) {
			return attributes.get(0).toString() + getOperatorString() + getRhs().toString();
		}else{
			log.log(Level.WARN, "Warning: there is an empty list of attributes");
			return "";
		}
		
	}
	
	private String getOperatorString() {
		switch (getComparationOperation()) {
		case OP_EQUALS:
			return " = " ;

		case OP_DIFFERENT_THAN:
			return " != " ;
			
		case OP_LESS_THAN:
			return " < " ;

		case OP_GREATER_THAN:
			return " > " ;

		case OP_LESS_OR_EQUAL:
			return " <= " ;

		case OP_GREATER_OR_EQUAL:
			return " >= " ;
			
		case OP_IN:
			return " IN " ;
				
		default:
			break;
		}
		log.log(Level.ERROR, "Warning: invalid operator: " + getComparationOperation());
		return null;
	}

	@Override
	public List<Attribute> getAttributes() {

		ArrayList<Attribute> attributeList = new ArrayList<Attribute>();
		
		for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext();) {
			Attribute at = (Attribute) iterator.next();
			attributeList.add(at.clone());
		}
		
		return attributeList;
	}
	
	public List<Query> getNestedQueries() {
		return rhs.getQueries();
	}

	
	@Override
	public Formula clone() {
		return new Atom(getRhs(), getAttributes(),getComparationOperation());
	}

	public RightHandSide getRhs() {
		return rhs.clone();
	}

	public void setRhs(RightHandSide rhs) {
		this.rhs = rhs;
	}

	
	public int getComparationOperation() {
		return comparationOperation;
	}

	
	private void setComparationOperation(int comparationOperation) {
		this.comparationOperation = comparationOperation;
	}

	public void negateCondition() {
		switch (getComparationOperation()) {
		case OP_EQUALS:
			setComparationOperation(OP_DIFFERENT_THAN);
			break;
		case OP_DIFFERENT_THAN:
			setComparationOperation(OP_EQUALS);
			break;
			
		case OP_LESS_THAN:
			setComparationOperation(OP_GREATER_OR_EQUAL);
			break;		
			
		case OP_GREATER_THAN:
			setComparationOperation(OP_LESS_OR_EQUAL);
			break;
			
		case OP_LESS_OR_EQUAL:
			setComparationOperation(OP_GREATER_THAN);
			break;
			
		case OP_GREATER_OR_EQUAL:
			setComparationOperation(OP_LESS_THAN);
			break;		
			
		default:
			break;
		}
		
	}

	
	/*
	@Override
	public Formula filterAttribute(Attribute at) {

		if (hasAttribute(at)){
			return null;
		}
		
		List<Query> nestedQueries = getNestedQueries();
		
		for (Iterator <Query> iterator = nestedQueries.iterator(); iterator.hasNext();) {
			Query qry = iterator.next();
			
			if (qry.filterAttribute(at) == null){
				
			}
		}
		return this;
	}*/
}
