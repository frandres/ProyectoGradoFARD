package mbfi.textPreProcessor.formatReaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class PDFReader {

	static Logger log = Logger.getLogger(PDFReader.class.getName());  
	/* Receive a filename with the complete path. Return the contents of the
	 * file
	 */
	public static String getPDFContent (String filename){
		InputStream input = null;
		try {
			input = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			log.log(Level.INFO,"Problem finding the file: " + filename);
			e.printStackTrace();
			return null;
		}
		ContentHandler textHandler = new BodyContentHandler(10*1024*1024);
		Metadata metadata = new Metadata();
		PDFParser parser = new PDFParser();
		ParseContext context = new ParseContext();
		try {
			parser.parse(input, textHandler, metadata,context);
			input.close();
		} catch (Exception e1) {
			log.log(Level.WARN,"Problem reading the file" + filename + ". Error: " + e1.getMessage());
		}
		String text =  textHandler.toString();
		
		text = filterWhitespaces(text);
		
		return text;
	}
	/*
	 * Filter cases in which the input of the PDF has spaces between every letter
	 */
	public static String filterWhitespaces (String text){
		String copy = new String(text); 
		double charCount = copy.replaceAll(" ", "").length();
		
		copy = copy.replace("  ", "$%*()$%*");
		copy = copy.replace(" ", "");
		copy = copy.replace("$%*()$%*", " ");
		
		if ((double) ((double) charCount/ (double) text.length())<=0.50){
			return copy;
		} else{
			return text;
		}
	}
}
