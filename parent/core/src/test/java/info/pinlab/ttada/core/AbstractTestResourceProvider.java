package info.pinlab.ttada.core;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public abstract class AbstractTestResourceProvider<T> implements TestResourceProvider<T>{
	private Class<T> clazz;
	private List<T> resourceObjects = new ArrayList<T>();
	
	public AbstractTestResourceProvider(Class<T> clazz){
		this.clazz = clazz; 
	}
	
	@Test
	@Override
	public void testGetResources(){
		List<T> ts = getResources();
		assertTrue("No resource to test! (null array)",  ts != null);
		assertTrue("No " + clazz.getSimpleName() + "s, empty array!", ts.size() > 0 );
		for(T t : ts){
			assertTrue(t != null);
		}
	}
	
	@Override
	public void addResource(T t){
		if(t==null){
			throw new IllegalArgumentException("Can't add null as " + clazz.getName());
		}
		resourceObjects.add(t);
	}
	
	@Override
	public List<T> getResources(){
		return new ArrayList<T>(resourceObjects);
	}
	
}
