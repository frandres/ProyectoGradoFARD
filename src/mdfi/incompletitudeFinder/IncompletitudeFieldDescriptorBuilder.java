package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.DateManipulator;

import mbfi.focalizedExtractor.FieldDescriptor;
import mbfi.focalizedExtractor.FieldValue;

public class IncompletitudeFieldDescriptorBuilder {

	XMLReader reader;
	
	static Logger log = Logger.getLogger(IncompletitudeFieldDescriptorBuilder.class.getName());
	
	private static final int INTEGER_INCREMENT = 1;
	private static final double DOUBLE_INCREMENT = .1;
	
	private static final int INTEGER_MINIMUM = 0;
	private static final int INTEGER_MAXIMUM = 0;
	
	private static final int DOUBLE_MINIMUM = 0;
	private static final int  DOUBLE_MAXIMUM = 0;
	
	private static final String DATE_MINIMUM = "01-01-1988";
	private static final String DATE_MAXIMUM = "31-12-2012";
	
	public IncompletitudeFieldDescriptorBuilder(XMLReader reader) {
		super();
		this.reader = reader;
	}

	public List<IncompletitudeFieldDescriptor> buildIncompletitudeFieldDescriptors(){
			
			List<IncompletitudeFieldDescriptor> iFDs;
						
			iFDs = reader.getFDescriptors();
			
			for (Iterator <IncompletitudeFieldDescriptor> iterator = iFDs.iterator(); iterator.hasNext();) {
				IncompletitudeFieldDescriptor iFD = iterator.next();
				
				if (iFD.isGenerateValues()){
					iFD = generateValues(iFD);
				}
				
			}
			
			return iFDs;
		
	}

	private IncompletitudeFieldDescriptor generateValues(
			IncompletitudeFieldDescriptor iFD) {
		
		switch (iFD.getType()) {
			case FieldDescriptor.INTEGER:
	
				return generateIntValues(iFD, 
										 iFD.getDomainType() ==  IncompletitudeFieldDescriptor.CONTINUOUS);
				
			case FieldDescriptor.DOUBLE:
				
				return generateDoubleValues(iFD, 
						 iFD.getDomainType() ==  IncompletitudeFieldDescriptor.CONTINUOUS);
	
			case FieldDescriptor.DATE:
				
				return generateDateValues(iFD, 
						 iFD.getDomainType() ==  IncompletitudeFieldDescriptor.CONTINUOUS);
			
			case FieldDescriptor.BOOLEAN:
				break;
			case FieldDescriptor.STRING:
				break;
				
			default:
				log.log(Level.DEBUG,"Field type not identified: " + iFD.getType());
				break;
		}
		
		
		return iFD;
	}
	
	private IncompletitudeFieldDescriptor generateIntValues(IncompletitudeFieldDescriptor iFD,
															boolean bounded){
		
		List<FieldValue> possibleFieldValues = new ArrayList<FieldValue>();
		
		int intLowerBound,intMaxBound;
		
		if (bounded){
			intLowerBound = Integer.parseInt(iFD.getImplicitDomainMinimumValue());
			intMaxBound = Integer.parseInt(iFD.getImplicitDomainMaxmumValue());
		} else{
			intLowerBound = INTEGER_MINIMUM;
			intMaxBound = INTEGER_MAXIMUM;			
		}
		
		int increment = INTEGER_INCREMENT;
		if (iFD.getIncrement().length()>0){
			increment = Integer.parseInt(iFD.getIncrement());
		}
		
		for (int i = intLowerBound; i<= intMaxBound; i+= increment){
			possibleFieldValues.add(new FieldValue(Integer.toString(i), FieldDescriptor.INTEGER));
		}
		
		iFD.setPossibleValues(possibleFieldValues);
		return iFD;
		
	}

	private IncompletitudeFieldDescriptor generateDateValues(IncompletitudeFieldDescriptor iFD,
			boolean bounded){

		List<FieldValue> possibleFieldValues = new ArrayList<FieldValue>();
		
		Date dateLowerBound,dateMaxBound;
						
		DateManipulator dMan;
				
		Calendar c = Calendar.getInstance();
		
		if (bounded){

			dMan = new DateManipulator(iFD.getImplicitDomainMinimumValue());
			dateLowerBound = dMan.getDate();
			
			dMan = new DateManipulator(iFD.getImplicitDomainMaxmumValue());
			dateMaxBound = dMan.getDate();

		} else{
			
			dMan = new DateManipulator(DATE_MINIMUM);
			dateLowerBound = dMan.getDate();
			
			dMan = new DateManipulator(DATE_MAXIMUM);
			dateMaxBound = dMan.getDate();		
			
		}
		System.out.println(dateLowerBound.toString());		
		int increment = INTEGER_INCREMENT;
		if (iFD.getIncrement().length()>0){
			increment = Integer.parseInt(iFD.getIncrement());
		}
		
		int counter = 0;
		
		String dateRep;
		
		for (Date idate = dateLowerBound; idate.before(dateMaxBound); ){
			
			dateRep = DateManipulator.getDefaultStringRepresentation(idate);
			
			possibleFieldValues.add(new FieldValue(dateRep, FieldDescriptor.DATE));
			
			if (counter++ >= 1000000){
				log.log(Level.WARN, "Passed numer of possible dates");
				return iFD;
			}
			
			c.setTime(idate);
			c.add(Calendar.DATE, increment);
			idate = c.getTime();
		}
		
		iFD.setPossibleValues(possibleFieldValues);
		return iFD;

	}	
	
	private IncompletitudeFieldDescriptor generateDoubleValues(IncompletitudeFieldDescriptor iFD,
			boolean bounded){

		List<FieldValue> possibleFieldValues = new ArrayList<FieldValue>();
		
		double doubleLowerBound,doubleMaxBound;
		
		if (bounded){
			doubleLowerBound = Double.parseDouble(iFD.getImplicitDomainMinimumValue());
			doubleMaxBound = Double.parseDouble(iFD.getImplicitDomainMaxmumValue());
		} else{
			doubleLowerBound = DOUBLE_MINIMUM;
			doubleMaxBound = DOUBLE_MAXIMUM;			
		}
		
		double increment = INTEGER_INCREMENT;
		if (iFD.getIncrement().length()>0){
			increment = Double.parseDouble(iFD.getIncrement());
		}
		
		for (double i = doubleLowerBound; i<= doubleMaxBound; i+= increment){
			possibleFieldValues.add(new FieldValue(Double.toString(i), FieldDescriptor.DOUBLE));
		}
		
		iFD.setPossibleValues(possibleFieldValues);
		return iFD;

	}	
	
	
}
