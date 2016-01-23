package info.pinlab.ttada.gson;

import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializerFactory;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;

public class SerializerUtil {

	static SimpleJsonSerializer gson ; 
	static SimpleJsonSerializerFactory gsonFacotry; 

	static{
		gsonFacotry = new SimpleGsonSerializerFactory();
		gson = new SimpleGsonSerializerFactory().build();
	}
	
	public static void addClassTag(String tag, Class<?> clazz){
		gson = null;
		gsonFacotry.addClassTag(tag, clazz);
	}

	public static void removeClassTag(String tag){
		gson = null;

	}

	public static void removeClassTag(Class<?> clazz){
		gson = null;
	}
	

	public static void serializeAndCompare(Object orig, Class<?> clazz){
		if(gson == null){
			gson = gsonFacotry.build();
		}
		serializeAndCompare(orig, clazz, gson);
	}
	
	
	public static void serializeAndCompare(Object orig, Class<?> clazz, SimpleJsonSerializer gson){
		String json = gson.toJson(orig);
		
		Object copy = gson.fromJson(json, clazz);
		
		assertTrue(copy!=null);
		assertTrue("Serialization (json) error.\nOrigin:\n" + json +"\nCopy:\n" + gson.toJson(copy), 
				copy.equals(orig));
		assertTrue("Serialization (json) error.\nOrigin:\n" + json +"\nCopy:\n" + gson.toJson(copy), 
				orig.equals(copy));
	}
	
	public static void main(String [] args){
		
	}
	
}
