package info.pinlab.ttada.session;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.PlayerDeviceFacotry;
import info.pinlab.pinsound.RecorderDeviceController;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.pinsound.app.AudioRecorder;
import info.pinlab.pinsound.app.AudioRecorderController;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.pinsound.app.AudioRecorderView;
import info.pinlab.ttada.cache.disk.DiskCache;
import info.pinlab.ttada.cache.disk.DiskEnrollController;
import info.pinlab.ttada.cache.disk.LocalSaveHook;
import info.pinlab.ttada.core.cache.Cache;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.control.AudioPlayRecController;
import info.pinlab.ttada.core.control.EnrollController;
import info.pinlab.ttada.core.control.EnrollManagerReqListener;
import info.pinlab.ttada.core.control.SessionController;
import info.pinlab.ttada.core.control.SessionControllerWithAudio;
import info.pinlab.ttada.core.control.SimpleEnrollManager;
import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.control.TaskControllerWithAudio;
import info.pinlab.ttada.core.model.HasAudio;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.ttada.core.model.response.ResponseSet;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.core.view.EnrollView;
import info.pinlab.ttada.core.view.EnrollViewFactory;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.core.view.RecordTaskView;
import info.pinlab.ttada.core.view.TaskView;
import info.pinlab.ttada.core.view.UserInteractionView;
import info.pinlab.ttada.session.Registry.Key;

