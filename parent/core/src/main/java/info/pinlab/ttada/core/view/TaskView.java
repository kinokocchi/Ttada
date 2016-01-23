package info.pinlab.ttada.core.view;

import java.util.Map;

import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.task.Task;


public interface TaskView{
	
	
	public void setTask(Task task);
	
	public void setTaskController(TaskController controller);
	
	/**
	 * In case forced stop (e.g., time limit), gets response.
	 * 
	 * @param response
	 */
	public ResponseContent getResponse( );
	
	/**
	 * For setting last response state.
	 * 
	 * @param response
	 */
	public void setState(Response response);
	
	/**
	 * For example to disable responses (over attemptN).
	 * 
	 * @param b
	 */
	public void setEnabled(boolean b);
	
	
	
	public void setTop(PlayerTopView topView);

	/**
	 * Get the association between models and views after they are set up.
	 * This can be important if views are created on the fly. 
	 * 
	 * @return
	 */
	public Map<ExtendedResource<?>, Object> getModelViewMap();
	
	
}
