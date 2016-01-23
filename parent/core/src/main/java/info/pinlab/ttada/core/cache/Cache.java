package info.pinlab.ttada.core.cache;


public interface Cache {
	public <T> Pointer put(T obj, Class<T> clazz);
	public <T> T get(Pointer ptr, Class<T> clazz);
	public <T> void remove(Pointer ptr, Class<T> clazz);
	
	CacheLevel getLevel();
}

