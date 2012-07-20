package mdfi.conditions.rightHandedSide;

import java.util.ArrayList;
import java.util.List;

import mdfi.query.Query;

public class NestedQuery extends RightHandSide {
	Query nestedQuery;

	public NestedQuery(Query nestedQuery) {
		super();
		this.nestedQuery = nestedQuery;
	}

	public Query getNestedQuery() {
		return nestedQuery.clone();
	}

	public void setNestedQuery(Query nestedQuery) {
		this.nestedQuery = nestedQuery;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return nestedQuery.toString(true);
	}
	
	@Override
	public List<Query> getQueries() {
		List<Query> list = new ArrayList<Query>();
		list.add(nestedQuery.clone());
		return list;
	}
	
	@Override
	public RightHandSide clone() {
		// TODO Auto-generated method stub
		return new NestedQuery(getNestedQuery());
	}
}
