package mdfi.conditions.rightHandedSide;

import java.util.ArrayList;
import java.util.List;

import mdfi.conditions.Formula;
import mdfi.query.Attribute;
import mdfi.query.Query;

public class NullCondition extends Formula {

	public NullCondition(){
		
	}
	@Override
	public String getConditionText(boolean isAndSeq) {
		return "";
	}

	@Override
	public List<Formula> toNCF() {
		return new ArrayList<Formula>();
	}

	@Override
	public List<Attribute> getAttributes() {
		return new ArrayList<Attribute>();
	}

	@Override
	public List<Query> getNestedQueries() {
		return new ArrayList<Query>();
	}

	@Override
	public Formula clone() {
		return new NullCondition();
	}

	@Override
	public Formula negateCondition() {
		return clone();
	}

}