public class SessionImpl implements SessionControllerWithAudio, SessionController, 
							EnrollManagerReqListener{
	public static Logger LOG = LoggerFactory.getLogger(SessionImpl.class);
	private Registry conf;
	private PlayerTopView topView = null;
	
//	private CacheManager cacheManager ;
	
	private TaskSet taskSet;
	private EnrollViewFactory enrollViewFactory;
	
	private PlayerDeviceFacotry playerDeviceFactory = null;
	private RecorderDeviceController recorderDevice = null;
	
	String userId = "unk";
	
	//-- created by all means - does not used by enrollers
	private ResponseSet parentResponseSet = new ResponseSet();
	private List<ResponseSet> responseSets ;
	
	private final Map<Integer, TaskInstance> taskInstances = new HashMap<Integer, TaskInstance>();

	//-- caches --//
	private final Map<TaskInstance, TaskView> taskViewCache = new HashMap<TaskInstance, TaskView>();
	private final Map<TaskInstance, TaskController> taskControllerCache = new HashMap<TaskInstance, TaskController>();

	
	private int currentTaskIx = 0; //-- always points to the NEXT member! //-- don't use to it!
	

	private final SimpleEnrollManager enrollManager;
	
	public SessionImpl(){
		this(Registry.getDefaultInstance());
		LOG.info("Created default Registry for Session");
	}
	
	public SessionImpl(Registry conf){
		this.conf = conf;
		enrollManager = new SimpleEnrollManager();
		responseSets = new ArrayList<ResponseSet>();
		responseSets.add(parentResponseSet);
	}

	synchronized public boolean hasNext(){
		if(taskSet==null)
			return false;
		if(currentTaskIx >= taskSet.size()){
			return false;
		}
		return true;
	}
	synchronized public boolean hasPrev(){
		if(currentTaskIx <= 1 ){
			return false;
		}
		return true;
	}
	
	private static boolean hasSound(TaskInstance taski){
		return hasSound(taski.getTask());

	}
	private static boolean hasSound(Task task){
		for (Display disp : task.getDisplays()){
			if(disp instanceof HasAudio){
				return true;
			}
		}
		return false;
	}
	
	private void displayTask(TaskInstance taski){
//		System.out.println("Display " + currentTaskIx + "/" + this.size());
//		System.out.println(taskViewCache.size());
		isStepNextAfterPlayingSet  = false;
		topView.getNaviView().setLabel(currentTaskIx + "/" + this.size());

		TaskView taskView = taskViewCache.get(taski);
		TaskController taskController  = taskControllerCache.get(taski);
		TaskControllerWithAudio taskControllerWithAudio = null;
//		TaskControllerWithAudio audioWrapperController = null;

		
		//-- controllers!
		if(taskController != null){
			topView.getNaviView().setStepController(taskController);

			if(taskView!=null){
				topView.setTaskView(taskView);
				//-- if taskcontroller & taskview is cached --//
				return;
			}
			//-- load audio
//			setAudio(taskController, taski);
		}else{ //-- not in cache: create!
			//-- init view and controller for task 
			String taskClazzName = taski.getTask().getClass().getSimpleName();
			LOG.debug("Setting display for '" + taskClazzName +"'");
			String controllClazzName = 
					TaskController.class.getPackage().getName() + "." 
					+ taskClazzName + "Controller";
			LOG.debug("Looking for class   '" + controllClazzName+"'");
			
			try{
				Class<?> controllClazz = Class.forName(controllClazzName);
				LOG.debug("Creating '" +controllClazzName +"' on the fly");
				taskController = (TaskController)controllClazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			

			if(SessionImpl.hasSound(taski)){
				taskControllerWithAudio = new TaskControllerWithAudio(taskController);
				taskControllerWithAudio.setSessionController(this);
				taskControllerWithAudio.setTaskInst(taski);
				taskControllerCache.put(taski, taskControllerWithAudio);
			}else{ //-- duplicating code.. not nice! ugly hack
				taskController.setSessionController(this);
				taskController.setTaskInst(taski);
				taskControllerCache.put(taski, taskController);
			}
		} //-- creating controller
		
		
		if(taskControllerWithAudio!=null){
			topView.getNaviView().setStepController(taskControllerWithAudio);
			//-- set task view on Top Window/Frame/Panel
			taskView = topView.setTaskView(taski.getTask());
			taskView.setTop(topView);
			taskView.setTaskController(taskControllerWithAudio);
		}else{
			topView.getNaviView().setStepController(taskController);
			//-- set task view on Top Window/Frame/Panel
			taskView = topView.setTaskView(taski.getTask());
			taskView.setTop(topView);
			taskView.setTaskController(taskController);
		}
		
		//-- set sound if necessary --//
//		System.out.println((taskController instanceof AudioPlayRecController));
//		System.out.println(taskController);
		if(taskController instanceof AudioPlayRecController){
			final AudioRecorder recorder = new AudioRecorder();
			recorder.setRecorderDevice(recorderDevice);
			LOG.debug("Setting sound device for '" +taskController.getClass().getName() +"'  " + recorder);
			((AudioPlayRecController)taskController).setAudioRecorder(recorder);

			AudioPlayer player = new AudioPlayer();
			player.setPlayerDevice(playerDeviceFactory.getPlayer());
			((AudioPlayRecController)taskController).setAudioPlayer(player);

			if(taskView instanceof AudioRecorderView ){
				recorder.setAudioRecorderView((AudioRecorderView) taskView);
			}
			if(taskView instanceof AudioPlayerView){
//				audioWrappedController.setAudioPlayerView((AudioPlayerView)taskView);
				player.setAudioPlayerView((AudioPlayerView)taskView);
			}
			if(taskView instanceof RecordTaskView && taskController instanceof AudioRecorderListener){
				((RecordTaskView)taskView).setRecActionListener((AudioRecorderListener)taskController);
			}
			if(taskView instanceof RecordTaskView && taskController instanceof AudioRecorderController){
				((AudioRecorderController)taskController).setAudioRecorderView(((RecordTaskView)taskView));
			}
		}
		
		//-- pair controller and wrapper
		if(taskControllerWithAudio!=null){
			taskControllerWithAudio.setView(taskView);
		}else{
			taskController.setView(taskView);
		}
		taskViewCache.put(taski, taskView);

	}
	
	
	
	public TaskInstance getTaskInstanceByIx(int ix){
		TaskInstance taski = null;
		if(taskInstances.containsKey(ix)){
			taski = taskInstances.get(ix);
		}else{
			final Task task = taskSet.get(currentTaskIx);
			taski = new TaskInstance(task, taskSet.hashCode(), currentTaskIx);
			taskInstances.put(currentTaskIx, taski);
		}
		return taski;
	}
	
	
	
	@Override
	public void setTaskSet(TaskSet tset) {
		taskSet = tset;
	}

	@Override
	public TaskSet getTaskSet(){
		return taskSet;
	}
	
	@Override
	public ResponseSet getResponseSet(){
		//TODO: it may be unsafe for 3rd party uses -> consider readonly interface for it
		//      responses are enrolled only through Session!!!
		return parentResponseSet;
	}
	
	@Override
	public void addEnrollController(EnrollController ec){
		enrollManager.addEnrollController(ec);

		if(enrollViewFactory!=null){
			EnrollView view = enrollViewFactory.buildEnrollView(ec);
			ec.setEnrollView(view);
			ec.setEnrollManagerReqListener(this);
		}		
		responseSets.add(ec.getResponseSet());
	}
	
	
	
	public void onBeforeStartSession(){
		//-- print 
		if(conf.getBoolean(Key.TASK_SET_JSON_IS_PRINT)){
			throw new IllegalStateException("Not Implemented: printing task set...");
//			System.err.println(
//					Initializer.localJsonSerializer.toJson(Initializer.tset)
//					);
		}
		
		//-- save TaskSet if needed
		if( conf.getBoolean(Key.TASK_SET_IS_SAVE_LOCALLY)
		&&  conf.getBoolean(Key.LOCAL_AVAILABLE)){
			boolean isSaved = false;
			for(EnrollController enroller : enrollManager.getEnrollControllers()){
				if(enroller instanceof DiskEnrollController){
					enroller.getCache().put(this.taskSet, TaskSet.class);
					isSaved = true;
					break;
				}
			}
			
			if(!isSaved){
				LOG.error("Couldn't save TaskSet locally! No DiskCache was found!");
			}
//			if(Initializer.cm == null){
//				LOG.warn("Cache manager is not set!");
//			}else{
//				LOG.info("Saving TaskSet to cache");
//				//TODO: is does not save locally!
//				Initializer.cm.cache(DiskCache.getCacheLevel(), Initializer.tset, TaskSet.class);
//			}
		}
//		//-- cache daemon
//		Initializer.diskEnroller.start(); //-- start caching daemon

	}
	
//	private boolean NO_GUI = false;
//	private boolean NO_ENROLL = false;

	/**
	 * Starts GUI, saves taskset 
	 * 
	 */
	@Override
	public void startSession(){
		//-- sanity check
		if (this.taskSet == null){
			throw new IllegalStateException("Task set is NULL!");
		}
		if (this.taskSet.size() < 1){
			throw new IllegalStateException("Task set size is ZERO!");
		}
		
//		enrollManager.startAllEnrolls();
		enrollManager.start();
		
//		if (topView == null){
//			if(conf.getBoolean(Key.PLAYER_HAS_GUI)){
////				NO_GUI = true;
//				throw new IllegalStateException("TopView is NULL! The session needs a view!");
//			}
//			LOG.info("Starting NO-GUI session");
//		}
		
		
		if(conf.getBoolean(Key.PLAYER_HAS_GUI)){
			if(topView==null){
				LOG.error("No gui is available!");
			}else{
				topView.startGui();
				
				for(EnrollController controller : enrollManager.getEnrollControllers()){
					topView.setEnrollViewVisible(controller.getEnrollView(), false);
				}
			}
			doNext();
		}else{
			LOG.info("no gui - shutting down");
			//-- no gui: do nothing
			doShutDown();
		}
	}

	@Override
	public void setTopView(PlayerTopView view) {
		this.topView = view;
	}
	
	boolean isStepNextAfterPlayingSet  = false;
	
	@Override
	public void doNext(){
		if(!hasNext()){
			reqCloseWindow();
			return;
		}
		
		TaskInstance taski = getTaskInstanceByIx(currentTaskIx);
		currentTaskIx++;
		displayTask(taski);
	}
	
	
	@Override
	public void doPrev() {
		if(!hasPrev()){
			LOG.debug("No previous task '" + currentTaskIx);
			return;
		}
		
		TaskInstance taski = getTaskInstanceByIx(currentTaskIx-2);
		currentTaskIx--;
		displayTask(taski);
	}
	
	@Override
	public int size(){
		return taskSet.size();
	}

	
	private ResponseHeader getRepsonseHeader(long respT, long timeStamp){
		TaskInstance taski = getTaskInstanceByIx(currentTaskIx-1);
		int attemptN = parentResponseSet.getAttemptCntForTaskInstance(taski);
		
		return new ResponseHeaderBuilder()
		.setTaskSetId(taski.getTaskSetHash())
		.setTaskId(taski.getTask().hashCode())
		.setTaskInstId(taski.hashCode())
		.setTaskIx(taski.getTaskIx())
		.setTaskBrief(taski.getTask().getBrief())
		//-- if taskSet is not empty!
		.setTaskSetBrief(taskSet.getBrief())
		.setTaskType(taski.getTask().getClass())
		.setSessionId(this.hashCode()+"")
		.setUsrId(this.getUserId())
		.setAttemptN(attemptN)
		.setTimeStamp(timeStamp)
		.build();
	}
	
	@Override
	public void enrollResponse(ResponseContent respCont){
		final ResponseHeader header = getRepsonseHeader(respCont.getResponseTime(), respCont.getTimeStamp());
		final Response resp = new Response(header, respCont);

		for(ResponseSet responseSet : responseSets){
			responseSet.add(resp);
		}
	}

	
	
	
	@Override
	public void reqAudioDevSelector(){
		if(topView!=null){
			List<String> deviceNames = new ArrayList<String>();
			if(deviceNames.size() > 0 && deviceNames.get(0) != null){
				int selectedDevNameIx = topView.showAudioDevSelector(deviceNames);
				LOG.debug("Selected audio device ix " + selectedDevNameIx + ".  '" + deviceNames.get(selectedDevNameIx) +"'");
			}else{
				LOG.warn("Device names are not available!");
			}
		}else{
			LOG.debug("Don't start Audio Dev selector as gui is NOT available!");
		}
	}

	@Override
	public void reqCloseWindow(){
		if(topView!=null){
			boolean confirmed = topView.showCloseConfirmDialog();
			if(confirmed){
				//-- seal responses --//
				for(ResponseSet rset : responseSets){
					rset.seal();
				}
				
				topView.setTaskWindowVisible(false);

				//-- show all views --//
				for(EnrollController controller : enrollManager.getEnrollControllers()){
					topView.setEnrollViewVisible(controller.getEnrollView(), true);
				}
				if(playerDeviceFactory!=null){
					playerDeviceFactory.stopAll();
				}
				
				new Thread(new ShutDownMonitor()).start();
			}
		}
	}
	
	
	
	
	
	@Override
	public boolean login(String id, char [] pwd){
		conf.put(Key.REMOTE_LOGIN_ID, id);
		conf.put(Key.REMOTE_LOGIN_PWD, String.valueOf(pwd));
		
		RemoteCache cache = SessionFactory.initRemoteCache(conf);
		if(cache==null){
			
			return false;
		}
		
		return true;
	}
			
	
	
	
	/**
	 * Runs after session is finished. 
	 * Shuts down everything if finished. 
	 * 
	 * @author Gabor Pinter
	 *
	 */
	private class ShutDownMonitor implements Runnable{
		private long timeResInMs = 500; // in ms 

		@Override
		public void run(){
			LOG.info("Shutdown initiated..");
			LOOP:while(true){
				try {
					Thread.sleep(timeResInMs);
				} catch (InterruptedException ignore ){}


				if(!enrollManager.isComplete()){ //-- wait till enrolls complete 
//					LOG.info("Enroll not complete yet..");
					continue LOOP;
				}else{
					if(topView.isEnrollViewVisible()){
						continue LOOP;
					}else{
						break LOOP;
					}
				}
			}
			doShutDown();
		}
	}

	
	
	private void doShutDown(){
		enrollManager.dispose();
		
		if(playerDeviceFactory!=null){
			playerDeviceFactory.stopAll();
		}

		if(topView!=null){
			topView.dispose();
		}
		
		if(playerDeviceFactory!=null){
			playerDeviceFactory.stopAll();
//			playerDeviceFactory.disposeAll();
		}
		//-- do shutdown house keeping if necessary
		LOG.info("Shutdown - complete");
	}
	
	
	@Override
	public void setAudioPlayerDeviceFactory(PlayerDeviceFacotry playerFactory) {
		LOG.info("Audio player device controller was set to '" + playerFactory +"'");
		playerDeviceFactory = playerFactory;
	}

	@Override
	public void setAudioRecorderDeviceController(RecorderDeviceController device) {
		LOG.info("Audio recorder device controller was set to '" + device +"'");
		recorderDevice = device;
//		audioRecorderController = device;
	}
	
	@Override
	public PlayerDeviceFacotry getAudioPlayerDeviceFactory() {
		return playerDeviceFactory;
	}

	@Override
	public RecorderDeviceController getAudioRecorderDeviceController() {
		return recorderDevice;
	}
	
	@Override
	public void setUserId(String usr){
		userId = usr;
		if(this.topView!=null){
			this.topView.setLabel(usr);
		}
	}

	@Override
	public String getUserId() {
		return userId;
	}


	
	public void reqStartFallbackEnroll(EnrollController failedEnroller){
		
		UserInteractionView ui = null;
		if(topView!=null && topView instanceof UserInteractionView){
			ui = (UserInteractionView) topView;
		}
		final ResponseSet rset = failedEnroller.getResponseSet();

		if(!rset.isSealed()){ //-- do nothing if not finished
			if(ui != null){
				ui.showWarning("Not yet!", "You need to finish your session first!");
			}
			return;
		}
		
		if(ui != null){
			final File saveDir = ui.showDirChooser("Choose directory to save your responses!");
			if(saveDir==null ){
				return;
			}
			if(!saveDir.isDirectory()){
				if(!saveDir.mkdirs()){
					if(ui != null){
						ui.showWarning("Wrong dir", "Couldn't create dir! Choose a different one!");
					}
					return;
				}
			}
			Cache cache = new DiskCache.DiskCacheBuilder((DiskCache)failedEnroller.getCache())
			.setDiskCacheRootPath(saveDir)
			.build();

			ResponseSet rsetCopy = new ResponseSet(rset);
			rsetCopy.seal();
			DiskEnrollController diskFallbackController = new DiskEnrollController(rsetCopy);
			diskFallbackController.setCache(cache);

			if(failedEnroller instanceof DiskEnrollController){
				for(LocalSaveHook hook : ((DiskEnrollController)failedEnroller ).getSaveHooks()){
					//-- create hook with new path
					hook.relocate(saveDir);
					diskFallbackController.addSaveHook(hook);
				}
			}
			
			addEnrollController(diskFallbackController);
			//-- todo how to hide etc. enroll views together with main windwo
			topView.setEnrollViewVisible(diskFallbackController.getEnrollView(), true);

			diskFallbackController.start();
		}
	}
	
	@Override
	public void setEnrollViewFactory(EnrollViewFactory factory) {
		enrollViewFactory = factory;
	}


}
