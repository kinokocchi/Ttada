package info.pinlab.ttada.core.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentMulti;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.TaskView;


/**
 * 
 * Implements all usual task controlling acts
 * 
 * @author Gabor Pinter
 *
 */
public abstract class AbstractTaskController implements TaskController{
	public static Logger LOG = LoggerFactory.getLogger(AbstractTaskController.class);
	SessionController sessionController = null;
	TaskView view = null;
	Task currentTask = null;
	TaskInstance taski = null;
	EnrollController enroller;
	TimedStepController timedStepController = null;
//	private boolean isNextByResp = true;
	
	int attemptN = 0;
	
	private volatile boolean isNextByUsrRqst = false; //-- as opposed to enroll request
	
	synchronized boolean isNextReq(){
		return isNextReq();
	}
	
	@Override
	public void reqNext(){
		if(view!=null){
			view.setEnabled(false);
			isNextByUsrRqst = true;  // in order to avoid stepping while enrollResponse
			final ResponseContent respContent = view.getResponse();
			if(respContent instanceof ResponseContentMulti){
				ResponseContentMulti multiResp = (ResponseContentMulti) respContent;
				//-- enroll all separately!
				for(ResponseContent cont : multiResp){
					enrollResponse(cont);
				}
				
			}else{
				enrollResponse(respContent);
			}
			isNextByUsrRqst = false;
		}
		sessionController.doNext();
	}

	@Override
	public void reqPrev(){
//		System.out.println("Req prev! " + sessionController);
//		view.setEnabled(false);
//		ResponseContent respContent = view.getResponse();
//		enrollResponse(respContent);
		
		sessionController.doPrev();
//		if(sessionController!=null){
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
////					sessionController.doPrev();
//				}
//			}).start();		
//		}
	}

	@Override
	public void reqNextByUsr(){
		StepRule srule = currentTask.getStepRule();
//		System.out.println(" AbsCont " + currentTask + " " +  srule.isNextByUsr());

		if(!srule.isNextByUsr()){
			LOG.debug("Step forward not allowed for user by StepRule!");
			return;
		}
		//-- no next if no response! (don't skip!)

		
		if (currentTask.isResponsible() 
				&& sessionController.getResponseSet()
				.getAttemptCntForTaskInstance(taski)  == 0
				){ //-- no response enrolled!!!
			LOG.debug("Can't step without a response!");
			return;
		}
		
		if(view!=null){	
			view.setEnabled(false);
			ResponseContent respContent = view.getResponse();
			if(respContent!=null){
				isNextByUsrRqst = true;
				enrollResponse(respContent);
				isNextByUsrRqst = false;
			}
		}
		this.onBeforeNext();  //-- implemented by tasks
		sessionController.doNext();
	}

	@Override
	public void reqPrevByUsr() {
		StepRule srule = currentTask.getStepRule();
//		System.out.println("HEllo " + srule);
		if(srule!=null && !srule.isPrevByUsr()){
			LOG.debug("Step backward is not allowed for user by StepRule!");
			return;
		}
		reqPrev();
	}

	@Override
	public void setSessionController(SessionController sessionController) {
		this.sessionController = sessionController;
	}

	public SessionController getSessionController(){
		return this.sessionController;
	}
	
	
	@Override
	public void setView(TaskView view) {
		this.view = view;
	}

	
	@Override
	public void setTaskInst(TaskInstance taski){
//		System.out.println("SET  TASK");
		
		this.taski = taski;
		this.currentTask = taski.getTask();
		
		int timeout = currentTask.getStepRule().getTimeout();
		if(timeout > 0){
			timedStepController = new TimedStepController(timeout, this);
		}
		
		if(view!=null)
			view.setTask(currentTask);
		if(enroller!=null){
			attemptN = enroller.getResponseN(taski);
		}
	}
	
	public void setEnrollController(EnrollController enrollController){
		enroller = enrollController;
	}
	
	
	public void onViewVisible(){
//		System.out.println("Visible!");
		if(timedStepController!=null){
			new Thread(timedStepController).start();
		}
	}
	
	public int getAttemptN(){
		return attemptN;
	}

	public StepRule getStepRule(){
		return currentTask.getStepRule();
	}
	
	@Override
	public void enrollResponse(final ResponseContent respContent){

		if(currentTask.getStepRule().getMaxAttempt() >= 0){  //--) there is a constraint
			if(attemptN >=  currentTask.getStepRule().getMaxAttempt()){    //-- too many attempts
				if(view!=null)	view.setEnabled(false);
				//-- no enrolling --//
				return;
			}else{
				if(view!=null)	view.setEnabled(true);
			}
		}else{ //-- there is NO maxAttempt constraint
			if(view!=null)	view.setEnabled(true);
		}
		attemptN++;
		System.out.println(">>> Attempt : "+ attemptN);
		if(sessionController!=null){
			sessionController.enrollResponse(respContent);	
			//-- STEP or not? -> step only if enroll-only request, 
			//             don't step here for step request -> step is handled reqNexByUsr  
			if(!isNextByUsrRqst && currentTask.getStepRule().isNextByResp()){
				this.onBeforeNext();
				sessionController.doNext();
			}
		}else{ //-- no reponse enroller
			LOG.warn("No response enroller set!");
		}
	}
	

	@Override
	public void onBeforeNext() {
		
	}
}
