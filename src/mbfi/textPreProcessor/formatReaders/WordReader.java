package mbfi.textPreProcessor.formatReaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class WordReader {
	 static Logger log = Logger.getLogger(WordReader.class.getName());  
	 
	/* Receive a filename with the complete path. Return the contents of the
	 * file
	 */
	public static String getTextContent (String filename){
		
		String textContent= null;
		
		try {
				
			InputStream input = new FileInputStream(new File(filename));
			
			if (filename.matches(".*docx")){
				XWPFWordExtractor extractor = new XWPFWordExtractor(new XWPFDocument(input));
				input.close();
				
			
				textContent = extractor.getText();
				
			} else{
				WordExtractor extractor = new WordExtractor(input);
				input.close();


				textContent = extractor.getText();
			}
		
		} catch (Exception e) {
			log.log(Level.ERROR,"Problem reading the file" + filename + ". Error: " + e.getMessage());
		}
		
		return textContent;
		
	}
}
