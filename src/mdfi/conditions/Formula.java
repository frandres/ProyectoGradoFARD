package mdfi.conditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mdfi.query.Attribute;
import mdfi.query.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/* 
 * Implementación de un transformador de expresiones lógicas a
 * su Forma Normal Conjuntiva basada en el algoritmo descrito en 
 * http://cs.jhu.edu/~jason/tutorials/convert-to-CNF
 * del Prof. Jason Eisner de la John Hopkins University. 
 * 
 * Formula es una clase abstracta que es implementada por 4 tipos de
 * formulas especificas:
 * 
 * 1) Un átomo (fórmula más sencilla que consiste en una condición sobre uno 
 * o más atributos).
 * 
 * 2) NegativeFormula: fórmula que consiste en el negado de otra fórmula.
 * 
 * 3) BinaryFormula: fórmula que consiste en la conjunción o disjunción de 2
 * fórmulas.
 * 
 * Esta clase contiene métodos abstractos que deben implementar las fórmulas 
 * ya definidas así como metodos generalizados para operar sobre atributos.
 * 
 * @author Francisco Rodríguez Drumond. 
 */
public abstract class Formula {

	static Logger log = Logger.getLogger(Formula.class.getName());

	
	// Constructor. 
	
	public Formula (){
		
	}
	

	// Otros metodos utiles.
	
	
	public boolean hasAttribute (Attribute at){
		
		List<Attribute> ats = getAllAttributes();
		
		for (Iterator<Attribute> iterator = ats.iterator(); iterator.hasNext();) {
			Attribute attribute = (Attribute) iterator.next();
			if (attribute.equals(at)){
				return true; 
			}
		}
		
		return hasNestedQueryWithAttribute(at);
		
	}
	// Metodos abstractos
	
	
	protected abstract boolean hasNestedQueryWithAttribute(Attribute at);


	/*
	 * Método para obtener el texto de la condición en formato de 
	 * condición SQL/ODIL.
	 * 
	 * isAndSeq debe ser true si se está imprimiendo la formula en un contexto
	 * en el cual hay una secuencia de ANDs (o si sólo se está imprimiendo 
	 * esta formula por separado) y false si se está imprimiendo
	 * en un contexto en el cual hay una secuencia de ORs. En esencia este 
	 * booleano permite imprimir apropiadamente los parentesis.
	 *
	 * 
	 */
	public abstract String getConditionText(boolean isAndSeq);
	
	/*
	 * Metodo que devuelve una lista que representa la NCF de la formula en cuestión
	 * como una secuencia (lista) de fórmulas que enlazadas por ANDs constituyen una
	 * expresión lógica equivalente a la fórmula. 
	 */
	public abstract List<Formula> toNCF ();
	
	/*
	 * Método para obtener la lista de los atributos que contiene la formula.
	 * Este método es el que debe ser invocado para obtener los atributos. 
	 */
	public abstract List<Attribute> getAttributes ();

	/*
	 * Método para obtener la lista de los queries anidados que contiene la formula.
	 * Este método es el que debe ser invocado para obtener los queries anidados. 
	 */
	public abstract List<Query> getNestedQueries ();

	/*
	 * Metodo que dado un atributo, devuelve una formula correspondiente a la 
	 * misma condición luego de aplicar un filtrado segun las siguientes 
	 * consideraciones:
	 * 
	 *  1. Si el atributo está presente en un átomo, ese átomo se elimina. 
	 *  2. Si el atributo está presente en una consulta anidada, se filtra
	 *  esa consulta anidada. 
	 
	public abstract Formula filterAttribute(Attribute at);
	*/
	
	/*
	 * Metodo para clonar un objeto. 
	 * @see java.lang.Object#clone()
	 */
	public abstract Formula clone ();


	public List<Attribute> getAllAttributes() {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		attributes.addAll(getAttributes());
		
		for (Iterator <Query> iterator = getNestedQueries().iterator(); iterator.hasNext();) {
			Query query = iterator.next();
			attributes.addAll(query.getConditionAttributes());
		}
		
		return attributes;
	}


//	public Formula toNCFSingleFormula() {
//		List<Formula> formulas = toNCF();
//		
//		return flattenListOfFormulas(formulas);
//
//	}

	public abstract Formula negateCondition();
	
	private Formula flattenListOfFormulas(List<Formula> formulas) {
		
		int remainingObjects = formulas.size();
		
		if (remainingObjects==1){
			return formulas.get(0);
		} 
		if (remainingObjects>1)
		{
			Formula rightSide = formulas.remove(remainingObjects-1);
			Formula leftSide = flattenListOfFormulas(formulas);
			
			AndFormula andForm = new AndFormula(leftSide, rightSide);
			
			return andForm;
		}
		if (remainingObjects<1){
			
			return new NullCondition();
		}
		
		return new NullCondition();
	}


	public Formula removeAttribute(Attribute attribute) {
		List<Formula> formulas = toNCF();
		
		for (Iterator <Formula> iterator = formulas.iterator(); iterator.hasNext();) {
			 Formula formula = iterator.next();
			if (formula.hasAttribute(attribute)){
				log.log(Level.INFO, "Found attribute");
				iterator.remove();
			}
		}
		
		return flattenListOfFormulas(formulas);
		
	}


	/*
	 * Returns a flattened NCF. 
	 */
	public Formula toNCFSF() {
		List<Formula> formulas = toNCF();
		return flattenListOfFormulas(formulas);
	}

}