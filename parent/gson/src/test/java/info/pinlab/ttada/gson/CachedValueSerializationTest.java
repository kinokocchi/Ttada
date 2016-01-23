/**
 * 
 */
package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.Task;

import org.junit.Test;

/**
 * @author Gabor Pinter
 *
 */
public class CachedValueSerializationTest {

	
	
	@Test
	public void test(){
		//-- InfoTask uses cached displays
		InfoTask task = new InfoTask("This TextDisplay is cached");
		SerializerUtil.serializeAndCompare(task, InfoTask.class);
		SerializerUtil.serializeAndCompare(task, Task.class);
	}

}
