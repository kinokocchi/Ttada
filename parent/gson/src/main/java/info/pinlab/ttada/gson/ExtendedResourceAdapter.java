package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.model.ExtendedResource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExtendedResourceAdapter implements 	
	JsonDeserializer<ExtendedResource<?>>,
	JsonSerializer<ExtendedResource<?>>{

	private static final String CLAZZ_TAG = "class";
	private static final String HASH_TAG = "hash";

	private final Map<String, Class<?>> tagClassMap = new HashMap<String, Class<?>>(); 

	
	public ExtendedResourceAdapter(){
	
	}

	
	public Map<String, Class<?>> getTagClassMap(){
		return this.tagClassMap;
	}
	
	
	@Override
	public ExtendedResource<?> deserialize(JsonElement elem, Type type,
			JsonDeserializationContext context) throws JsonParseException {

//		System.out.println("DES " + type);
		JsonObject jObj = elem.getAsJsonObject();
		final String classTag = jObj.get(CLAZZ_TAG).getAsString();
		jObj.remove(CLAZZ_TAG);
		
		Class<?> clazz = tagClassMap.get(classTag);
//		System.out.println(clazz);
		if(clazz==null){
			try {
				clazz = Class.forName(classTag);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}			
		if(clazz == null){
			throw new IllegalStateException("No class enrolled for tag '" + classTag +"'" );
		}
		
//		final Object obj = vanillaGson.getAdapter(clazz).fromJsonTree(jObj);
//		return (ExtendedResource) obj;
		return context.deserialize(jObj, clazz);
	}

	
	volatile int hash = 0;
	
	@Override
	public JsonElement serialize(ExtendedResource<?> res, Type type,
			JsonSerializationContext context) {
//		Class<?> if_clazz = res.getInterfaceClass();
//		System.out.println("Serialize! " + res.getClass());
		
//		JsonElement element  = vanillaGson.toJsonTree(res);
//		JsonObject jobj = element.getAsJsonObject();
		
		JsonElement element = context.serialize(res);
//		JsonElement element = context.serialize(res, res.getClass());
		JsonObject obj = element.getAsJsonObject();
		obj.addProperty(CLAZZ_TAG, res.getClass().getSimpleName());
		obj.addProperty(HASH_TAG, res.hashCode());
		return element;
	}
	
}
