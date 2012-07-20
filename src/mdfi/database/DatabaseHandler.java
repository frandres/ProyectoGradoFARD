package mdfi.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.query.Attribute;
import mdfi.query.Query;

/*
 * Esta clase sirve de adaptador entre la base de datos o el componente de software 
 * que gestione la Base de Datos y el MDFI.
 */
public class DatabaseHandler {

	public static final String NULL = "NULL";

	public static List<Attribute> getPrimaryKey (String conceptName){
		return new ArrayList<Attribute>();
	}
	
	public static List<FieldValue> getQuerySingleResult (Query query){
		List<FieldValue> results = new ArrayList<FieldValue>();
		Random random = new Random();
		
		FieldValue value = new FieldValue(Integer.toString(random.nextInt(100)), FieldDescriptor.INTEGER);
		results.add(value);
		return results;
		
	}
	
	public static List<DatabaseResult> getQueryResult (Query query){
		List<DatabaseResult> results = new ArrayList<DatabaseResult>();

		return results;
		
	}
	
	public static void insertValuesIntoOntology(String conceptName, 
												String attribute, 
												FieldValue value){
		
		
	}
}
