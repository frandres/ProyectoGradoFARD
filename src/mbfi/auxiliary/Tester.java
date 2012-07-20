package mbfi.auxiliary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldInformation;

public class Tester {

	static Logger log = Logger.getLogger(Tester.class.getName());
	
	private String contentFilePath;
	private final static String delimiter = "---";
	private final static String fieldNameRegExp = "(.*)\\|.*";
	private final static String fieldValueRegExp = ".*\\|(.*)";
	
	public Tester(String contentFilePath) {
		super();
		this.contentFilePath = contentFilePath;
	}

	public String getContentFilePath() {
		return contentFilePath;
	}

	public List<ExtractionContext> getFieldIinfos(){
	
		List<ExtractionContext> extractionContexts = new ArrayList<ExtractionContext>();
		List<FieldInformation>  fieldInformations  = new ArrayList<FieldInformation>();
		String thisLine;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(contentFilePath));
			while ((thisLine = br.readLine()) != null) { 
				if (thisLine.compareTo(delimiter)==0){
					extractionContexts.add(new ExtractionContext(fieldInformations));
					fieldInformations  = new ArrayList<FieldInformation>();
				} else{
					fieldInformations.add(parseLine(thisLine));
				}
			}  
		} catch (Exception e) {
			log.log(Level.ERROR, "Problem reading file:" +contentFilePath + " Error:" + e.getMessage());
		}
		
		return extractionContexts;
		
	}

	private FieldInformation parseLine(String thisLine) {
		
		String fieldName = thisLine;
		String fieldValue = thisLine;
		
		Pattern pattern = Pattern.compile(
	                fieldNameRegExp,
	                Pattern.MULTILINE
	    );

		Matcher matcher = pattern.matcher(thisLine);

		if (matcher.find()){

			try{
				fieldName = matcher.group(1);
			}catch (IllegalStateException e) {
				log.log(Level.ERROR, "Problema encontrando nombre de campo: " + thisLine + ", error:" + e.getMessage());
			}		
		}
		
		pattern = Pattern.compile(
                fieldValueRegExp,
                Pattern.MULTILINE
	    );
	
		matcher = pattern.matcher(thisLine);
	
		if (matcher.find()){
	
			try{
				fieldValue = matcher.group(1);
			}catch (IllegalStateException e) {
				log.log(Level.ERROR, "Problema encontrando valor de campo: " + thisLine + ", error:" + e.getMessage());
			}		
		}
		
		ArrayList<String> values = new ArrayList<String>();
		values.add(fieldValue);
		return new FieldInformation(fieldName, values);
	}
	

	private static double  [] getMinimumHitMeasure(boolean training){
		
		double minimumHitMeasure [];
		
		if (training){
			
			minimumHitMeasure = new double [1];
			minimumHitMeasure[0] = .75;
		} else{

			minimumHitMeasure = new double [3];
			
			minimumHitMeasure[0] = 1;
			minimumHitMeasure[1] = .66;
			minimumHitMeasure[2] = .33;
		}
		return minimumHitMeasure;
	}
	
	private static double  [] getProbability(boolean training){
		
		double probability [];
		
		if (training){
			
			probability = new double [1];
			probability[0] = 0;
		} else{

			probability = new double [3];
			
			probability[0] = 0;
			probability[1] = .33;
			probability[2] = .66;
		}
		return probability;
	}
	
	public static void main(String[] args) {
		String configFilePath, testCasesPath;
		
		int mode = 0;
		final int TRAINING = 0;
		final int UNITS = 1;
		final int TESTING =2; 
		String name;
		 if(args.length < 4) {
			 System.out.println("usage: java Tester configFilePath testCasesPath");
			 configFilePath = "/home/frandres/Eclipse/workspace/ExtractionModule/tests/Designaciones/extractionConfigFile.xml";
			 testCasesPath = "/home/frandres/Eclipse/workspace/ExtractionModule/tests/Designaciones/testCases";
//			 System.exit(0);
			 name = "Designaciones";
		 } else{
			 configFilePath = args[0];
			 testCasesPath = args[1]; 
			 
			 if (args[2].compareTo("training")==0){
				 mode =TRAINING;
			 }
			 
			 if (args[2].compareTo("testing")==0){
				 mode =TESTING;
			 }
			 
			 if (args[2].compareTo("units")==0){
				 mode =UNITS;
			 }
			 
			 name = args[3];
		 }
		 Tester testGen = new Tester(testCasesPath);
		 TestSet designacionesTestSet;
		 
		if (mode ==UNITS){
			
			designacionesTestSet = new TestSet(testGen.getFieldIinfos(),0);
			designacionesTestSet.getUnits(configFilePath);
		}else{
		
			double minimumHitMeasure [] = getMinimumHitMeasure(mode==TRAINING);
			double probability [] =  getProbability(mode==TRAINING);
			designacionesTestSet = new TestSet(testGen.getFieldIinfos(),0);
			designacionesTestSet.printLatexTables(configFilePath,minimumHitMeasure,probability,name); 
		
		}
		
	}
	
}
