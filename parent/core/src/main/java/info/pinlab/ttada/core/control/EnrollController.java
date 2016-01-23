package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.cache.Cache;
import info.pinlab.ttada.core.model.response.ResponseSet;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.EnrollView;

public interface EnrollController extends EnrollReqListener {
//	public void enrollResponse(Response resp);
//	public int getResponseN(ResponseHeader respHdr);

	public int getResponseN(TaskInstance task);
	
	enum State{
		NOT_STARTED,//-- before starting
		IDLE, 		//-- waiting for data to process 
		BUSY,	 	//-- saving or uploading  
		COMPLETED, 	//-- all enrolled 
		INTERRUPTED, //-- may be non-enrolled items 
		DISPOSABLE,	 //-- ready to kill it, after it is completed
		DISPOSED	 //-- disposed : dead 
	}
	
	/**
	 * Number of pending enroll operations. 
	 */
	public int getPending();
	
	public State getState();
	
	/**
	 * 
	 * @return false either if ResponseSet is not sealed, or still unprocessed are there 
	 */
	public boolean isEnrollCompleted();
	
	
	public void setCache(Cache cache);
	public Cache getCache();
	
	public ResponseSet getResponseSet();
	
	/**
	 * Start / stop enrolling 
	 */
	public void start();
	public void stop();
	
	
	/**
	 * Set enroll display (GUI). 
	 * 
	 * @param view
	 */
	public void setEnrollView(EnrollView view);
	public EnrollView getEnrollView();
	
	
	
	public void setEnrollManagerReqListener(EnrollManagerReqListener l);
	
	
	public void dispose();
	
}
