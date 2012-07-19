package mbfi.auxiliary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import mbfi.focalizedExtractor.ExtractionContext;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FocalizedExtractor;
import mbfi.focalizedExtractor.XMLReader;

public class TestSet {
	static Logger log = Logger.getLogger(TestSet.class.getName());
	private int numFields;
	private List<ExtractionContext> testCases;
	private double probability;
	private Random rGenerator;

	public TestSet(List<ExtractionContext> testCases, double probability) {
		super();
		
		this.testCases = testCases;
		this.probability = probability;
		rGenerator = new Random();
		if (testCases!= null && testCases.size()>0){
			this.numFields = testCases.get(0).getFieldsInformation().size();
		} else{
			System.out.println("Lista vacia");
		}
		
	}
	
	
	public int getNumFields() {
		return numFields;
	}


	public List<ExtractionContext> getTestCases() {
		return testCases;
	}


	private int getContextNum(int num){
		return num/numFields;
	}
	
	private int getFieldNum(int num){
		return num%numFields;
	}
	
	public ExtractionContext getContext(int num){
		
		List<FieldInformation> fInfo = new ArrayList<FieldInformation>(getTestCases().get(getContextNum(num)).getFieldsInformation());
		
		fInfo.remove(getFieldNum(num));
		
		for (Iterator <FieldInformation> iterator = fInfo.iterator(); iterator.hasNext();) {
			FieldInformation fieldInformation = (FieldInformation) iterator
					.next();
			if (!haveTheField()){
				iterator.remove();
			}
			
		}

		return new ExtractionContext(fInfo);
	}
	
	private boolean haveTheField(){
		return (rGenerator.nextDouble()>probability);
		 
	}
	public String getMissingFieldName(int num){
		return getTestCases().get(getContextNum(num)).getFieldsInformation().get(getFieldNum(num)).getFieldName();
	}
	
	public String getCorrectAnswer(int num){
		return getTestCases().get(getContextNum(num)).getFieldsInformation().get(getFieldNum(num)).getFieldValue();
	}


