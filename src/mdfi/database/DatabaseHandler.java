package mdfi.database;

import java.util.ArrayList;
import java.util.List;

import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.query.Query;

/*
 * Esta clase sirve de adaptador entre la base de datos o el componente de software 
 * que gestione la Base de Datos y el MDFI.
 */
public class DatabaseHandler {

	public static String getPrimaryKey (String conceptName){
		return "Primary Key";
	}
	
	public static List<FieldValue> getQueryResult (Query query){
		List<FieldValue> results = new ArrayList<FieldValue>();
		return results;
		
	}
	
	public static void insertValuesIntoOntology(String conceptName, 
												String attribute, 
												FieldValue value){
		
		
	}
}
