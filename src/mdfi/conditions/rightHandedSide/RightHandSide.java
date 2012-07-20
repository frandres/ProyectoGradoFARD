package mdfi.conditions.rightHandedSide;

import java.util.List;

import mdfi.query.Query;

public abstract class RightHandSide {

	public abstract String toString();

	public abstract List<Query> getQueries();
	
	public abstract RightHandSide clone();
}
