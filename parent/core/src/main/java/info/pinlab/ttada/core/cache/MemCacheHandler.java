package info.pinlab.ttada.core.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MemCacheHandler<T> {
	private static Map<Class<?>, MemCacheHandler<?>> clazz2CacheMap = new HashMap<Class<?>, MemCacheHandler<?>>();

	@SuppressWarnings("unchecked")
	public static <D> MemCacheHandler<D> getCacheFor(Class<D> cla){
		if(clazz2CacheMap.containsKey(cla)){
			return (MemCacheHandler<D>)clazz2CacheMap.get(cla);
		}else{
			MemCacheHandler<D> store = new MemCacheHandler<D>(cla);
			return store;
		}
	}
	

	
	private final Class<T> clazz;
	private final Map<Integer, T> cache = new HashMap<Integer, T>();
	
	private MemCacheHandler(Class<T> cla){
		clazz = cla;
		clazz2CacheMap.put(cla, this);
	}
	public Class<T> getValueType(){
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	protected static <V> Pointer cache(V value){
		final Class<V> clazz = (Class<V>)value.getClass();
		MemCacheHandler<V> cache = getCacheFor(clazz);
		return cache.put(value);
	}

	@SuppressWarnings("unchecked")
	protected static <T, D extends T> Pointer cacheAs(T value, Class<D> clazz){
		MemCacheHandler<D> cache = getCacheFor(clazz);
		return cache.put((D)value);
	}

	
	public Pointer put(T t){
		int key = t.hashCode();
//		System.out.println("   caching : " + t.getClass() + " " +  key);
		Pointer ptr = new Pointer(key);
		if(cache.containsKey(key))
			return ptr;
		cache.put(key, t);
		return ptr;
	}
	
	public T get(Pointer ptr){
		int key = ptr.getInt();
//		System.out.println("Getting " + key + " " + clazz);
		return cache.get(key);
	}
	
	
	public Set<Pointer> getAllPointers(){
		Set<Pointer> ptrs = new HashSet<Pointer>();
		for(int i : cache.keySet()){
			ptrs.add(new Pointer(i));
		}
		return ptrs;
	}
	
	public boolean containsKey(Pointer ref){
		return containsKey(ref.getInt());
	}
	public boolean containsKey(Integer ref){
		return cache.containsKey(ref);
	}
	
	public void remove(Pointer ptr) {
		cache.remove(ptr);
//		List<Integer> toRemove = new ArrayList<Integer>();
//		for(Integer h : store.keySet())
//			if (h.equals(key))
//				toRemove.add(h);
//		for(Integer i : toRemove)
//			hash2IxMap.remove(i);
	}
}
