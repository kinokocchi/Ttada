package info.pinlab.ttada.cache.disk;

import info.pinlab.ttada.core.cache.CacheLevel;
import info.pinlab.ttada.core.cache.LocalCache;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.utils.FileStringTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * Write data to local HDD
 * 
 * Wraps DiskCacheHandler for put, get, remove 
 * 
 * @author Gabor Pinter
 *
 */
public class DiskCache implements LocalCache{
	public static final Logger logger = Logger.getLogger(DiskCache.class);
	public static final String SEP = FileStringTools.SEP; // System.getProperty("file.separator");
	public static final String EXT = ".gz";


	private final File rootDir;
	private final Map<Class<?>, Class<?>> interFaceMap;
	private final Map<String, Class<?>> tag2ClassMap;
	private SimpleJsonSerializer jsonAdapter;
	Map<Class<?>, TypeCacher<?>> typeMap; 
	


	private static final CacheLevel level = new DiskCacheLevel();
	private static final class DiskCacheLevel implements CacheLevel{
		private static final String levelTag = "loc";
		@Override
		public String getPrefix() {
			return levelTag;
		}
	}
	
	private DiskCache(
			File rootDir,
			Map<Class<?>, Class<?>> ifMap,
			Map<String, Class<?>> tag2ClazzMap,
			SimpleJsonSerializer adapter
			){
		this.rootDir = rootDir;
		//-- create root dir if does not exist! --//
		if(!rootDir.exists()){
			if(!rootDir.mkdirs()){
				logger.error("Couldn't create root dir for cache! '" + rootDir.getAbsolutePath() +"'");
			}else{
				logger.info("Creating root dir for cache! '" + rootDir.getAbsolutePath() +"'");
			}
		}
		
		
		this.tag2ClassMap = tag2ClazzMap;
		this.interFaceMap = ifMap;
		this.jsonAdapter = adapter;
		
		typeMap = new HashMap<Class<?>, TypeCacher<?>>();
	}
	
	
	public static class DiskCacheBuilder{
		//-- default cache root --//
		private File root = null;
		//-- maps --//
		private Map<Class<?>, Class<?>> interFaceMap = new HashMap<Class<?>, Class<?>>();
		private Map<String, Class<?>> tag2ClassMap = new HashMap<String, Class<?>>();
		private SimpleJsonSerializer jsonAdapter = null;
//		private boolean isSaveTextResponseAsText = true;
//		private boolean isSaveWavResponseAsText = true;
//		private File responseTxtFile = null;
		
		public DiskCacheBuilder(){};
		
		/**
		 * Create cache builder with all the data give cache 
		 * 
		 * @param cache  copy settings from here
		 */
		public DiskCacheBuilder(DiskCache cache){
			this.root = cache.getRootDir();
			this.jsonAdapter = cache.jsonAdapter;
			this.interFaceMap = cache.interFaceMap;
			this.tag2ClassMap = cache.tag2ClassMap;
//			this.responseTxtFile = cache.getTextResponseFile();
//			this.isSaveTextResponseAsText = cache.isSaveTxtResp;
//			this.isSaveWavResponseAsText = cache.isSaveWavResp;
//			this.responseTxtFile = cache.getTextResponseFile();
		}
		
