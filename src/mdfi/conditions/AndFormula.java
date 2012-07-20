package mdfi.conditions;

import java.util.ArrayList;
import java.util.List;

public class AndFormula extends BinaryFormula {

	private static final String OdilLangAndOperator = " AND ";
	
	
	public AndFormula(Formula leftSide, Formula rightSide) {
		super(leftSide, rightSide);
		// TODO Auto-generated constructor stub
	}
	
	/* 
	 * Metodo estatico para convertir en una formula una secuencia (lista)
	 * de formulas unidas por ANDs.
	 */
	public static Formula convertAndSeq(List<Formula> andSeq) {
	
		
		if (andSeq.size()==0){

			return null;
		}
		
		if (andSeq.size()==1){

			return andSeq.get(0);
		}
		
		if (andSeq.size()>=2){
		
			Formula firstEl = andSeq.remove(0);
			return new AndFormula(firstEl, convertAndSeq(andSeq));
			
		}
	
		return null;
	}

	@Override
	/*
	 * Metodo que devuelve una lista que representa la NCF de la formula en cuestión
	 * como una secuencia (lista) de fórmulas que enlazadas por ANDs constituyen una
	 * expresión lógica equivalente a la fórmula.
	 * 
	 *  En el caso de una formula AND, consiste en unir las secuencias de ANDs
	 *  correspondientes a la transformación en NCF de las partes. 
	 */
	public List<Formula> toNCF() {

		ArrayList<Formula> lista = new ArrayList<Formula> (leftSide.toNCF());
		lista.addAll(rightSide.toNCF());
		return lista;
		
	}

	@Override
	protected String getOperatorText() {

		return OdilLangAndOperator;
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
			return leftSide.getConditionText(true) + getOperatorText() + rightSide.getConditionText(true);
		}
		else {
			return "(" + leftSide.getConditionText(true) + getOperatorText() + rightSide.getConditionText(true) +")";
		}
	}
	
	@Override
	public Formula negateCondition() {
		// TODO Auto-generated method stub
		return new OrFormula(getLeftSide().negateCondition(), getRightSide().negateCondition());
	}
	
	@Override
	public Formula clone() {
		return new AndFormula(getLeftSide(), getRightSide());
	}
}
