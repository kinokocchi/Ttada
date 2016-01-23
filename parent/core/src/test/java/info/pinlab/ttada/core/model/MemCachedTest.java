package info.pinlab.ttada.core.model;

import org.junit.Test;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.cache.MemCache;
import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl;
import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl.SimpleCacheManagerBuilder;

public class MemCachedTest {

	public static void initMemCache(){
		SimpleCacheManagerBuilder scmb = new SimpleCacheManagerBuilder();
		scmb.addCache(MemCache.getInstance(), true);
		SimpleCacheManagerImpl cacheManager = scmb.build();
		
		
		CachedValue.setCacheManager(cacheManager);
	}
	
	@Test
	public void test() {
		//-- init memcache!
//		fail("Not yet implemented");
	}

}
