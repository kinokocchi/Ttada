package info.pinlab.ttada.core.cache;


public class MemCache implements Cache{
	
	private static final CacheLevel level = new MemCacheLevel();
	private static final class MemCacheLevel implements CacheLevel{
		private static final String levelTag = "mem";
		@Override
		public String getPrefix() {
			return levelTag;
		}
	}
	
	private static final MemCache singleton = new MemCache();
	
	public static MemCache getInstance(){
		return singleton;
	}
	
	
	
	@Override
	public CacheLevel getLevel() {
		return level;
	}


	@Override
	public <T> Pointer put(T obj, Class<T> clazz) {
		MemCacheHandler<T> cache = MemCacheHandler.getCacheFor(clazz);
		return cache.put(obj);
	}


	@Override
	public <T> T get(Pointer ptr, Class<T> clazz) {
		MemCacheHandler<T> cache = MemCacheHandler.getCacheFor(clazz);
		return cache.get(ptr);
	}


	@Override
	public <T> void remove(Pointer ptr, Class<T> clazz) {
		MemCacheHandler<T> cache = MemCacheHandler.getCacheFor(clazz);
		cache.remove(ptr);
	}
	
	
	public static CacheLevel getCacheLevel(){
		return level;
	}
	
}
