package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseSet;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.core.view.EnrollViewFactory;
import info.pinlab.ttada.core.view.PlayerTopView;

public interface SessionController extends SessionUserReqListener{
	
	//-- settings things from above --//
	public void setTaskSet(TaskSet tset);
	public TaskSet getTaskSet();
	
	public ResponseSet getResponseSet();
	
	
	public String getUserId();
	public void setUserId(String usr);
	
	/**
	 * Adds enroll controller. {@link SessionController} sets view to {@link EnrollController}. 
	 * 
	 * @param enroller
	 */
	public void addEnrollController(EnrollController enroller);
	
	public void setTopView(PlayerTopView view);
	public void enrollResponse(ResponseContent respCont);
	
	public void startSession();
	
	public void doPrev();
	public void doNext();
	
	public void setEnrollViewFactory(EnrollViewFactory factory);

	public int size();
	
	
	
	
//	public int getAttemptN();
	
	/**
	 * End session - init hooks before dispose.
	 */
//	public void endTaskSession();
	
	
	/**
	 * 
	 * @return a session specific implementation of the interface
	 */
//	public Object getImplementaction(Class<?> interfaceClass);
	
	
	
}
