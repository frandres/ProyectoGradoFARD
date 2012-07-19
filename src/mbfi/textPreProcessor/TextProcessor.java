package textPreProcessor;
import java.util.List;

import textPreProcessor.extractor.Extractor;
import textPreProcessor.files.FileSource;
import textPreProcessor.formatReaders.XMLReader;

public class TextProcessor {
	 
	public static void main(String[] args) {
		
//		 if(args.length < 1) {
//	        	System.out.println("usage: java TextProcessor <files>");
//	        	System.exit(0);
//	        }
		 String filepath = "/home/frandres/Eclipse/workspace/ExtractionModule/tests/JuradosAscenso/preProcessingConfigFile.xml";
		 XMLReader xmlReader = new XMLReader(filepath);		 
		 List<FileSource> fSources = xmlReader.getFSources();
		 
		 Extractor ext = new Extractor();
		 
		 ext.processFiles(fSources);
		 
	}
}
