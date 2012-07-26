package common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DateManipulator {

	private String date;
	static Logger log = Logger.getLogger(DateManipulator.class.getName());
	
	private final static  String [] dateFormats= {"dd.MM.yyyy",
												  "MM.yyyy",
												   "yyyy.MM",
												   "dd/MM/yyyy",
												   "MM/yyyy",
												   "yyyy/MM",
												   "dd-MM-yyyy",
												   "MM-yyyy",
												   "yyyy-MM"};
	public DateManipulator(String date) {
		super();
		this.date = date;
	}
	
	public static String getDefaultStringRepresentation(Date date){

		DateFormat dFormat =  new SimpleDateFormat(dateFormats[7]);
		
		return dFormat.format(date);
		
	}
	
	public List<String> getEquivalentDates(){
		int format = determineFormat();
		
		if (format<0){
			ArrayList<String> list = new ArrayList<String>();
			list.add(date);
			//log.log(Level.INFO, "Did not match anything");
			return list;
		}
		return getFormats(format);
	}

	public Date getDate(){
		
		Date dat = null;
		
		int format = determineFormat();
		
		if (format<0){
			log.log(Level.ERROR, "Could not parse date");
			return null;
		}
		
		DateFormat dFormat =  new SimpleDateFormat(dateFormats[format]);
		try {
			dat = dFormat.parse(date);
		} catch (ParseException e) {
			//log.log(Level.ERROR, "Could not parse date");

		}
		
		return dat;
	}
	
	private List<String> getFormats(int format) {
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(date);
		
		DateFormat dFormat =  new SimpleDateFormat(dateFormats[format]);
		Date dat = null;
		try {
			dat = dFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//log.log(Level.ERROR, "Could not parse date");
			return list;
		}
		
		for (int i = 0; i < dateFormats.length; i++) {
			if (format==i){
				continue;
			}
			
			dFormat = new SimpleDateFormat(dateFormats[i]);
			list.add(dFormat.format(dat));
			
		}
		
		return list;
	}

	private int determineFormat() {
		for (int i = 0; i < dateFormats.length; i++) {
			String format = dateFormats[i].replace("d", "\\d").
										   replace("M", "\\d").
										   replace("y", "\\d");
			
			if (date.matches(format)){
				return i;
			}
		}
		return -1;
	}
}
