package info.pinlab.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class AbsoluteTime {
	public static String getDay(){
	    GregorianCalendar c = new GregorianCalendar();
	    c.setTime(new Date());
	    return String.format("%4d%02d%02d" 
	    		,c.get(GregorianCalendar.YEAR)
	    		, c.get(GregorianCalendar.MONTH) + 1
	    		, c.get(GregorianCalendar.DAY_OF_MONTH)
	    		);
	}
	
	public static String toTimeStamp(long t){
	    GregorianCalendar c = new GregorianCalendar();
	    c.setTime(new Date(t));
	    return toTimeStamp(c); 
	}
	
	
	public static String toTimeStamp(GregorianCalendar c){
	    return String.format("%4d%02d%02d-%02d%02d%02d" 
	    		, c.get(GregorianCalendar.YEAR)
	    		, c.get(GregorianCalendar.MONTH) + 1
	    		, c.get(GregorianCalendar.DAY_OF_MONTH)
	    		, c.get(GregorianCalendar.HOUR_OF_DAY)
	    		, c.get(GregorianCalendar.MINUTE)
	    		, c.get(GregorianCalendar.SECOND)
	    		);
	}
	
	public static String getNow(){
	    GregorianCalendar c = new GregorianCalendar();
	    c.setTime(new Date());
	    return toTimeStamp(c); 
	}
	
}
