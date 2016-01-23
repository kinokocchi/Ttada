package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.cache.CacheLevel;
import info.pinlab.ttada.core.cache.CacheManager;
import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.model.ExtendedResource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CachedValueAdapter<T> implements
JsonSerializer<CachedValue<?>>,
JsonDeserializer<CachedValue<T>>{
	
	private final CacheManager cacheManager;
	private final CacheLevel cacheLevel;
	private final boolean useRefsInJson;
	
	public CachedValueAdapter(CacheManager cacheManager, CacheLevel cacheLevel, boolean useRefsInJson){
		this.cacheManager = cacheManager;
		this.cacheLevel = cacheLevel;
		this.useRefsInJson = useRefsInJson;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public CachedValue<T> deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException{
		final ParameterizedType typeArg = (ParameterizedType)type;
		final Type paramType = typeArg.getActualTypeArguments()[0];
		final Class<?> clazz = (Class<?>)paramType;

		if(json.isJsonPrimitive()){ //-- it is a reference!
			final String refId = json.getAsString();
			return CachedValue.getCacheWrapperForValue(cacheManager.get(refId, (Class<T>)clazz), (Class<T>)clazz);
		}else{ //-- not a reference but json comes!
//				System.out.println("CachedValue : "+ clazz  +"\n" + json);
				Object obj = null;
				if(ExtendedResource.class.isAssignableFrom(clazz)){
					obj = context.deserialize(json, ExtendedResource.class);
				}else{
					obj = context.deserialize(json, clazz);
				}
				return CachedValue.getCacheWrapperForValue((T)obj, (Class<T>)clazz);
		}
	}

	@Override
	public JsonElement serialize(CachedValue<?> cached, Type typeOfT,
			JsonSerializationContext context){
//		System.out.println("Deserialize Cached!");
		
		final Object obj = cached.getValue();
//		System.out.println("INIT" + obj);

		Class<?> clazz = null; 
		if(obj instanceof ExtendedResource){
			//-- use the interface class to serialize! --//
			clazz = ((ExtendedResource<?>) obj).getInterfaceClass();
		}else{
			clazz = obj.getClass();
		}
//		CacheLevel cacheLevel = cacheManager.getCacheLevel();
		Pointer ptr = cacheManager.unsafeCache(cacheLevel, obj, clazz);
		
		if(useRefsInJson){
			return context.serialize(cacheLevel.getPrefix() + "://"	 + ptr.toString());
		}else{
//			System.out.println("No refs " + clazz);
			if(ExtendedResource.class.isAssignableFrom(clazz)){
				return context.serialize(obj, ExtendedResource.class);
			}else{
				return context.serialize(obj);
			}
		}
	}
	
	
	
}
