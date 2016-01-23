package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.cache.CacheManager;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;

import com.google.gson.Gson;


public class SimpleGsonSerializer implements SimpleJsonSerializer {
//	private final String CLAZZ_TAG;
//	private final String HASH_TAG;
//	private final Map<String, Class<?>> tagClassMap = new HashMap<String, Class<?>>(); 

	private final Gson gson; 
	final Gson vanillaGson; 
	
	
	protected SimpleGsonSerializer(
//			String classAttribName, String hashAttribName,
//			Map<String, Class<?>> tagClassMap,
//			Map<Class<?>, Object> typeAdapterMap,
			Gson gson, Gson vanillaGson,
			CacheManager cacheManager
			){
		
		this.gson = gson;
		this.vanillaGson = vanillaGson;
	}
	
	
	@Override
	public String toJson(Object obj){
//		System.out.println("TO JSON" + obj.getClass());
//		System.out.println(obj instanceof ExtendedResource);
		if(obj instanceof ExtendedResource){
			return gson.toJson(obj, ExtendedResource.class);
		}
		return gson.toJson(obj);
	}
	
	@Override
	public String toJson(Object obj, Class<?> clazz){
		return gson.toJson(obj, clazz);
	}


	@Override
	public <T> T fromJson(String json, Class<T> clazz){
		if(ExtendedResource.class.isAssignableFrom(clazz)){
			Object obj = gson.fromJson(json, ExtendedResource.class);
			return clazz.cast(obj);
		}
		return gson.fromJson(json, clazz);
	}
	
	
	
	
}
