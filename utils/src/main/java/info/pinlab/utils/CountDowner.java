package info.pinlab.utils;


/**
 * 
 * Counts down and calls Runnable at the end :-)
 * 
 * <pre>
 * {@code
 * CountDowner t = new CountDowner(1200, new Runnable() {
 *     &#64;Override
 *     public void run() {
 *        System.out.println("Booya!");
 *      }
 *    });
 * t.start();
 * }
 * </pre>
 * 
 * @author Gábor PINTÉR
 *
 */
public class CountDowner implements Runnable{
	long t0;
	private final int maxT;
	private final Runnable run;
	long res = 30; /*ms*/
	boolean cancelRunOnInterrput = true;
	
	public CountDowner(int waitT, Runnable r){
		run = r;
		maxT = waitT;
	}
	public void setResolution(long ms){
		res = ms;
	}
	public void isCancelRunOnInterrput(boolean b){
		cancelRunOnInterrput = b;
	}
	
	volatile boolean isCancelled = false;
	
	synchronized public void interrupt(){
		isCancelled = true;
	}
	synchronized public boolean isInterrupted(){
		return isCancelled;
	}
	
	@Override
	public void run(){
		t0 = System.currentTimeMillis();
		while(!isInterrupted()){
			try {
				Thread.sleep(res);
			} catch (InterruptedException e) {
				interrupt();
				e.printStackTrace();
			}
			long delta = System.currentTimeMillis()-t0;
			if(delta >= maxT)
				break;
		}
		if(isInterrupted() && cancelRunOnInterrput){
			//-- don't run the Runnable --//
		}else{
			run.run();
		}
	}
	public void start(){
		new Thread(this).start();
	}
}
