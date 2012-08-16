package mdfi.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.FieldInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldInformation;
import mbfi.focalizedExtractor.FieldValue;
import mdfi.query.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DatabaseXMLReader {

	//No generics
	//String filename;
	Document dom;
	
	static Logger log = Logger.getLogger(DatabaseXMLReader.class.getName());
	
	public DatabaseXMLReader(String fname){

		dom = parseXmlFile(fname);	
		
		if (dom == null){
			log.log(Level.ERROR, "Unable to read XML: " + fname);	
		}
	}

	/*
	 * Open the file and parse the XML File, returning a Documents with the parsed
	 * informatoin. 
	 */
	private Document parseXmlFile(String filename){

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
	 * the existing FieldDescriptors to return a list of those present
	 * in the XML. 
	 */
	public List<DatabaseUnit> getDatabaseUnits(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		List<DatabaseUnit> dbUnits = new ArrayList<DatabaseUnit>();
		
		//get a nodelist of <FieldDescriptor> elements
		NodeList nl = docEle.getElementsByTagName("DatabaseUnit");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the element
				Element el = (Element)nl.item(i);
				
				//get the FileSource object
				DatabaseUnit dbUnit = getDbUnit(el);
				
				//add it to list
				dbUnits.add(dbUnit);
			}
		}
		
		return dbUnits;
	}


	/**
	 * Given the list of FileSources, extracts them and return
	 * a list of them. 
	 * @param filSEl
	 * @return
	 */
	private DatabaseUnit getDbUnit(Element filSEl) {
		
		List<FieldInformation> fieldInfos = new ArrayList<FieldInformation>();
		
		//get a nodelist of <FieldDescriptor> elements
		NodeList nl = filSEl.getElementsByTagName("FieldInfo");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the element
				Element el = (Element)nl.item(i);
				
				//get the FileSource object
				FieldInformation fInfo = getFieldInfo(el);
				
				//add it to list
				fieldInfos.add(fInfo);
			}
		}
		return new DatabaseUnit(fieldInfos);
	}
	
	private FieldInformation getFieldInfo(Element el) {
		List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		fieldValues.add(new FieldValue(getTextValue(el, "FieldValue"),
						parseFieldType(getTextValue(el, "FieldType"))));
		
		return new FieldInformation(getTextValue(el, "FieldName"), fieldValues);
	}

	
	private int parseFieldType(String textValue) {
		if (textValue.compareTo("INTEGER")==0){
			return FieldDescriptor.INTEGER;
		}
		
		if (textValue.compareTo("STRING")==0){
			return FieldDescriptor.STRING;
		}
		
		if (textValue.compareTo("DOUBLE")==0){
			return FieldDescriptor.DOUBLE;
		}
		
		if (textValue.compareTo("BOOLEAN")==0){
			return FieldDescriptor.BOOLEAN;
		}
		
		if (textValue.compareTo("DATE")==0){
			return FieldDescriptor.DATE;
		}
		
		log.log(Level.WARN, "No se pudo determinar el tipo para:" + textValue);
		return FieldDescriptor.DEFAULT;
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
//
////	public List<String> getUnitRegExps() {
////		Element docEle = dom.getDocumentElement();
////
////		return getTextValue(docEle,"UnitRegExp");
////	}

//	public List<String> getUnitRegExps(){
//		return getRegExpWithPriority(dom.getDocumentElement(), "UnitRegExp");
//		
//	}
	public double getMinimumHitRatio() {
		Element docEle = dom.getDocumentElement();
		return Double.parseDouble(getTextValue(docEle,"MinimumHitRatio"));
	}

	public Attribute getPrimaryKey() {
		return new Attribute(getTextValue(dom.getDocumentElement(),"ConceptName"), 
							 getTextValue(dom.getDocumentElement(),"PrimaryKeyName"));
	}

//	public String getDocumentsFilePath() {
//		Element docEle = dom.getDocumentElement();
//
//		return getTextValue(docEle,"DocumentsFilePath");
//	}

}


	