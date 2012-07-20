package mdfi.database;

import java.util.List;

import mbfi.focalizedExtractor.FieldInformation;

/*
 * Row de una salida de la base de datos.
 */
public class DatabaseResult {

	private List<FieldInformation> results;

	public DatabaseResult(List<FieldInformation> results) {
		super();
		this.results = results;
	}

	public List<FieldInformation> getResults() {
		return results;
	}
	
	
	
}
