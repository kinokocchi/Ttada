package info.pinlab.ttada.core.cache;




import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl.SimpleCacheManagerBuilder;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Cached values are cahced (in memory/in DB) and normalized.
 * 
 * @author kinoko
 *
 * @param <V>
 */
public class CachedValue<V>{
	public static Logger LOG = LoggerFactory.getLogger(CachedValue.class);
	private static Map<Pointer, CachedValue<?>> staticCache = new HashMap<Pointer, CachedValue<?>>();
	
	
	private V value = null;
//	private final List<Reference<V>> ptrs = new ArrayList<Reference<V>>();
	private final Class<V> clazz ;
	transient private final Map<CacheLevel, Pointer> pointers = new HashMap<CacheLevel, Pointer>();
	
	static private CacheManager cacheManager ;
	
	
	static {
		//-- default CacheManger : mem only --//
		cacheManager =  (new SimpleCacheManagerBuilder()).build();
	}
	
	public static void setCacheManager(CacheManager cm){
		cacheManager = cm;
	}
	public static CacheManager getCacheManager(){
		return cacheManager;
	}

	
	public static <T> CachedValue<T> getCacheWrapperForValue(T obj, Class<T> clazz){
		CachedValue<T> cv = new CachedValue<T>(obj, clazz);
		for(Pointer ptr : cv.pointers.values()){
			if(ptr!=null)
				staticCache.put(ptr, cv);
		}
		return cv;
	}
	public static <T> CachedValue<T> getCacheWrapperForPointer(Pointer ptr, Class<T> clazz, CacheLevel lev){
		CachedValue<T> cv = new CachedValue<T>(clazz, ptr, lev);
		staticCache.put(ptr, cv);
		return cv;
	}
	
	
	
//	@SuppressWarnings("unchecked")
	private CachedValue(V obj, Class<V> type){
		value = obj;
		clazz = type;
		Pointer ptr = cacheManager.cache(obj, type);
		addPointer(cacheManager.getCacheLevel(), ptr);
//		Pointer ptr = CacheFactory.cache(CACHE_LEVEL.MEMORY, t, type);
//		addPointer(CACHE_LEVEL.MEMORY, ptr);
//		if(clazz.equals(MultichoiceTask.class))
//			throw new IllegalArgumentException();
	}

	private CachedValue(Class<V> type, Pointer ptr, CacheLevel level){
		value = null;
		clazz = type;
		addPointer(level, ptr);
	}
	
	public Class<V> getEnclosedClass(){
		return clazz;
	}
	
	
	@SuppressWarnings("unchecked")
	protected void setValueUnsafe(Object value){
		this.value= (V)value;
	}
//	
	protected void setValue(V value){
		this.value = value;
	}
	
	/**
	 * Possible blocks - if it has to be downloaded from remote.
	 * It loads wrapped object into memory.
	 * 
	 * @return
	 */
	public V getValue(){
		if(value==null){
			return cacheManager.uncache(this, getEnclosedClass());
//			throw new IllegalStateException("Has null value! Try calling cacheManager.findValue()!");
//			value = CacheFactory.findValue(this);
		}
		return value;
		
//		if(pointers.containsKey(CACHE_LEVEL.MEMORY)){
//			value = MemCache.getCacheFor(clazz).get((Integer)pointers.get(CACHE_LEVEL.MEMORY));
//			if(value!=null)
//				return value;
//		}
//		if(pointers.containsKey(CACHE_LEVEL.LOCAL)){
//			value = MemCache.getCacheFor(clazz).get((Integer)pointers.get(CACHE_LEVEL.LOCAL));
//			if(value!=null)
//				return value;
//		}
//		if(pointers.size()>0){
//			throw new IllegalStateException("Neither value nor reference is given!");
//		}else{
//			throw new IllegalStateException("Missing resources!");
//		}
	}

	protected void addPointer(CacheLevel cache, Pointer obj){
		pointers.put(cache, obj);
	}
	
	public Pointer getPointer(CacheLevel level){
		return pointers.get(level);
	}
	
	
	public boolean hasValue(){
		return value!=null;
	}
	
//	protected void addRefPointer(Reference<V> ptr){
//		if(ptr!=null){
//			ptrs.add(ptr);
//			return;
//		}
//		throw new IllegalArgumentException("Argumetn can't be null!");
//	}
//
//	public boolean isCached(){
//		if(value!=null)
//			return false;
//		if(ptrs.size()>0)
//			return true;
//		throw new IllegalStateException("Neither value nor reference is given!");
//	}
	
	
	transient int hash = 0;
	