		public DiskCacheBuilder setDiskCacheRootPath(File root){
			if (root==null || root.getPath().isEmpty()){
				throw new IllegalArgumentException("Disk cache root dir can't be null!");
			}
			if(!root.isAbsolute()){
				throw new IllegalArgumentException("Only absolute path can be the Local Cache root! vs '" + root + "'");
			}
			this.root = root;
			return this;
		}
		public DiskCacheBuilder setTag2ClassMap(Map<String, Class<?>> map){
			this.tag2ClassMap = map;
			return this;
		}
		public DiskCacheBuilder setClassInterfaceMap(Map<Class<?>, Class<?>> map){
			this.interFaceMap = map;
			return this;
		}
		public DiskCacheBuilder setJsonAdapter(SimpleJsonSerializer adapter){
			jsonAdapter = adapter;
			return this;
		}

//		public DiskCacheBuilder isSaveRespTextOnTheFly(boolean b){
//			isSaveTextResponseAsText = b;
//			return this;
//		}
//		public DiskCacheBuilder isSaveRespWavOnTheFly(boolean b){
//			isSaveWavResponseAsText = b;
//			return this;
//		}
//		public DiskCacheBuilder setTxtResponseFile(File f){
//			if(f!=null){
//				isSaveTextResponseAsText = true;
//			}
//			responseTxtFile = f;
//			return this;
//		}
		
		
		/**
		 * 
		 * @return Immutable {@link DiskCache} instance.
		 * 
		 * @throws IllegalStateException if json adapter is not set. Json adapter is a compulsory.
		 */
		public DiskCache build(){
			if(this.jsonAdapter==null){
				throw new IllegalArgumentException("Json adapter cannot be null! Please setJsonAdapter()!");
			}

			
			
			if(root==null){
				//-- fallback disk cache folder, if not set --//
				root = new File(System.getProperty("user.home")+ DiskCache.SEP + ".pin" + DiskCache.SEP+ "cache" + DiskCache.SEP).getAbsoluteFile();
				setDiskCacheRootPath(root);
			}
			DiskCache cache = new DiskCache(
					root, 
					interFaceMap, tag2ClassMap, 
					jsonAdapter		);

			//-- house keeping --//
			cache.discoverDiskCache();
			
			return cache;
		}
	}
	
	
	
	void discoverDiskCache(){
		if(!rootDir.isDirectory()){
			logger.warn("No root directory for cache! '" + rootDir.getAbsolutePath() +"'");
			return;
		}
		
		for(String dir : rootDir.list()){
			File classCachePath = new File (rootDir + SEP  + dir);
			if(classCachePath.isDirectory()){
				try{
					Class<?> clazz = tag2ClassMap.get(dir);
					if (clazz!=null){
						if(interFaceMap.containsKey(clazz)){
							clazz = interFaceMap.get(clazz);
						}
						TypeCacher<?> cacher = this.getOrCreateTypeCacher(clazz);
						cacher.parseDir();
					}
				}catch(IllegalArgumentException e){
					logger.warn("Can't parse dir '" +dir +"'" , e);
					continue;
				}
			}
		}
	}
	
	@Override
	public CacheLevel getLevel() {
		return level;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private <T> TypeCacher<T> getOrCreateTypeCacher(Class<T> clazz){
		if(typeMap.containsKey(clazz)){
			return (TypeCacher<T>)typeMap.get(clazz);
		}else{
			File typeRootDir = new File(rootDir + SEP + clazz.getSimpleName());
			TypeCacher<T> typeCacher = new TypeCacher<T>(clazz, typeRootDir, jsonAdapter);
			typeMap.put(clazz, typeCacher);
			return typeCacher;
		}
	}
	
	@Override
	public <T> Pointer put(T obj, Class<T> clazz){
		TypeCacher<T> cacher = getOrCreateTypeCacher(clazz);
		return cacher.put(obj);
	}

	@Override
	public <T> T get(Pointer ptr, Class<T> clazz) {
		TypeCacher<T> cacher = getOrCreateTypeCacher(clazz);
		return cacher.get(ptr);
	}

	@Override
	public <T> void remove(Pointer ptr, Class<T> clazz) {
		TypeCacher<T> cacher = getOrCreateTypeCacher(clazz);
		cacher.remove(ptr);
	}
	public static CacheLevel getCacheLevel(){
		return level;
	}

	
//	@Override
//	public void isSaveTextResponseAsText(boolean b) {
////		DiskCacheHandler.isSaveTextResponseAsText(b);
//		isSaveTextResponseAsText = b;
//	}
//
//	@Override
//	public void isSaveAudioResponseAsWav(boolean b) {
////		DiskCacheHandler.isSaveAudioResponseAsWav(b);	
//		isSaveAudioResponseAsWav = b;
//		
//	}

	@Override
	public File getRootDir() {
		return rootDir;
	}

//	@Override
//	public File getTextResponseFile() {
//		if(respSaver == null){
//			return null;
//		}
//		return respSaver.getResponseTxtFile();
//	}
//
//	
//	@Override
//	public boolean isSaveTextResponse() {
//		return respSaver != null;
//	}
//	
//	public void dispose(){
//		respSaver.dispose();
//	}
}
