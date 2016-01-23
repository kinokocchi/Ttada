package info.pinlab.ttada.core.control;


/**
 * To be dispatched from EnrollView. 
 * 
 * @author Gabor Pinter
 *
 */
public interface EnrollReqListener {
	
	public void reqStartEnroll();
	/** 
	 * Cancel Enroll process (e.g., to server is down)
	 */
	public void reqStopEnroll();
	/**
	 * If primary enroll is not working (e.g., server is down, local fs is full)
	 */
	public void reqFallbackEnroll();
	/** 
	 * when really finished - no coming back
	 */
	public void reqExitEnroll();

}
