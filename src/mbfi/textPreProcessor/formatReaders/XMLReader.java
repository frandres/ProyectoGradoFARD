
package mbfi.textPreProcessor.formatReaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mbfi.textPreProcessor.extractor.RegExpDescritor;
import mbfi.textPreProcessor.files.FileInformation;
import mbfi.textPreProcessor.files.FileSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReader {

	//No generics
	String filename;
	public XMLReader(String fname){

		
		this.filename=fname;
	}

	/*
	 * Open the file and extract the filesources listed there.
	 */
	public List<FileSource> getFSources() {
		
		//parse the xml file and get the dom object
		Document dom = parseXmlFile();

		//get each filesource element and create a Filesource object
		if (dom != null){
			return parseDocument(dom);
		}
		else{
			System.out.println("Error reading document");
			return null; 
		}
		
	}
	
	/*
	 * Open the file and parse the XML File, returning a Documents with the parsed
	 * informatoin. 
	 */
	private Document parseXmlFile(){

		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filename);
			
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {		
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return dom; 
	}

	/*
	 * Given a document already ready to be parsed, iterates through
	 * the existing FileSources to return a list of those present
	 * in the XML. 
	 */
	private List<FileSource> parseDocument(Document dom){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		List<FileSource> fSources = new ArrayList<FileSource>();
		
		//get a nodelist of <FileSource> elements
		NodeList nl = docEle.getElementsByTagName("Filesource");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the element
				Element el = (Element)nl.item(i);
				
				//get the FileSource object
				FileSource f = getFileSource(el);
				
				//add it to list
				fSources.add(f);
			}
		}
		
		return fSources;
	}


	/**
	 * Given the list of FileSources, extracts them and return
	 * a list of them. 
	 * @param filSEl
	 * @return
	 */
	private FileSource getFileSource(Element filSEl) {
		
	
		String iPath = getTextValue(filSEl,"InputFilePath");
		String oPath = getTextValue(filSEl,"OutputFilePath");
		List <RegExpDescritor> regExp = getRegExpDescritor(filSEl);
		String readAllFiles = getTextValue(filSEl,"readAllFiles");
		List<FileInformation> fileInformationList;
		
		
		if (readAllFiles.compareTo("1")==0){
			  fileInformationList = new ArrayList<FileInformation>();
			  String outputExtension = getTextValue(filSEl,"outputExtension");
			  String fileName;
			  
			  File folder = new File(iPath);
			  File[] listOfFiles = folder.listFiles(); 
			 
			  for (int i = 0; i < listOfFiles.length; i++) 
			  {
			   
			   if (listOfFiles[i].isFile()) 
			   {
				   fileName = listOfFiles[i].getName();
				   fileInformationList.add(new FileInformation(fileName, extractName(fileName)+outputExtension));
				   // System.out.println(extractName(fileName)+outputExtension);
			      }
			  }
		} else{
			fileInformationList = getFiles(filSEl);
		}
		
		Collections.sort(fileInformationList);
		
		//Create a new Employee with the value read from the xml nodes
		FileSource f = new FileSource(iPath,fileInformationList,oPath,regExp);
		
		return f;
	}

	private List<RegExpDescritor> getRegExpDescritor(Element filSEl) {
		ArrayList<RegExpDescritor> listRegExps = new ArrayList<RegExpDescritor>();
		
		NodeList nl = filSEl.getElementsByTagName("RegExp");
		
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the element
				Element el = (Element)nl.item(i);
				
				//get the FileSource object
				RegExpDescritor f = new RegExpDescritor(getTextValue(el, "String"),
														getIntValue(el,"Priority"));
				
				//add it to list
				listRegExps.add(f);
			}
		}
		
		Collections.sort(listRegExps);
				
		return listRegExps;
	}

	

	/*
	 * Function that given a complete filename with extension, extracts
	 * using regular expressions the filename without the extension. 
	 */
	private String extractName(String fileName2) {
		
		return fileName2.replaceFirst("[.][^.]+$", "");
	}

	private List<FileInformation> getFiles(Element filSEl){

		List<FileInformation> files = new ArrayList<FileInformation>();
		
		//get a nodelist of <employee> elements
		NodeList nl = filSEl.getElementsByTagName("File");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the employee element
				Element el = (Element)nl.item(i);

				//add it to list
				files.add(new FileInformation(getTextValue(el,"InputFilename"),getTextValue(el,"OutputFilename")));
			}
		}
		
		return files;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private int getIntValue(Element el, String string) {

		return Integer.parseInt(getTextValue(el, string));
	}
}
	