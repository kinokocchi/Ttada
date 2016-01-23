package info.pinlab.ttada.cache.disk;

import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.utils.FileStringTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Local cache for a single type. <br> 
 * Instances created for each type/cache. <br>
 * 
 * E.g., one TypeCacher for Task, one for ResponseContent
 *        
 * 
 * @author Gabor Pinter
 *
 * @param <T>
 */
public class TypeCacher<T> {
	public static Logger logger = Logger.getLogger(TypeCacher.class);
	private final Class<T> clazz;
	private final File rootDir; 
	private final Map<Pointer, File> cache;
	/**
	 * No changing serializer for the same cache - for format consistency
	 */
	private final SimpleJsonSerializer jsonAdapter;

	public TypeCacher(Class<T> clazz, File absDir, SimpleJsonSerializer jsonAdapter){
		if(jsonAdapter==null){
			throw new IllegalArgumentException("Json Adapter cannot be null!");
		}
		this.clazz = clazz;
		if(!absDir.isAbsolute()){
			throw new IllegalArgumentException("Resource root must be ABSOLUTE! Yours: '" + absDir + "'");
		}
		rootDir = absDir;
		cache = new HashMap<Pointer, File>();

		if(!rootDir.isDirectory()){
			logger.info("Creating cache dir " + rootDir.getAbsolutePath());
			if(!rootDir.mkdirs())
				throw new IllegalArgumentException("CaN't mkdir '" + rootDir.getAbsolutePath()+"'");
		}
		this.jsonAdapter = jsonAdapter;
	}




	public Pointer put(T t){
		Pointer ptr = new Pointer(t.hashCode());

		File savePath = cache.get(ptr);
		if(savePath!=null){
			//-- already saved! --//
			return ptr;
		}
		savePath = new File(rootDir + DiskCache.SEP + ptr + DiskCache.EXT);


		if(savePath.exists()){
			cache.put(ptr, savePath);
			return ptr;//savePath.getName();
		}
		byte [] bytes = FileStringTools.zip(jsonAdapter.toJson(t));
		saveToDisk(bytes, savePath);
		cache.put(ptr, savePath);
		return ptr;//savePath.getName();
	}


	/**
	 *  removes resource from disk + map
	 * @param ptr
	 */
	public void remove(Pointer ptr) {
		File path = cache.get(ptr);
		if(path!=null){
			if(path.exists() && path.isFile()){
				if(path.delete()){
					logger.debug("Removed '" + path +"'");
				}else{
					logger.debug("Failed to removed '" + path +"'");
				}
			}
		}
		cache.remove(ptr);
	}

	
	public T get(Pointer ptr){
		File path = cache.get(ptr);
		if(path==null){
			path = new File(rootDir + DiskCache.SEP + ptr.getInt() + DiskCache.EXT);
			cache.put(ptr, path);
		}
		
		T t = getFromPath(path.getAbsolutePath());
		if(t==null){
			cache.remove(ptr);
		}
		return t;
		
	}
	private T getFromPath(String absPath){
		try {
			byte[] bytes = FileStringTools.getFileAsByteArray(absPath);
			String json = FileStringTools.unzip(bytes);
			T t = jsonAdapter.fromJson(json, clazz);
			return t;
		} catch (FileNotFoundException e) {
			logger.warn("No such resource as '" + absPath);
			return null;
		}catch (IllegalStateException e){
			logger.error("Not a gzip object? '" + absPath +"'");
			return null;
		}
	}
	

	private static void saveToDisk(byte [] bytes, File savePath){
		try {
			FileOutputStream fos = new FileOutputStream(savePath);
			fos.write(bytes);
			fos.close();
			logger.info("Saved cache: '"  + savePath + "'");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void parseDir(){
		for(String fileName : rootDir.list()){
			int hashAsInt = 0;
			T t = null;
			if(fileName.toLowerCase().endsWith(DiskCache.EXT)){
				String hash = fileName.substring(0, fileName.length()-DiskCache.EXT.length());
				try{
					hashAsInt = Integer.parseInt(hash);
					Pointer ptr = new Pointer(hashAsInt);
					t = get(ptr);
				}catch (NumberFormatException ignore){
					String path = rootDir + DiskCache.SEP + fileName;
					t = getFromPath(path);
					Pointer ptr = new Pointer(t.hashCode());
					cache.put(ptr, new File(path));
				}
			}
		}
	}
	
	
//	private T loadFromDisk(String ref){
//		File resourcePath = new File (rootDir + SEP + ref );
//		//		logger.debug("Loading resource '" + resourcePath.getAbsolutePath());
//		try {
//			byte[] bytes = FileStringTools.getFileAsByteArray(resourcePath.getAbsolutePath());
//			String json = FileStringTools.unzip(bytes);
//			T obj = jsonAdapter.fromJson(json, clazz);
//			//			T obj = gson.fromJson(clazz, json);
//			Pointer ptr = new Pointer(obj.hashCode());
//			cache.put(ptr, resourcePath);
//			//			if(copyLocalToMemory){
//			//				CacheFactory.cache(CACHE_LEVEL.MEMORY, obj, clazz);
//			//			}
//			return obj;
//		} catch (FileNotFoundException e) {
//			//			e.printStackTrace();
//			logger.warn("No such resource as '" + resourcePath.getAbsolutePath());
//			return null;
//		}catch (IllegalStateException e){
//			//			e.printStackTrace();
//			logger.error("Not a gzip object? '" + resourcePath.getAbsolutePath() +"'");
//			return null;
//		}
//	}




}