	@Override
	public int hashCode(){
		if(hash!=0)
			return hash;
		if(value!=null){
			hash = value.hashCode();
			return hash;
		}
//		if(pointers.containsKey(CACHE_LEVEL.MEMORY)){
//			return pointers.get(CACHE_LEVEL.MEMORY).getInt();
//		}
//		if(pointers.containsKey(CACHE_LEVEL.LOCAL)){
//			return pointers.get(CACHE_LEVEL.LOCAL).getInt();
//		}
//		if(pointers.containsKey(CACHE_LEVEL.REMOTE)){
//			return 0;
////			return HashCodeUtil.hash(101, pointers.get(CACHE_LEVEL.REMOTE).getString());
//		}
		
		return 0;
//		value.hashCode();
//		hash = 4451;
//		hash = HashCodeUtil.hash(hash, clazz);
//		if(value!=null){
//			hash = HashCodeUtil.hash(hash, value.hashCode());
//		}else if(pointers.containsKey(CACHE_LEVEL.MEMORY)){
//			hash = HashCodeUtil.hash(hash, pointers.get(CACHE_LEVEL.MEMORY));
//		}else if(pointers.containsKey(CACHE_LEVEL.LOCAL)){
//			hash = HashCodeUtil.hash(hash, pointers.get(CACHE_LEVEL.LOCAL));
//		}else if(pointers.containsKey(CACHE_LEVEL.REMOTE)){
//			hash = HashCodeUtil.hash(hash, pointers.get(CACHE_LEVEL.REMOTE));
//		}
//		return hash;
	}
	
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean equals(Object obj){
		if(!(obj instanceof CachedValue))
			return false;
		CachedValue<?> otherCV = (CachedValue)obj;
//		System.out.println(this.clazz + " ? " + other.clazz);
		if(!this.clazz.equals(otherCV.clazz))
			return false;
		CachedValue<V> other = (CachedValue<V>) otherCV;

		//-- match by value: if present
		if(this.value!=null && other.value!=null){
			return this.value.equals(other.value);
		}
		return cacheManager.equals(this, other);
		
		
//		Pointer thisRef = null;
//		Pointer otherRef = null;
//		//-- matching LOCAL/MEMORY pointers --//
//		thisRef = pointers.get(CACHE_LEVEL.MEMORY);
//		if(thisRef==null){
//			thisRef = pointers.get(CACHE_LEVEL.LOCAL);
//		}
//		otherRef = other.pointers.get(CACHE_LEVEL.MEMORY);
//		if(otherRef==null){
//			otherRef = other.pointers.get(CACHE_LEVEL.LOCAL);
//		}
//		if(thisRef != null && otherRef != null)
//			return thisRef.equals(otherRef);
//		
//		//-- matching REMOTE pointers --//
//		if(	this.pointers.containsKey(CACHE_LEVEL.REMOTE)
//			&& 
//			other.pointers.containsKey(CACHE_LEVEL.REMOTE)
//		){
//			return this.pointers.get(CACHE_LEVEL.REMOTE).equals(other.pointers.get(CACHE_LEVEL.REMOTE));
//		}
		
//		//-- --//
//		V thisValue = this.getValue();
//		V otherValue = (V)other.getValue();
//
//		if(thisValue==null || otherValue==null){
//			logger.warn("Can't compare objects! One of the wrapped objects are not reachable!" + this + " <=> " + other);
//			return false;
//		}
//		return thisValue.equals(otherValue);
//		throw new IllegalStateException("No matching local:local, remote:remote refs to compare! Hybrid comparision (remote:local) has not implemented yet!");
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("CachedValue<" + clazz + "> " + (value==null? "no-value : " : "has-value : "));
		for (CacheLevel lev : pointers.keySet()){
			sb	.append(lev.getPrefix())
				.append(": ")
				.append(pointers.get(lev))
				.append(" ");
			;
		}
		return sb.toString();
//		return "CachedValue<" + clazz + "> " + (value==null? "no-value : " : "has-value : ") 
//				+ pointers.get(CACHE_LEVEL.MEMORY) +" : "
//				+ pointers.get(CACHE_LEVEL.LOCAL) +" : "
//				+ pointers.get(CACHE_LEVEL.REMOTE) ;
	}
	
}
