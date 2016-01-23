package info.pinlab.ttada.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleCacheManagerImpl implements CacheManager{

	private CacheLevel cacheLevel;
	private final Map<CacheLevel, Cache> caches = new HashMap<CacheLevel, Cache>();
	private final List<CacheLevel> chachePriorityList = new ArrayList<CacheLevel>();
	
	private SimpleCacheManagerImpl(){}
	
	
	public static class SimpleCacheManagerBuilder{
		List<Cache> caches = new ArrayList<Cache>();
		CacheLevel defaultLevel = null;
		public SimpleCacheManagerBuilder(){
			
		}
		
		public SimpleCacheManagerBuilder addCache(Cache cache, boolean isDefault){
			if(cache==null){
				throw new IllegalArgumentException("Cache can't be null!");
			}
			caches.add(cache);
			if(isDefault){
				defaultLevel = cache.getLevel();
			}
			return this;
		}
		
		public SimpleCacheManagerImpl build(){
			//-- create / put mem cache if no caches is available - at all!
			if (caches.size() == 0){
				this.addCache(MemCache.getInstance(), false);
			}
			//-- below: check for memcache <- not necesary!
//			boolean hasMemCache = false;
//			for(Cache cache : caches){
//				if(cache instanceof MemCache){
//					hasMemCache = true;
//					break;
//				}
//			}
//			if(!hasMemCache){
//				this.addCache(MemCache.getInstance(), false);
//			}
			
			SimpleCacheManagerImpl scm = new SimpleCacheManagerImpl();
			CacheLevel firstCacheLevel = null;
			for(Cache cache : caches){
				scm.caches.put(cache.getLevel(), cache);
				scm.chachePriorityList.add(cache.getLevel());
				if(firstCacheLevel==null){
					firstCacheLevel = cache.getLevel();
				}
			}
			scm.cacheLevel = (defaultLevel== null) ? firstCacheLevel : defaultLevel;
			return scm;
		}
	}

	
	@Override
	public CacheLevel getCacheLevel() {
		return cacheLevel;
	}


	@Override
	public <T> Pointer cache(T obj, Class<T> clazz) {
		//TODO: differenciate between caching policies
		//-- e.g., memory -> disk -> server 
//		if(cacheLevel.getPrefix().startsWith("re")){ //-- as in remote!
//			return cache(MemCache.getCacheLevel(), obj, clazz);
//		}
		return cache(cacheLevel, obj, clazz);
	}



	@Override
	public <T> Pointer cache(CacheLevel lev, T obj, Class<T> clazz) {
		Cache cache = caches.get(lev);
		return cache.put(obj, clazz);
	}



	@Override
	public <T> T uncache(CachedValue<T> cv, Class<T> clazz) {
		for(CacheLevel level : chachePriorityList){
			Pointer ptr = cv.getPointer(level);
			if (ptr != null){
				Cache cache = caches.get(level);
				return cache.get(ptr, clazz);
			}
		}
		throw new IllegalStateException("Can't find value for CachedValue" + cv);
	}



	@Override
	public int getHashCode(CachedValue<?> cv) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public <V> boolean equals(CachedValue<V> objA, CachedValue<V> objB) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public <T> Pointer unsafeCache(CacheLevel lev, Object obj, Class<T> clazz) {
		Cache cache = caches.get(lev);
		if(cache==null){
			throw new IllegalStateException("Not initialized cache '" + lev.getClass() + "'");
		}
		return cache.put(clazz.cast(obj), clazz);
	}


	@Override
	public <T> T get(String ptr, Class<T> clazz) {
		for(CacheLevel level : chachePriorityList){
			final String prefix = level.getPrefix()+"://";
			if(ptr.startsWith(prefix)){
				Cache cache = caches.get(level);
				Integer i = Integer.parseInt(ptr.substring(prefix.length()));
				return cache.get(new Pointer(i), clazz);
			}
		}
		return null;
	}
	
	@Override
	public <T> T get(Pointer ptr, Class<T> clazz) {
		for(CacheLevel level : chachePriorityList){
			Cache cache = caches.get(level);
			T obj = cache.get(ptr, clazz);
			if(obj!=null)
				return obj;
		}
		return null;
	}

}
