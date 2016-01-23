package info.pinlab.ttada.core.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Handles several {@link EnrollController} instances 
 * 
 * @author Gabor Pinter
 *
 */
public class SimpleEnrollManager {
	public static Logger LOG = LoggerFactory.getLogger(SimpleEnrollManager.class);
	
	List<EnrollControllerInsanceWrapper> wrappers
	;
	
	private static class EnrollControllerInsanceWrapper{
		private EnrollController controller;
		boolean isActive = true;
		
		EnrollControllerInsanceWrapper(EnrollController ec){
			this.controller = ec;
		}
		
		void isActive(boolean b){
			this.isActive = b;
		}
		/**
		 * Starts enroll manager if active.
		 *   
		 * @return true if controller is started
		 */
		boolean start(){
			if(isActive){
				controller.start();
				return true;
			}
			return false;
		}
		void stop(){
			controller.stop();
		}
		boolean isComplete(){
			EnrollController.State state = this.controller.getState();
			if (state.equals(EnrollController.State.COMPLETED)
			|| state.equals(EnrollController.State.DISPOSABLE)){ //-- disposable
				return true;
			}
			return false;
		}
		boolean isInterrupted(){
			return EnrollController.State.INTERRUPTED.equals(controller.getState());
		}
		
		
		boolean isDisposed(){
			return EnrollController.State.DISPOSED.equals(controller.getState());
		}
		
		void dispose(){
			controller.dispose();
		}
		
		boolean isDisposable(){
			return EnrollController.State.DISPOSABLE.equals(this.controller.getState());
		}
		
//		EnrollControllerInsanceWrapper getFallback(){
//			return fallbackController;
//		}
	}
	
	
	
	public SimpleEnrollManager (){
		wrappers =new ArrayList<EnrollControllerInsanceWrapper>();
	}
	
	public void addEnrollController(EnrollController ec){
		for(EnrollControllerInsanceWrapper wrapper: wrappers){
			if(ec.equals(wrapper.controller)){
				//-- don't add things twice 
				LOG.warn("Not adding controller, as already in the pool!");
				return;
			}
		}
		wrappers.add(new EnrollControllerInsanceWrapper(ec));
	}

	
	void isActive(EnrollController ec, boolean b){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			if(ec.equals(wrapper.controller)){
				wrapper.isActive(b);
				return;
			}
		}
	}

	
	
	public EnrollController getFallbackController(EnrollController controller){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			if(controller.equals(wrapper.controller)){
				return wrapper.controller; 
			}
		}
		return null;
	}
	
	/**
	 * Starts EnrollControllers (each in has separate thread)
	 */
	public void start(){
		if (this.wrappers.size() < 1 ){
			LOG.warn("No response enroller is present (responses are not saved)");
		}

		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			wrapper.start();
		}
	}
	
	
	public List<EnrollController> getEnrollControllers(){
		List<EnrollController> controllers = new ArrayList<EnrollController>();
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			controllers.add(wrapper.controller);
		}
		return controllers;
	}

	
	/**
	 * Completed or interrupted 
	 * 
	 * @return
	 */
	public boolean isComplete(){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			if(!wrapper.isComplete() && !wrapper.isInterrupted()){
				return false;
			}
			
		}
		return true;
	}
	
	
	
	public boolean isDisposed(){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			if(!wrapper.isDisposed()){
				return false;
			}
		}
		return true;
	}
	
	
	
	public void stop(){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			wrapper.stop();
		}
	}
	
	
	public boolean isDisposable(){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			if(!wrapper.isDisposable()){
				return false;
			}
		}
		return true;
	}
	public void dispose(){
		for(EnrollControllerInsanceWrapper wrapper : wrappers){
			wrapper.dispose();
		}
	}
}
