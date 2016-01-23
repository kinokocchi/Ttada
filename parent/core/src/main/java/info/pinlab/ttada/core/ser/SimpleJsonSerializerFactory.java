package info.pinlab.ttada.core.ser;

import info.pinlab.ttada.core.cache.CacheManager;

public interface SimpleJsonSerializerFactory{
	
	public SimpleJsonSerializerFactory registerTypeAdapter(Class<?> clazz, Object obj);
	public SimpleJsonSerializerFactory setCacheManager(CacheManager cacheManager);
	
	public SimpleJsonSerializerFactory addClassTag (String aTag, Class<?> forThisClass);
	public SimpleJsonSerializerFactory removeClassTag (String aTag);
	public SimpleJsonSerializerFactory removeClassTag (Class<?> forThisClass);
	
	public SimpleJsonSerializerFactory setUseRefsInJson(boolean b);
	

	public SimpleJsonSerializer build();
	
}
