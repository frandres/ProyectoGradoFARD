package mdfi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.io.XmlReader;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldValue;

import mdfi.query.Attribute;
import mdfi.query.Query;

import mdfi.conditions.*;
import mdfi.conditions.rightHandedSide.BinaryRightHandSide;
import mdfi.conditions.rightHandedSide.NestedQuery;
import mdfi.conditions.rightHandedSide.RightHandSide;
import mdfi.conditions.rightHandedSide.SimpleValue;
import mdfi.incompletitudeFinder.IncompletitudeFieldDescriptor;
import mdfi.incompletitudeFinder.QueryFlattener;
import mdfi.incompletitudeFinder.XMLReader;

public class Prueba {
	public static void main(String[] args) {
		XMLReader xmlReader = new XMLReader("/home/frandres/Eclipse/workspace/ProyectoGradoFARD/tests/Escalafon/incompletitudeDeterminatorConfigFile.xml");
		List<IncompletitudeFieldDescriptor> list= xmlReader.getFDescriptors();
		IncompletitudeFieldDescriptor desc = list.get(0);
		
		
		System.out.println(desc.getFieldName());
		System.out.println(desc.getConceptName());
		System.out.println(desc.getImplicitDomainMaxmumValue());
		System.out.println(desc.getImplicitDomainMinimumValue());
		System.out.println(desc.getDomainType());
		System.out.println(desc.getType());
		System.out.println(desc.getPossibleValues().get(0));
		System.out.println(desc.isGenerateValues());
		
//		List<Attribute> requestedAttributes = new ArrayList<Attribute>();
//		List<FieldValue> values;
//		RightHandSide rhsLS,rhsRS,rhs;
//		
//		requestedAttributes.add(new Attribute("A1", "C1"));
//		
//		values = new ArrayList<FieldValue>();
//		values.add(new FieldValue("1", FieldDescriptor.INTEGER));
//		rhsLS = new NestedQuery(new Query(null, requestedAttributes, new NullCondition()));
//		
//		values = new ArrayList<FieldValue>();
//		values.add(new FieldValue("-2", FieldDescriptor.DOUBLE));
//		
//		rhsRS = new SimpleValue(values);
//		
//		rhs = new BinaryRightHandSide(rhsLS, rhsRS, BinaryRightHandSide.OP_SUM);
//		
//		Formula condition = new NegativeFormula(
//									 new Atom(rhs, 
//									 new Attribute("A2", "C1"),
//									 Atom.OP_GREATER_THAN));
//		
//		Query query = new Query(null, requestedAttributes, condition);
//		
//		System.out.println(query.toString());
//
//		query = QueryFlattener.flattenQuery(query,new Attribute("A2", "C1"));
//		
//		System.out.println(query.toString());
//		int a =2;
//		double b = 1;
//		double c = (double) a;

	}
//	public static void main(String[] args) {
//		
//		ArrayList<Attribute> atributos1;
//		ArrayList<Attribute> atributos2;
//		
//		atributos1 = new ArrayList<Attribute>();
//		atributos1.add(new Attribute("A1", "C1"));
//		
//		atributos2 = new ArrayList<Attribute>();
//		atributos2.add(new Attribute("A1", "C1"));
//		//new Atom(new SimpleValue(), attributes)
//		
//		RightHandSide rhs = new SimpleValue(values);
//		
//		Formula temp1 = new AndFormula(new Atom("C1", atributos1, new ArrayList<Query>()), 
//				 					   new Atom("C2", atributos2, new ArrayList<Query>()));
//		
//		atributos1 = new ArrayList<Attribute>();
//		atributos1.add(new Attribute("A3", "C1"));
//		atributos1.add(new Attribute("A4", "C1"));
//		
//		atributos2 = new ArrayList<Attribute>();
//		
//		Formula temp2 = new AndFormula(new Atom("C3", atributos1, new ArrayList<Query>()), 
//										new Atom("C4", null, new ArrayList<Query>()));
//
//		Formula tempL = new OrFormula(temp1,temp2);
//		
//		atributos1 = new ArrayList<Attribute>();
//		atributos1.add(new Attribute("A5", "C1"));
//		
//		atributos2 = new ArrayList<Attribute>();
//		atributos2.add(new Attribute("A6", "C1"));
//		
//		temp1 = new AndFormula(new Atom("C5", atributos1, new ArrayList<Query>()), new Atom("C6", atributos2, new ArrayList<Query>()));
//		
//		atributos1 = new ArrayList<Attribute>();
//		atributos1.add(new Attribute("A7", "C1"));
//		atributos1.add(new Attribute("A8", "C1"));
//		
//		atributos2 = new ArrayList<Attribute>();
//		atributos2.add(new Attribute("A9", "C1"));
//		
//		temp2 = new OrFormula(new Atom("C7", atributos1, new ArrayList<Query>()),	new Atom("C8", atributos2,new ArrayList<Query>()));
//		
//		Formula tempR = new AndFormula(temp1,temp2);
//
//		Formula form = new NegativeFormula(new OrFormula(tempL, tempR));
//		
//		atributos1= new ArrayList<Attribute>();
//		atributos1.add(new Attribute("B1", "C2"));
//		Query query = new Query(null,atributos1 ,form);
//		
//		System.out.println(query.toString());
//	
//		for (Iterator <Formula> iterator = form.toNCF().iterator(); iterator.hasNext();) {
//			Formula formNueva = iterator.next();
//			
//			System.out.print(formNueva.getConditionText(true));
//			
//			if (iterator.hasNext()){
//				System.out.print(" AND ");
//			}
//		}
//		
//	}

}
