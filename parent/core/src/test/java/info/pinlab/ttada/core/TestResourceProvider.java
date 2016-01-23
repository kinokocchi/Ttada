package info.pinlab.ttada.core;

import java.util.List;

public interface TestResourceProvider<T> {
	public List<T> getResources();
	public void addResource(T t);
	public void testGetResources(); //-- this should be a test
}