	public int getNumTests() {
		return numFields*testCases.size();
	}


	
	public double[][][][] runTest(String configFilePath, int numField, double [] minimumHitMeasures,double [] probability) {
		double  results[][][][] = new double[getNumFields()][NOAPROBADO+1][minimumHitMeasures.length][probability.length];
		
		for (int i = 0; i < getNumFields(); i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < minimumHitMeasures.length; k++) {
					for (int l = 0; l < probability.length; l++) {
						results[i][j][k][l]  = 0;
					
					}
				}
			}
		}
		
		int numPruebas = getNumTests();
		double total = numPruebas;
		
		FocalizedExtractor fExt = new FocalizedExtractor();
		fExt.initialize(configFilePath);

	

		for (int probabilityIndex = 0; probabilityIndex < probability.length; probabilityIndex++) {
			this.probability = probability[probabilityIndex];
			for (int minimumHitMeasureIndex = 0; minimumHitMeasureIndex < minimumHitMeasures.length; minimumHitMeasureIndex++) {
				double minimumHitMeasure = minimumHitMeasures[minimumHitMeasureIndex];
				fExt.setMinimumHitRadio(minimumHitMeasure);
//				System.out.println("MINIMUM HIT MEASURE: " + minimumHitMeasure);
				for (int testCaseNum = 0; testCaseNum < getNumTests(); testCaseNum++) {
					if (testCaseNum%getNumFields()!=numField&&numField>=0){
						continue;
					}
					ExtractionContext extContext = getContext(testCaseNum);
					fExt.seteContext(extContext);
					
					String result = fExt.findFieldValue(getMissingFieldName(testCaseNum)).get(0).getStringValue();
					
					int resultEvaluation = evaluateTest(getCorrectAnswer(testCaseNum),result);
					switch (resultEvaluation) {
					case CORRECT:
						log.log(Level.INFO,"Prueba número: " + testCaseNum + " correcta: " + getMissingFieldName(testCaseNum));
						break;
					
					case INCOMPLETE:
						log.log(Level.INFO,"Prueba número: " + testCaseNum + " incompleta: " + getMissingFieldName(testCaseNum));
						log.log(Level.INFO,"Resultado correcto:" + getCorrectAnswer(testCaseNum));
						log.log(Level.INFO,"Resultado obtenido:" + result);
						break;
									
					case INCORRECT:
						log.log(Level.INFO,"Prueba número: " + testCaseNum + " incorrecta: " + getMissingFieldName(testCaseNum));
						log.log(Level.INFO,"Resultado correcto:" + getCorrectAnswer(testCaseNum));
						log.log(Level.INFO,"Resultado obtenido:" + result);
						break;
					case EXCESS:
						log.log(Level.INFO,"Prueba número: " + testCaseNum + " excess: " + getMissingFieldName(testCaseNum));
						log.log(Level.INFO,"Resultado correcto:" + getCorrectAnswer(testCaseNum));
						log.log(Level.INFO,"Resultado obtenido:" + result);
						break;
						
					default:
						break;
					}
	//				log.log(Level.INFO,"Resultado correcto:" + getCorrectAnswer(testCaseNum));
	//				log.log(Level.INFO,"Resultado obtenido:" + result);
					//System.out.println("");
					
					results[testCaseNum%getNumFields()][resultEvaluation][minimumHitMeasureIndex][probabilityIndex]++;
								
				}
				
				for (int i = 0; i<=NOAPROBADO;i++){
					for (int j = 0; j<getNumFields();j++){
						if (i==NOAPROBADO || i == APROBADO){
							results[j][i][minimumHitMeasureIndex][probabilityIndex]= results[j][i-1][minimumHitMeasureIndex][probabilityIndex]+
																					 results[j][i-2][minimumHitMeasureIndex][probabilityIndex];
						}
						else{			
							
								results[j][i][minimumHitMeasureIndex][probabilityIndex]= results[j][i][minimumHitMeasureIndex][probabilityIndex]*getNumFields()*100/total;
						}
					}
				}
			}
//			System.out.println("RESULTS: ");
//		    System.out.println("With probability of: " + probability);
		    
//		    if (humanReadable){
//		    
//			    for (int i = 0; i < getNumFields(); i++) {
//			    	System.out.println("Field: " + getMissingFieldName(i)) ;
//			 
//			    	System.out.println(" CORRECTAS: " + results[i][CORRECT][minimumHitMeasureIndex][probabilityIndex]*getNumFields()*100/total);
//			    	System.out.println(" EXCESS: " + results[i][EXCESS]*getNumFields()*100/total);
//			    	System.out.println(" INCOMPLETE: " + results[i][INCOMPLETE]*getNumFields()*100/total);
//			    	System.out.println(" INCORRECT: " + results[i][INCORRECT]*getNumFields()*100/total);
//			    	
//			    	results[i][CORRECT]=0;
//			    	results[i][EXCESS]=0;
//			    	results[i][INCOMPLETE]=0;
//			    	results[i][INCORRECT]=0;
//				}	
//			    System.out.println("");
//			    System.out.println("-----------------------");
//				System.out.println("");
//		    }
		}
		
		return results;
	    
	}
	
	private String replaceLastPunctuationSigns(String orig){
		if (orig.endsWith(",")){
			orig= orig.substring(0,orig.lastIndexOf(","));
		}
		
		if (orig.endsWith(".")){
			orig= orig.substring(0,orig.lastIndexOf("."));
		}
		
		return orig;
	}
	
	private final static int CORRECT = 0;
	private final static int EXCESS = 1;
	private final static int APROBADO = 2;
	private final static int INCORRECT = 3;
	private final static int INCOMPLETE = 4;
	private final static int NOAPROBADO = 5;
	
	
	private int evaluateTest(String correct, String answer){

		if (answer == null){
			if (correct.compareTo("")==0 || correct ==null){
				return CORRECT;
			}
			else{
				return INCORRECT;
			}
		}
		
		if (correct ==null || correct == ""){
			return INCORRECT;
		}
		
		correct = correct.replace('“','"');
		answer = answer.replace('“','"');
		
		correct = correct.replace('”','"');
		answer = answer.replace('”','"');
		
		if (answer.endsWith(",")){
			answer= answer.substring(0,answer.lastIndexOf(","));
		}
		correct = correct.trim();
		answer = answer.trim();
		
		correct = replaceLastPunctuationSigns(correct);
		answer = replaceLastPunctuationSigns(answer);
		
		
		// ATENCION: EL ORDEN DE ESTOS IFS AFECTA CONSIDERABLEMENTE. 
		
		if (answer.compareTo(correct)==0){
			// ATENCION: EL ORDEN DE ESTOS IFS AFECTA CONSIDERABLEMENTE.
			return CORRECT;
		}
		
		if (correct.contains(answer)){
			// ATENCION: EL ORDEN DE ESTOS IFS AFECTA CONSIDERABLEMENTE.
			return INCOMPLETE;
		}
		
		if (answer.contains(correct)){
			// ATENCION: EL ORDEN DE ESTOS IFS AFECTA CONSIDERABLEMENTE.
			return EXCESS;
		}
	
		
		return INCORRECT;
		
	}
	public void runTest(String configFilePath, double [] minimumHitMeasures, double [] probability) {
		runTest (configFilePath,-1,minimumHitMeasures, probability);
		
	}


	public void getUnits(String configFilePath) {
		
		FocalizedExtractor fExt = new FocalizedExtractor();
		fExt.initialize(configFilePath);
		List<String> docUnits = fExt.getDocumentFragments();

		for (Iterator iterator = docUnits.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println(string);
			System.out.println("&&&&&&&&&&&&");
		}
		
		System.out.println("Cases: " + docUnits.size());
	}


	public void printLatexTables(String configFilePath,
			double[] minimumHitMeasure,
			double[] probabilities, 
			String name) {
		double [][][][] results = runTest (configFilePath,-1,minimumHitMeasure, probabilities);
		
		for (int k = 0; k < minimumHitMeasure.length; k++) {
			
			System.out.println("\\begin{landscape}");
			System.out.println("\\begin{table}");
			System.out.println("\\centering");
			System.out.println("\\caption{ Resultados de la evaluación del Extractor Focalizado - Dominio: " + name+". UnitHit Measure mínimo:"+ minimumHitMeasure[k]+"}");
			System.out.println("\\centering");
			System.out.println("\\scriptsize");
			System.out.println("\\begin{tabular*}{1\\textwidth}{@{\\extracolsep{\\fill}} !{\\vrule width 1pt} c !{\\vrule width 1pt} c !{\\vrule width 1pt} c | c | c !{\\vrule width 1pt} c | c | c !{\\vrule width 1pt}}");
			System.out.println("\\hline");
			System.out.println("Campo & Prob. Campo Faltante & \\multicolumn{3}{c!{\\vrule width 1pt}}{\\bf{P. Aprobadas}} & \\multicolumn{3}{c!{\\vrule width 1pt}}{\\bf{P. No aprobadas}}\\\\");
			System.out.println("\\hline");

			System.out.println("\\multicolumn{2}{!{\\vrule width 1pt}c|}{ } & Correctos & Con texto de más & Aprobados & Incompletos & Incorrectos & No aprobados\\\\");
			System.out.println("\\hline");
			
			for (int i = 0; i < getNumFields(); i++) {
				
				System.out.println("\\multirow{3}{*}{"+ getMissingFieldName(i)+"} ");
				System.out.println("");
				for (int l = 0; l < probabilities.length; l++) {
					
					System.out.println("\t& " + probabilities[l]);
					System.out.print("\t");
					for (int j = 0; j <= NOAPROBADO; j++) {
						DecimalFormat myFormatter = new DecimalFormat("##.##");
						if (j== NOAPROBADO || j == APROBADO){
							System.out.print("& \\bf{" + myFormatter.format(results[i][j][k][l])+ "\\%} ");
						}else{
							System.out.print("& " + myFormatter.format(results[i][j][k][l])+ "\\% ");
						}
						
						
					
					}
					System.out.print("\\\\"+System.getProperty("line.separator"));
					System.out.println("\t\\cline{3-8}");
					System.out.println("");
				}
				
				System.out.println("\\hline");
			}
			
			System.out.println("\\end{tabular*}");
			System.out.println("\\label{tabla-resultados-EF" + name + minimumHitMeasure[k]+ "}");
			System.out.println("\\\\");
			System.out.println("Prob. Campo Faltante es la probabilidad de que no se tenga el valor uno de los campos que se utilizan para hacer extracción focalizada.");
			System.out.println("\\end{table}");
			System.out.println("\\end{landscape}");
		}
	}
	
}
