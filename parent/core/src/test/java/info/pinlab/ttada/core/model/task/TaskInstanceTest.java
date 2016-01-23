package info.pinlab.ttada.core.model.task;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.ttada.core.model.TestResources;

public class TaskInstanceTest {
	static List<TaskInstance> taskis ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		taskis = TestResources.getResoursesFor(TaskInstance.class);
	}


	@Test
	public void testEquals() throws Exception {
		assert(taskis.size() > 0);
		TaskInstance prev = null;
		for(TaskInstance task : taskis){
			if (prev == null) continue;
			assertFalse(prev.equals(task));
			assertFalse(task.equals(prev));
			assertTrue(task.equals(task));
		}
	}
	
	
	@Test
	public void somethign(){
		assert(taskis.size() > 0);
		
		TaskInstance taski = taskis.get(0);
		Task task = taski.getTask();
		
		TaskInstance one = new TaskInstance(task, 12, 1);
		TaskInstance two = new TaskInstance(task, 12, 2);

		assertFalse(one.equals(two));
		assertFalse(two.equals(one));
	}

}
