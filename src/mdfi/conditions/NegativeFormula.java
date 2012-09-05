package mdfi.conditions;

import java.util.ArrayList;
import java.util.List;
import mdfi.query.Attribute;
import mdfi.query.Query;

public class NegativeFormula extends Formula {

	private Formula nFormula;
	private static final String OdilLangNotOperator = "NOT ";

	
	public NegativeFormula(Formula nFormula) {
		super();
		this.nFormula = nFormula;
	}

	@Override
	/*
	 * Método para obtener el texto de la condición en formato de 
	 * condición SQL/ODIL.
	 * 
	 * isAndSeq es irrelevante en el caso de una NegativeFormula. 
	 *
	 * 
	 */
	public String getConditionText(boolean isAndSeq) {

		if (nFormula instanceof Atom){
			
			return OdilLangNotOperator + nFormula.getConditionText(isAndSeq);
			
		}else{
			
			return OdilLangNotOperator + "(" + nFormula.getConditionText(isAndSeq) + ")";
		}
	}
	
	@Override
	/*
	 * Metodo que devuelve una lista que representa la NCF de la formula en cuestión
	 * como una secuencia (lista) de fórmulas que enlazadas por ANDs constituyen una
	 * expresión lógica equivalente a la fórmula.
	 * 
	 *  En el caso de una formula NOT se aplican las leyes de Morgan o
	 *  doble negación según sea necesario
	 */
	public List<Formula> toNCF() {
		
		ArrayList<Formula> lista = new ArrayList<Formula>();
		
		if (nFormula instanceof Atom){
			lista.add(new NegativeFormula(nFormula));
			return lista;
		}
		
		if (nFormula instanceof NegativeFormula){
			NegativeFormula nForm = (NegativeFormula) nFormula;
			Formula nestedNForm = nForm.getnFormula();
			return nestedNForm.toNCF();
			 
		}
		
		
		if (nFormula instanceof AndFormula){
			AndFormula aForm = (AndFormula) nFormula;
			
			Formula leftSide = new NegativeFormula(aForm.getLeftSide());
			Formula rightSide = new NegativeFormula(aForm.getRightSide());
			
			return (new OrFormula(AndFormula.convertAndSeq(leftSide.toNCF()),
									AndFormula.convertAndSeq(rightSide.toNCF()))).toNCF();			
			
		}

		if (nFormula instanceof OrFormula){
			
			OrFormula oForm = (OrFormula) nFormula;
			
			Formula leftSide  = new NegativeFormula(oForm.getLeftSide());
			Formula rightSide = new NegativeFormula(oForm.getRightSide());
			
			lista.add(AndFormula.convertAndSeq(leftSide.toNCF()));
			lista.add(AndFormula.convertAndSeq(rightSide.toNCF()));
			
		}
		
		return lista ; 
	}

	public Formula getnFormula() {
		return nFormula.clone();
	}

	
	public void setnFormula(Formula nFormula) {
		this.nFormula = nFormula;
	}

	@Override
	public List<Attribute> getAttributes() {

		return getnFormula().getAttributes();
	}
	
	@Override
	public List<Query> getNestedQueries(){
		
		return getnFormula().getNestedQueries();
	}

	@Override
	public Formula clone() {
		return new NegativeFormula(getnFormula());
	}

	@Override
	public Formula negateCondition() {
		return getnFormula().negateCondition();
	}
	
	@Override
	protected boolean hasNestedQueryWithAttribute(Attribute at) {
		return getnFormula().hasNestedQueryWithAttribute(at);
	}
	
	/*
	@Override
	public Formula filterAttribute(Attribute at) {
		NegativeFormula clon = (NegativeFormula) this.clone();
		clon.setnFormula(getnFormula().filterAttribute(at));
		
		return clon;
	}*/
}
