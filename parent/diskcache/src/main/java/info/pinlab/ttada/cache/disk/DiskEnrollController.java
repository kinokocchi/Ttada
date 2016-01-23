package info.pinlab.ttada.cache.disk;

import info.pinlab.ttada.core.cache.Cache;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.control.EnrollManagerReqListener;
import info.pinlab.ttada.core.control.EnrollReqListener;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseSet;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.EnrollView;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * Features
 * 
 * <ul>
 *   <li> saves responses as to local file system
 *   <li> data is serialized to .json, saved as .gz
 *   <li> wraps/references a {@link ResponseSet} 
 *   <li> checks {@link ResponseSet} every 0.5 sec to save unsaved responses
 *   <li> has a {@link DiskCache} for caching
 *   <li> root path should be set in {@link DiskCache}
 *   <li> filenames are the objects' hashcodes
 * </ul>
 * 
 * <p>
 * Usage example:
 * <pre>
 * EnrollController diskEnroller = new DiskEnrollController(new ResponseSet());
 * diskEnroller.setCache(diskCache);
 * </pre>
 * </p>
 * 
 * @author Gabor Pinter
 *
 */
public class DiskEnrollController implements LocalEnrollController{
	public static Logger logger = Logger.getLogger(DiskEnrollController.class);
	
	private Cache cache = null;
	private final ResponseSet responseSet ;
	private EnrollView enrollView = null;
	
	private volatile int enrolledN = 0;
	private List<LocalSaveHook> hooks = new ArrayList<LocalSaveHook>();


	
	private volatile State state;

	private Thread enrollWorkerThread = null;
	private Thread enrollViewUpdateThread = null;
	
	private EnrollManagerReqListener enrollManager = null;

	public DiskEnrollController(){
		this(new ResponseSet());
	}
	public DiskEnrollController(ResponseSet rset){
		setState(State.NOT_STARTED);
		this.responseSet = rset;
	}
	
	
	
	
	synchronized public void addSaveHook(LocalSaveHook hook){
		for(LocalSaveHook registeredHook : hooks){
			if (hook.equals(registeredHook)){
				return;
			}
		}
		hooks.add(hook);
		return;
	}

	public List<LocalSaveHook> getSaveHooks(){
		List<LocalSaveHook> list = new ArrayList<LocalSaveHook>();
		list.addAll(this.hooks);
		return list;
	}

	
	
	
	/**
	 * Enrolls responses in a separate working thread.  <br>
	 * It may run after the main program is closed. <br>
	 * Reads responses from {@link info.pinlab.pinplayer.model.response.ResponseSet}  .
	 * 
	 * @author Gabor Pinter
	 *
	 */
	private class EnrollWorker implements Runnable{
		private long timeResInMs = 500; // in ms 
		
		@Override
		public void run(){
			Response resp = null;
			LOOP:while(true){
				resp = null;
				try {
					Thread.sleep(timeResInMs);
					if(responseSet==null){
						logger.error("Missing response set! What to enroll this way?");
						continue;
					}
					
					if(	getEnrolledN()  == responseSet.size() 
							&& responseSet.isSealed()){
							setState(State.COMPLETED);
						break LOOP;
					}
					if(getState() == State.INTERRUPTED){
						break LOOP;
					}
					
					
					if(!responseSet.hasNext()){
						//-- nothing to enroll, loop till it has
						timeResInMs = 1000;
						if(isEnrollCompleted()){
							break LOOP;
						}else{
							continue LOOP;
						}
					}
					timeResInMs = 100;
					resp = responseSet.next();
					if(resp!=null){
						setState(State.BUSY);
//						Thread.sleep(1500); //-- slow it down for testing!
						if(cache==null){
							logger.error("Not saving response to local cache ! '" + resp.toString() +"'");
						}else{
							cache.put(resp, Response.class);
						}
						//-- enroll via hooks
						for(LocalSaveHook hook: hooks){
							hook.save(resp);
						}
						resp=null;
						synchronized (DiskEnrollController.this){
							enrolledN++;
						}
						setState(State.IDLE);
					}
					
				} catch (InterruptedException e) {
					logger.info("EnrollWorkerThread was interrupted");
					
					if(resp!=null){ //-- put back this response
						responseSet.undoNext();
					}
					
					break LOOP;
				}
			} // :LOOP
			logger.info("EnrollWorkerThread is DONE");
			enrollWorkerThread=null;
		}
	}

	
	synchronized private int getEnrolledN(){
		return enrolledN;
	}
	
	
	private class EnrollViewUpdater implements Runnable{
		private long refreshInMs = 200; // in ms 
		
