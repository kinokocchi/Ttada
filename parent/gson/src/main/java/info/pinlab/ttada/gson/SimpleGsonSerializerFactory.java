package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.cache.CacheManager;
import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl.SimpleCacheManagerBuilder;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimpleGsonSerializerFactory implements SimpleJsonSerializerFactory{
	public static Logger LOG = LoggerFactory.getLogger(SimpleGsonSerializerFactory.class);
	
	private static final String propertyFileName = "classtags.property";
	
	public final String CLAZZ_TAG = "class";
	public final String HASH_TAG = "hash";

	private CacheManager cacheManager = null;
//	private CacheLevel cacheLevel = null;
	private boolean useRefsInJson = false;
	
	private final Map<Class<?>, Object> typeAdapterMap = new HashMap<Class<?>, Object>();
	
	private final GsonBuilder vanillaGsonBuilder;
	private final GsonBuilder gsonBuilder;
	
	private final ExtendedResourceAdapter extResAdapter;
	
	public SimpleGsonSerializerFactory(){
		vanillaGsonBuilder = new GsonBuilder();
		gsonBuilder = new GsonBuilder();
		
		gsonBuilder.disableHtmlEscaping();
		vanillaGsonBuilder.disableHtmlEscaping();
		
		gsonBuilder.setPrettyPrinting();
		vanillaGsonBuilder.setPrettyPrinting();

		
		//-- define Gson TypeAdapters --//
		extResAdapter = new ExtendedResourceAdapter();
	}
	
	
	@Override
	public SimpleJsonSerializerFactory registerTypeAdapter(Class<?> clazz, Object typeAdapter){
		if(typeAdapter==null && typeAdapterMap.containsKey(clazz)){
			typeAdapterMap.remove(clazz);
		}
		typeAdapterMap.put(clazz, typeAdapter);
		return this;
	}

	@Override
	public SimpleJsonSerializerFactory setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		return this;
	}
	@Override
	public SimpleJsonSerializerFactory addClassTag(String aTag, Class<?> forThisClass) {
		extResAdapter.getTagClassMap().put(aTag, forThisClass);
		return this;
	}
	
	
	@Override
	public SimpleJsonSerializerFactory removeClassTag (String aTag){
		extResAdapter.getTagClassMap().remove(aTag);
		return this;
	}
	

	@Override
	public SimpleJsonSerializerFactory removeClassTag (Class<?> forThisClass){
		for(String key : extResAdapter.getTagClassMap().keySet()){
			if(forThisClass.equals(extResAdapter.getTagClassMap().get(key))){
				extResAdapter.getTagClassMap().remove(key);
			}
		}
		return this;
	}
	
	@Override
	public SimpleJsonSerializerFactory setUseRefsInJson(boolean b){
		useRefsInJson = b;
		return this;
	}

	
	
	
	private void initClassTagsForExtendedResourceAdapter(){
		InputStream is = this.getClass().getResourceAsStream(propertyFileName);
		if(is==null){
			LOG.warn("Couldn't find jarred class2tag property file '" + propertyFileName +"'");
		}else{
			Properties props = new Properties();
			String clazzFQN = "";
			String clazzTag = "";
			try {
				props.load(is);
				for(Object key : props.keySet()){
					clazzTag = (String) key;
					clazzFQN =  props.getProperty(clazzTag);
					Class<?> clazz = Class.forName(clazzFQN);
					this.addClassTag(clazzTag, clazz);
				}
			} catch (IOException e) {
				LOG.warn("Couldn't read jarred property file  '" + propertyFileName  +"'");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				LOG.warn("Couldn't find class '" + clazzFQN +"' in classpath");
//				e.printStackTrace();
			}
		}
		
	}

	@Override
	public SimpleJsonSerializer build() {
		//-- register core classes --//
		gsonBuilder.registerTypeAdapter(Response.class, new ResponseAdapter());
		
		
		
		//-- init classTags --//
		initClassTagsForExtendedResourceAdapter();
		gsonBuilder.registerTypeAdapter(ExtendedResource.class, extResAdapter);

		if(cacheManager==null){
			cacheManager =  new SimpleCacheManagerBuilder().build();
//			cacheLevel = cacheManager.getCacheLevel();
		}
		Object cachedValAdapter = new CachedValueAdapter<Object>(cacheManager, cacheManager.getCacheLevel(), useRefsInJson);
		gsonBuilder.registerTypeAdapter(CachedValue.class, cachedValAdapter);
		vanillaGsonBuilder.registerTypeAdapter(CachedValue.class, cachedValAdapter);
		
		//-- register runtime typeAdapters classes --//
		for(Class<?> clazz : typeAdapterMap.keySet()){
			Object adapter = typeAdapterMap.get(clazz);
			if(adapter != null){
				gsonBuilder.registerTypeAdapter(clazz, adapter);
				vanillaGsonBuilder.registerTypeAdapter(clazz, adapter);
			}
		}
		Gson vanillaGson = vanillaGsonBuilder.create();
		Gson gson = gsonBuilder.create();

		return new SimpleGsonSerializer(gson, vanillaGson, cacheManager);
	}
	
	
	public static void main(String[] args){
//		BasicConfigurator.configure();
		
		SimpleJsonSerializer json = new SimpleGsonSerializerFactory().setUseRefsInJson(false).build();
		
		
//		System.out.println(json.toJson(new StepRuleBuilder().build()));
		System.out.println(json.toJson(new TextDisplay("haha")));
		
	}
	
	
}
