package info.pinlab.ttada.core.view;

import info.pinlab.ttada.core.control.EnrollController;
import info.pinlab.ttada.core.control.EnrollReqListener;

/**
 * 
 * Visual interface to show upload progress.
 * 
 * @author Gabor Pinter
 *
 */
public interface EnrollView extends Notification{
	
	public void setEnrollReqListener(EnrollReqListener l);
	
	public void setStoreItemMax(int n);
	public void setStoredItemN(int n);
	
	public void setState(EnrollController.State state);
	
	
	public void dispose();
}
