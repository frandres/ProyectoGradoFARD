package mbfi.focalizedExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class FocalizedExtractor {
	
	static Logger log = Logger.getLogger(FocalizedExtractor.class.getName());
	
	private List <FieldDescriptor> fieldDescriptors;
	private ExtractionContext eContext;
	//private List <String> documents; // Los documentos 
	private List <String> documentFragments;
	private double minimumHitRadio;
	
	public FocalizedExtractor(){
		
	}
	
	public FocalizedExtractor(String configFilePath, 
			ExtractionContext eContext, 
			double minimumHitRadio){
			initialize(configFilePath);
			this.minimumHitRadio = minimumHitRadio;
			this.eContext=eContext;
	}

	public void initialize (String configFilePath){

		XMLReader xmlReader= new XMLReader(configFilePath);
		
		// Read the field descriptors.
		
		fieldDescriptors = xmlReader.getFDescriptors();
		Collections.sort(fieldDescriptors);
		
		// Read the RegExp describing each unit.
		
		List<String> unitRegExp = xmlReader.getUnitRegExps();
		
		// Read the minimumHitRatio
		
		minimumHitRadio = xmlReader.getMinimumHitRatio();
		
		//this.documents = documents;
		
		List<String> documents = getDocuments(xmlReader.getDocumentsFilePath());
		
		this.documentFragments = null;
		
		if (documents == null){
			log.log(Level.FATAL, "No se pudieron cargar los documentos");
			return;
		}
		
		this.documentFragments = new ArrayList<String>();
		
		for (Iterator <String>documentsIterator = documents.iterator(); documentsIterator.hasNext();) {
			String document = documentsIterator.next();
			List <String> units = getDocumentUnits(unitRegExp, document);
		
			documentFragments.addAll(units);			
		}
	}
	
		
	public void initialize (String configFilePath, 
							ExtractionContext eContext, 
							double minimumHitRadio){
		initialize(configFilePath);
		this.minimumHitRadio = minimumHitRadio;
		this.eContext=eContext;
	}
	
	private List<String> getDocumentUnits (List<String> regExps, String document){

		ArrayList<String> docUnits = new ArrayList<String>();
		
		for (Iterator <String> iterator = regExps.iterator(); iterator.hasNext();) {
			String regExp = iterator.next();
			docUnits.addAll(getDocumentUnit(regExp,document));
			
		}
		
		return docUnits;
	}
	
	private List<String> getDocumentUnit (String regExp, String document){
		ArrayList<String> docUnits = new ArrayList<String>();
		Pattern pattern = Pattern.compile(
	               regExp,
	                Pattern.MULTILINE
	    );

		Matcher matcher = pattern.matcher(document);

		String unit = null;

		while (matcher.find()){

			try{
				unit = matcher.group();
				docUnits.add(unit);
			}catch (IllegalStateException e) {
				log.log(Level.ERROR, "Problema encontrando unit: " + document + ", error:" + e.getMessage());
			}		
		}
		return docUnits; 
	}
	
	
	public List<FieldValue> findFieldValue (String missingFieldName){
		
		PriorityQueue<UnitHit> pQueue = new PriorityQueue<UnitHit>();
		List<FieldValue> fieldValueList = new ArrayList<FieldValue>();
		String unit;
		
		if (documentFragments == null){
			log.log(Level.FATAL, "No se pudieron cargar los documentos");
			return fieldValueList; 
		}
		
		for (Iterator <String>iterator = documentFragments.iterator(); iterator
				.hasNext();) {
			unit = iterator.next();
			
			double hitM = calculateHitMeasure(unit);
			if (hitM>=minimumHitRadio){
				pQueue.add(new UnitHit(hitM,unit));
			}
			
		}
	
		String fieldValue;
		
		if (pQueue.isEmpty()){
			log.log(Level.INFO, "No unit found.");
		}
		while (!pQueue.isEmpty()){
			UnitHit uHit = pQueue.poll();
			unit = uHit.getUnit();
			log.log(Level.INFO, "Unit: " + unit + " has weight: " + uHit.getHitMeasure());
			
			if ((fieldValue=extractFieldValue(missingFieldName,unit))!=null){
				int type = getFieldDescriptorByName(missingFieldName).getType();
				fieldValueList.add(new FieldValue(parseFieldValue(fieldValue,type),type));
			}
							
		}
		
		return fieldValueList;
		
	}
	
	private String parseFieldValue(String fieldValue, int type) {
		if (type==FieldDescriptor.BOOLEAN){
			return Boolean.toString(fieldValue.length()>0);
		}
		
		return fieldValue;
	}

	private String extractFieldValue(String missingFieldName, String unit) {
		
		FieldDescriptor fDesc = getFieldDescriptorByName(missingFieldName);
		
		List<String> specificRegExps = fDesc.getSpecificRegExp();
		
		String value = null;
		
		for (Iterator<String> iterator = specificRegExps.iterator(); iterator.hasNext();) {
			
			
			String regExp = iterator.next();
			Pattern pattern = Pattern.compile(regExp,
	                					  		Pattern.DOTALL);
		
			Matcher matcher = pattern.matcher(unit);
		
			while (matcher.find()){
				
				try{
					value = matcher.group(1);
				}catch (IllegalStateException e) {
					//log.log(Level.INFO, "Unit: " + unit + ", no tiene el valor de:" + missingFieldName);
				}
			
			}
		
		}
		
		return value; 
	}

	private double calculateHitMeasure(String unit) {
		
		List<FieldInformation> fInfo = eContext.getFieldsInformation();
		double hitMeasure = 0;
		double maxHitMeasure = 0;
		
		for (Iterator <FieldInformation> iterator = fInfo.iterator(); iterator.hasNext();) {
			FieldInformation fieldInformation =  iterator.next();
			FieldDescriptor fDescriptor = getFieldDescriptorByName(fieldInformation.getFieldName());
			if (fDescriptor == null){
				continue;
			}
			
			List<FieldValue> values = fieldInformation.getFieldValues();
			List<String> possiblesValues = new ArrayList<String>();
			
			for (Iterator <FieldValue> iterator2 = values.iterator(); iterator2.hasNext();) {
				FieldValue value = iterator2.next();
				possiblesValues.addAll(fDescriptor.getPossibleValues(value.getStringValue()));
			}
			
			maxHitMeasure += fDescriptor.getWeight();
			
			for (Iterator <String> iterator2 = possiblesValues.iterator(); iterator2
					.hasNext();) {
				String possibleValue = iterator2.next();
				
				if (unit.contains(possibleValue)){
					hitMeasure += fDescriptor.getWeight();
					break;
				}
	
			}
			
		}
		double weight = 0;
		
		if (maxHitMeasure!= 0){
			weight = hitMeasure/maxHitMeasure;
		} else{
			weight =0;
		}
		
		//log.log(Level.INFO, "Unit: " + unit + " has weight: " + weight);
		
		return weight;
		
	}

	private FieldDescriptor getFieldDescriptorByName (String fieldName){
		
		
		FieldDescriptor dummy = new FieldDescriptor(fieldName);
		
		int iPoint = Collections.binarySearch(fieldDescriptors, dummy);
		if (iPoint>fieldDescriptors.size() || iPoint<0){
			log.log(Level.INFO, "No se ha encontrado el campo: " + fieldName + "entre los file descriptors");
			return null;
		}
		
		FieldDescriptor possibleMatch = fieldDescriptors.get(iPoint);
		
		if (possibleMatch.getFieldName().compareTo(fieldName)==0){
			return possibleMatch;
		}else {
			//log.log(Level.INFO, "No se ha encontrado el campo: " + fieldName + "entre los file descriptors");
			return null;
		}				
	}
	
	private List<String> getDocuments(String filePath){
		ArrayList<String> documents = new ArrayList<String>();
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles(); 
		 
		String filename,thisLine, document;
		
		if (listOfFiles==null){
			log.log(Level.ERROR, "Directorio fuente de archivos no encontrado: " + filePath);
			return null;
		}
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			document = new String();
			
			if (listOfFiles[i].isFile()) 
			{
			   filename = filePath + listOfFiles[i].getName();
			   
			   try {
			       BufferedReader br = new BufferedReader(new FileReader(filename));
			       while ((thisLine = br.readLine()) != null) { 
			    	   document+=thisLine+ System.getProperty("line.separator");
			       }  
			       br.close();
			   } catch (Exception e) {
				   log.log(Level.ERROR, "Problem reading file:" +filename + " Error:" + e.getMessage());
			   }
			   
			   documents.add(document);
			   
		      }
		  }
		
		return documents;
		
	}
	
	public List<String> getDocumentFragments() {
		return documentFragments;
	}
	
	public void setMinimumHitRadio(double minimumHitRadio) {
		this.minimumHitRadio = minimumHitRadio;
	}

	public void seteContext(ExtractionContext eContext) {
		this.eContext = eContext;
	}
	
}
