package info.pinlab.ttada.core.control;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;


//-- testing step rules with Mockito!
public class AbstractTaskControllerTest {

	TaskController taskController = null;
	//-- sessionController will be the mock
	//-- does nothing -> but check if called 
	SessionController sessionControllerMock = null;

	public static class AbstractTaskControllerImpl extends AbstractTaskController{

		@Override
		public void onBeforeNext() {
			// TODO Auto-generated method stub
			
		}
		//-- testing abstract Task only: no extra sta--//
	}
	
	
	public interface IF{
		
	}
	
	Task task;
	
	@Mock private IF iff;
	@Mock private SessionController sessionController3;
	
	@BeforeClass
	public static void setUpClass(){
//		BasicConfigurator.resetConfiguration();
//		BasicConfigurator.configure();
	}
	
	
	
	@Before
	public void setUp() throws Exception {
		//-- setup MOCK for session controller
//		Mockito.mock(SessionController.class);
//		Class<SessionController> 
		MockitoAnnotations.initMocks(AbstractTaskControllerTest.class);
		
		
		sessionControllerMock = mock(SessionController.class);
		
		task = new InfoTask("useless task");

		taskController = new AbstractTaskControllerImpl();
		taskController.setSessionController(sessionControllerMock);
	}
	
	
	
	@Test
	public void testReqNextAndPrevByUsr() throws Exception {
		task.setStepRule(new StepRule.StepRuleBuilder().setNextByUsr(false).setNextByResp(false).build());
		TaskInstance taski = new TaskInstance(task, 0, 0);
		taskController.setTaskInst(taski);
		System.out.println("Test " + taski.getTask().getStepRule().isNextByUsr());
		
		taskController.reqNextByUsr();
		verify(sessionControllerMock, never()).getResponseSet(); //-- from which attemptN is obtained (not a good way!)
		verify(sessionControllerMock, never()).doNext(); //-- check if doNext is NOT called (without answer!)

//		taskController.get
		
		task.setStepRule(new StepRule.StepRuleBuilder().setNextByUsr(true).setNextByResp(false).build());
		taski = new TaskInstance(task, 0, 0);
		System.out.println("Test " + taski.getTask().getStepRule().isNextByUsr());
		assertTrue(taski.getTask().getStepRule().isNextByUsr());
		taskController.enrollResponse(new ResponseContentEmpty());
		taskController.reqNextByUsr();
		if(task.isResponsible()){
			verify(sessionControllerMock).getResponseSet();
		}else{
			verify(sessionControllerMock, never()).getResponseSet();
		}
//		csessionControllerMock).doNext(); //-- check if doNext is called (with answer!)

		
		
		taskController.reqPrevByUsr();
		verify(sessionControllerMock).doPrev();
	}
	
	
	
	@Test
	public void testReqNextByUsrThatDenied() throws Exception{
		//-- set rule that can't step
		StepRule cantStepNextRule = new StepRuleBuilder().setNextByUsr(false).build();
		task.setStepRule(cantStepNextRule);
		TaskInstance taski = new TaskInstance(task, 0, 0);
		taskController.setTaskInst(taski);

		//-- Action : req by user
		taskController.reqNextByUsr();
		verify(sessionControllerMock, never()).doNext(); //-- check if doNext is NOT called
		//-- Action : req by program
		taskController.reqNext(); //-- this one should be called
		verify(sessionControllerMock).doNext(); //-- check if doNext is called
	}
	

	
	@Test
	public void testNextByResponse(){
		StepRule srule = new StepRuleBuilder().setNextByResp(true).build();
		task.setStepRule(srule);
		TaskInstance taski = new TaskInstance(task, 0, 0);
		taskController.setTaskInst(taski);
		
		//-- check if doNext is invoked when response enrolled 
		taskController.enrollResponse(new ResponseContentEmpty(0, 0));
		//-- enroll is in different thread, so use timeout with a window during which doNext() could be called
		verify(sessionControllerMock, timeout(500).times(1)).doNext(); //-- check if doNext is called
	}
	
	
	
	
	@Test
	public void testNoNextByResponse(){
		StepRule noStepRule = new StepRuleBuilder().setNextByResp(false).build();
		task.setStepRule(noStepRule);
		TaskInstance taski = new TaskInstance(task, 0, 0);
		taskController.setTaskInst(taski);
		
		//-- check if doNext is invoked when response entered 
		taskController.enrollResponse(new ResponseContentEmpty(0, 0));
		//-- enroll is in different thread, so use timeout!
		verify(sessionControllerMock, timeout(500).times(0)).doNext(); //-- check if doNext is called
	}
	

	@Test
	public void testTimeredStep(){
		int timeoutInMs = 1000;
		StepRule timedStepRule = new StepRuleBuilder().setTimeout(timeoutInMs).build();
		task.setStepRule(timedStepRule);
		TaskInstance taski1 = new TaskInstance(task, 0, 0);
		taskController.setTaskInst(taski1);
		
		//--- start the task : and timer
		taskController.onViewVisible();
		//-- check that doNext() is NOT called pre-maturely
		verify(sessionControllerMock, timeout(timeoutInMs-100).times(0)).doNext(); //-- check if doNext is called
		//-- than check that it IS called after timeout
		verify(sessionControllerMock, timeout(timeoutInMs+500).times(1)).doNext(); //-- check if doNext is called
	}
}