		@Override
		public void run() {
			while(enrollViewUpdateThread!=null){
				enrollView.setStoreItemMax(responseSet.size());
				enrollView.setStoredItemN(getEnrolledN());
				try {
//					logger.info("Updateing enroll view " + enrollViewUpdateThread);
//					System.out.println("Updateing!");
					Thread.sleep(refreshInMs);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	
	@Override
	public int getResponseN(TaskInstance taski) {
		if(responseSet==null)
			return 0;
		return responseSet.getUnrpocessedN();
	}

	
	@Override
	public void setEnrollView(EnrollView view) {
		enrollView = view;
		view.setEnrollReqListener(this);
		if(cache!=null){
			if(cache instanceof DiskCache ){
				enrollView.setLogMsg(((DiskCache)cache).getRootDir().getAbsolutePath());
			}
			if(cache instanceof RemoteCache){
				enrollView.setLogMsg(((RemoteCache)cache).getRemoteUri());
			}
		}
	}
	
	@Override
	public void setCache(Cache cache) {
		this.cache = cache;
		
		if(enrollView!=null){
			if(cache instanceof DiskCache ){
				enrollView.setLogMsg(((DiskCache)cache).getRootDir().getAbsolutePath());
			}
			if(cache instanceof RemoteCache){
				enrollView.setLogMsg(((RemoteCache)cache).getRemoteUri());
			}
		}
		logger.info("Setting cache '" + cache.getClass() +"'");
//		throw new IllegalArgumentException("Argument must be '" + DiskCache.class.getSimpleName() +"'");
	}
	
	@Override
	public void start(){
		logger.info("Starting Disk Enroller");
		
		setState(State.IDLE);
		if(enrollView == null){
			//-- add MOCK view -- does nothing --//
			enrollView = new EnrollView() {
				@Override public void showWarning(String ignore) {	}
				@Override public void showMessage(String ignore) {	}
				@Override public void showError(String ignore) {	}
				@Override public void setStoredItemN(int n) {	  }
				@Override public void setStoreItemMax(int n) {		}
				@Override public void setEnrollReqListener(EnrollReqListener l) {		}
				@Override public void dispose() {		}
				@Override public void setState(State state){		}
				@Override public void setLogMsg(String msg){	}
			};
		}
		if(enrollWorkerThread==null){
			enrollWorkerThread = new Thread(new EnrollWorker());
			enrollWorkerThread.start();
		}else{
			logger.warn("EnrollWorkerThread is already running!");
		}
		if(enrollViewUpdateThread==null){
			enrollViewUpdateThread = new Thread(new EnrollViewUpdater());
			enrollViewUpdateThread.start();
		}
	}

	
	@Override
	public boolean isEnrollCompleted(){
		final int n = getEnrolledN();
		System.out.println("Sealed: " + responseSet.isSealed() +"   " + n + " / " + responseSet.size() + " " + (responseSet.size() == n));
		
		if(responseSet.isSealed()       /* no more response registered */
			&& getEnrolledN() == responseSet.size() /* all registered responses are enrolled */ 
			){
			return true;
		}
		return false;
	}
	
	@Override
	public void stop(){
		enrollWorkerThread.interrupt();
		try {
//			System.out.println("Waiting for worker to join!");
			enrollWorkerThread.join();
		} catch (InterruptedException ignore) {
			System.out.println("Ignore: Waiting for worker to join!");
		}
	}

	@Override
	public int getPending() {
		return responseSet.getUnrpocessedN();
	}

	@Override
	public ResponseSet getResponseSet() {
		return responseSet;
	}
	
	public EnrollView getEnrollView(){
		return enrollView;
	}

	@Override
	synchronized public State getState() {
		return this.state;
//		return getCurrentState();
	}
	
	synchronized private void setState(State state){
		this.state = state;
		if(enrollView != null){
			enrollView.setState(state);
		}
	}
	
	
	@Override
	public void reqStartEnroll() {
		this.start();
	}
	@Override
	public void reqStopEnroll() {
		setState(State.INTERRUPTED);
		enrollView.showMessage("Stopping enrolling...");
		this.stop();
	}
	@Override
	public void reqFallbackEnroll(){
		if(enrollManager!=null){
			enrollManager.reqStartFallbackEnroll(this);
		}
	}
	
	
	@Override
	public void reqExitEnroll(){
		enrollManager = null;
		enrollWorkerThread = null;
		
		//-- kill view --//
		enrollViewUpdateThread = null;
		enrollView.dispose();
		
		setState(State.DISPOSABLE);
	}


	@Override
	public void setEnrollManagerReqListener(EnrollManagerReqListener l) {
		enrollManager = l;
	}



	@Override
	public Cache getCache() {
		return cache;
	}
	@Override
	synchronized public void dispose(){
		enrollWorkerThread = null;
		enrollViewUpdateThread = null;
		enrollView.dispose();
		
		setState(State.DISPOSED);
	}
}
