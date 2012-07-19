package mbfi.textPreProcessor.formatReaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;


public class HTMLReader {
	static Logger log = Logger.getLogger(HTMLReader.class.getName());  

	/* Receive a filename with the complete path. Return the contents of the
	 * file
	 */
	public static String getTextContent (String filename){

		InputStream input = null;
		
		
		try {
			input = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			log.log(Level.INFO,"Problem finding the file: " + filename + ". Error: " +e.getMessage());
			return null;
		}
		
		Source source;
		try {
			source = new Source(input);
			TextExtractor extractor = source.getTextExtractor();
			extractor.setConvertNonBreakingSpaces(true);
			input.close();
			return extractor.toString();
		} catch (Exception e) {
			log.log(Level.WARN,"Problem reading the file" + filename + ". Error: " + e.getMessage());
		}
		
		return null;
	}
		
}
