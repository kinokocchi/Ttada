package info.pinlab.ttada.core.control;

import java.util.List;


//-- manages several enrollControllers
public interface EnrollControllerManager{

	public void startAllEnrolls();
	public void startOneEnroll(EnrollController ec);
	public void addEnrollController(EnrollController ec);
	
	public void setFallbackEnrollController(EnrollController controller, EnrollController fallback);
	public void setFallbackEnrollControllerClass(EnrollController controller, Class<? extends EnrollController> fallbackClass);
	
	public EnrollController getFallbackEnroll(EnrollController failedController);
	public Class<? extends EnrollController> getFallbackEnrollClass(EnrollController failedController);
	
	/**
	 * If give controller failed, try another one
	 */
//	public void startFallbackEnroll(EnrollController failedEnroller);
	
	public boolean isEnrollComplete();
	public List<EnrollController> getEnrollControllers();
	
	
}
