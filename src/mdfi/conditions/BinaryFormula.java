package mdfi.conditions;

import java.util.ArrayList;
import java.util.List;

import mdfi.query.Attribute;
import mdfi.query.Query;

/* 
 * Una BinaryFormula es una clase abstracta que representa una fórmula
 * lógica que se caracteriza por contener dos fórmulas adicionales.
 * 
 * En particular esta clase es implementada por AndFormula y por OrFormula.
 * 
 * 
 * @author Francisco Rodríguez Drumond. 
 */
public abstract class BinaryFormula extends Formula {

	protected abstract String getOperatorText();

	Formula leftSide;
	Formula rightSide;
	
	public BinaryFormula(Formula leftSide, Formula rightSide) {
		super();
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
	
	/*
	@Override
	
	 * Metodo que dado un atributo, devuelve una formula correspondiente a la 
	 * misma condición luego de aplicar un filtrado segun las siguientes 
	 * consideraciones:
	 * 
	 *  1. Si el atributo está presente en un átomo, ese átomo se elimina. 
	 *  2. Si el atributo está presente en una consulta anidada, se filtra
	 *  esa consulta anidada. 

	public Formula filterAttribute(Attribute at){
		
		BinaryFormula clon = (BinaryFormula) this.clone();
		
		Formula rSide = rightSide.filterAttribute(at);
		Formula lSide = leftSide.filterAttribute(at);
		
		clon.setLeftSide(lSide);
		clon.setRightSide(rSide);
		
		return clon; 
	}*/
	
	@Override
	/*
	 * Metodo para obtener los atributos de la Formula.
	 * 
	 * En este caso, se verifica si la lista de atributos ha sido ya obtenida.
	 * Si no se ha obtenido, se calcula como la unión de los atributos contenidos
	 * en las 2 formulas.
	 * 
	 * Se supone una naturaleza "estatica" de la formula en el sentido de que no se 
	 * espera que la estructura de la formula cambie. Si cambiase cualquiera de las 2 
	 * formulas, habría que calcular la unión cada vez que se invoque este método. 
	 * 
	 */
	public List<Attribute> getAttributes() {
		
		List<Attribute> attributeList = new ArrayList<Attribute>();

		if (leftSide != null){
			attributeList.addAll(leftSide.getAttributes());
		}
		
		if (rightSide != null){
			attributeList.addAll(rightSide.getAttributes());
		}
		
		return attributeList;
	}

	/*
	 * Metodo para obtener las consultadas anidadas de la Formula.
	 * Se calcula como la unión de las consultadas anidadas contenidos 
	 * en las 2 formulas.
	 */
	public List<Query> getNestedQueries() {
			
		List<Query> nestedQueries = new ArrayList<Query>();

		if (leftSide != null){
			nestedQueries.addAll(leftSide.getNestedQueries());
		}
		
		if (rightSide != null){
			nestedQueries.addAll(rightSide.getNestedQueries());
		}
		
		return nestedQueries;
	}
	
	public Formula getLeftSide() {
		if (leftSide != null){
			return leftSide.clone();
		}
		
		return null;
	}

	public Formula getRightSide() {
		
		if (rightSide != null){
			return leftSide.clone();
		}
		
		return null;
	}

	public void setLeftSide(Formula leftSide) {
		this.leftSide = leftSide;
	}

	public void setRightSide(Formula rightSide) {
		this.rightSide = rightSide;
	}

	@Override
	protected boolean hasNestedQueryWithAttribute(Attribute at) {
		return getLeftSide().hasNestedQueryWithAttribute(at) || 
			   getRightSide().hasNestedQueryWithAttribute(at);
	}
}
