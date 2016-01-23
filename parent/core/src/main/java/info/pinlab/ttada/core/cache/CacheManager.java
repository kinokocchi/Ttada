package info.pinlab.ttada.core.cache;




/**
 * Manage how retrieval of resources through various local and remote repositories. 
 * 
 *  
 * 
 * @author Gabor Pinter
 *
 */
public interface CacheManager {
	
	public CacheLevel getCacheLevel();
	
	public <T> Pointer cache(T obj, Class<T> clazz);
	public <T> Pointer cache(CacheLevel  lev, T obj, Class<T> clazz);
	public <T> Pointer unsafeCache(CacheLevel  lev, Object obj, Class<T> clazz);
	public <T> T uncache(CachedValue<T> cv, Class<T> clazz); 
	
	public <T> T get(Pointer ptr, Class<T> clazz);
	public <T> T get(String ptr, Class<T> clazz);
	
	public int getHashCode(CachedValue<?> cv);
	public <V> boolean equals(CachedValue<V> objA, CachedValue<V> objB);
	
}
