package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.TaskView;

/**
 * @author Gabor Pinter
 *
 */
public interface TaskController extends StepReqListener{

	//-- Called from session --//
	public void setSessionController(SessionController sessionController);
	public void setEnrollController(EnrollController enrollController);

	public int getAttemptN();
	
	public StepRule getStepRule();
	
	public void setView(TaskView view);
	public void setTaskInst(TaskInstance taski);
	public void onBeforeNext();
	
	//-- called from GUI --//
	public void onViewVisible();
	public void enrollResponse(ResponseContent respContent);
	
	
}


