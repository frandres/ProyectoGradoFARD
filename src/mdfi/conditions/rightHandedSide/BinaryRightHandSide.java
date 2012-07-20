package mdfi.conditions.rightHandedSide;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mdfi.conditions.Atom;
import mdfi.query.Query;

public class BinaryRightHandSide extends RightHandSide{
	static Logger log = Logger.getLogger(BinaryRightHandSide.class.getName());
	private RightHandSide rrhs;
	private RightHandSide lrhs;
	private int operator;
	
	public static final int OP_SUM = 0; // +
	public static final int OP_MINUS = 1; // -
	public static final int OP_TIMES = 2; // *
	public static final int OP_DIVIDED = 3; // /
	public static final int OP_MOD = 4; // %
	public static final int OP_AND = 5; // &&
	public static final int OP_OR = 6; // ||
	
	public BinaryRightHandSide(RightHandSide lrhs, RightHandSide rrhs,
			int operator) {
		super();
		this.rrhs = rrhs;
		this.lrhs = lrhs;
		this.operator = operator;
	}
	
	public RightHandSide getRrhs() {
		return rrhs.clone();
	}
	
	public RightHandSide getLrhs() {
		return lrhs.clone();
	}
	
	public void setRrhs(RightHandSide rrhs) {
		this.rrhs = rrhs;
	}

	public void setLrhs(RightHandSide lrhs) {
		this.lrhs = lrhs;
	}

	public int getOperator() {
		return operator;
	}

	@Override
	public String toString() {

		return lrhs.toString() + getOperatorString() + rrhs.toString();

	}
	
	private String getOperatorString() {
		switch (operator) {
		case OP_SUM:
			return (" + ");

		case OP_MINUS:
			return (" - ");

		case OP_TIMES:
			return (" * ");

		case OP_DIVIDED:
			return (" / ");

		case OP_MOD:
			return (" % ");

		case OP_AND:
			return (" && ");

		case OP_OR:
			return (" || ");
			
		default:
			log.log (Level.WARN,"Operator: " + operator + " not defined");
			break;
		}
		return null;
	}

	@Override
	public List<Query> getQueries() {
		List<Query> list = new ArrayList<Query>();
		list.addAll(rrhs.getQueries());
		list.addAll(lrhs.getQueries());
		return list;
	}
	
	@Override
	public RightHandSide clone() {
		// TODO Auto-generated method stub
		return new BinaryRightHandSide(getRrhs(), getLrhs(), getOperator());
	}
}
