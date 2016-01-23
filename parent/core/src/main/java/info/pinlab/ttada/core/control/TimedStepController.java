package info.pinlab.ttada.core.control;


/**
 * 
 * Calls reqNext() aftern n milliseconds.  
 * Swing has some similar timer
 * 
 * @author Gabor Pinter
 *
 */
public class TimedStepController implements Runnable{
	private final int timeout; 
	private final TaskController controller;
	Thread thread;
	private final int refreshRateInMs = 10; /*ms*/
	
	public TimedStepController(int timeout, TaskController controller){
		this.timeout = timeout;
		this.controller = controller;
	}

	@Override
	public void run(){
		final long t0 = System.currentTimeMillis();
		thread = Thread.currentThread();
		while(!Thread.interrupted()){ 
			try {
				Thread.sleep(refreshRateInMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			long t1 = System.currentTimeMillis();
			if(t1-t0 >= timeout){
				break;
			}
		}
		controller.reqNext();
	}
}
