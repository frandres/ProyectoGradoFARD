package mdfi.conditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrFormula extends BinaryFormula {

	private static final String OdilLangOrOperator = " OR ";
	
	
	public OrFormula(Formula leftSide, Formula rightSide) {
		super(leftSide, rightSide);
	}

	/* 
	 * Metodo estatico para convertir en una formula una secuencia (lista)
	 * de formulas unidas por ORs.
	 */
	public static Formula convertOrSeq(List<Formula> orSeq) {
	
		
		if (orSeq.size()==0){

			return null;
		}
		
		if (orSeq.size()==1){

			return orSeq.get(0);
		}
		
		if (orSeq.size()>=2){
		
			Formula firstEl = orSeq.remove(0);
			return new OrFormula(firstEl, convertOrSeq(orSeq));
			
		}
	
		return null;
	}
	@Override
	protected String getOperatorText() {
		return OdilLangOrOperator;
	}

	@Override
	/*
	 * Metodo que devuelve una lista que representa la NCF de la formula en cuestión
	 * como una secuencia (lista) de fórmulas que enlazadas por ANDs constituyen una
	 * expresión lógica equivalente a la fórmula.
	 * 
	 *  En el caso de una formula OR se aplica la propiedad distributiva para obtener 
	 *  una secuencia AND de formulas de tipo OR. 
	 */
	public List<Formula> toNCF() {

		List<Formula> leftList = leftSide.toNCF();
		List<Formula> rightList = rightSide.toNCF();

		ArrayList<Formula> result = new ArrayList<Formula>(); 

		for (Iterator <Formula> iterator = leftList.iterator(); iterator.hasNext();) {
			Formula el = iterator.next();
			for (Iterator <Formula> iterator2 = rightList.iterator(); iterator2.hasNext();) {
				result.add(new OrFormula(el, iterator2.next()));
			}
		}
		return result;
		
	}
	
	@Override
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
	public String getConditionText(boolean isAndSeq) {
		if (isAndSeq){
			return "(" + leftSide.getConditionText(false) + getOperatorText() + rightSide.getConditionText(false) + ")";
		}
		else {
			return leftSide.getConditionText(false) + getOperatorText() + rightSide.getConditionText(false);
		}
	}
	
	@Override
	public Formula clone() {
		return new OrFormula(getLeftSide(), getRightSide());
	}
}
