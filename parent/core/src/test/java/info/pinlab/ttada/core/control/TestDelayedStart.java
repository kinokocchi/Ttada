package info.pinlab.ttada.core.control;

import static org.mockito.Mockito.mock;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDelayedStart {

	public static class AbstractTaskControllerImpl extends AbstractTaskController{
		//-- testing abstract Task only: no extra sta--//
	}


	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		BasicConfigurator.resetConfiguration();
//		BasicConfigurator.configure();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		BasicConfigurator.resetConfiguration();
	}

	
	@Before
	public void setUp() throws Exception {
		final TaskController taskController = new AbstractTaskControllerImpl();
		SessionController sessionControllerMock = mock(SessionController.class);
		taskController.setSessionController(sessionControllerMock);
		
		TaskSet tset = new TaskSet();
		for(int i = 0 ; i < 10 ; i++){
			Task task = new InfoTask("");
			task.setStepRule(
					new StepRuleBuilder()
					.setTimeout(2000)
					.build());
			tset.add(task);
		}
		
//		TaskInstance taski = new TaskInstance(task, 0, 0);
//		taskController.setTask(taski);
////		taskController.enrollResponse();
//		taskController.reqNext();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		
		int MAX_THREAD_N = 10; 
		
		for(int i = 0; i < MAX_THREAD_N ; i++){
			Thread thread = new Thread("Auto thread " + i){
				public void run(){
//					taskController.reqNextByUsr();
				}
			};
			thread.start();
		}
	}

}
