package info.pinlab.ttada.core.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.HasAudio;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.TaskView;


/**
 * Wraps TaskController types with with audio functionality.
 * 
 * @author Gabor Pinter
 *
 */
public class TaskControllerWithAudio implements TaskController{
	public static Logger logger = LoggerFactory.getLogger(TaskControllerWithAudio.class);
	private SessionControllerWithAudio sessionControllerWithAudio = null;

	
	List<PlayerController> playerTokens = new ArrayList<PlayerController>();
	Map<Object, PlayerController> modelPlayerMap = new HashMap<Object, PlayerController>();
	private final TaskController taskController;
	
	public void addPlayerToken(PlayerController token){
		AudioPlayer player = token.getAudioPlayer();
		if(player!=null){
			player.setAfterPlayHook(new Runnable() {
				@Override
				public void run() {
					if ( getEnrolledN()>0){
						//-- enroll a zero: this will trigger stepping on AbstractTaskController
//						TaskControllerWithAudio.this.enrollResponse(new ResponseContentEmpty());
					}
				}
			});
		}
		playerTokens.add(token);
	}
	
	public TaskControllerWithAudio(TaskController taskController){
		this.taskController = taskController;
	}
	
	
	@Override
	public void setSessionController(SessionController sessionController) {
		if(sessionController instanceof SessionControllerWithAudio){
			this.sessionControllerWithAudio =  (SessionControllerWithAudio) sessionController;
		}
		taskController.setSessionController(sessionController);
	}

	@Override
	public void reqNext(){
		for(PlayerController playerToken : playerTokens){
			playerToken.reqStop();
		}
		taskController.reqNext();
	}
	
	@Override
	public void reqPrev(){
		for(PlayerController playerToken : playerTokens){
			playerToken.pausePlaying();
		}
		taskController.reqPrev();
	}
	@Override
	public void reqNextByUsr() {
		for(PlayerController playerToken : playerTokens){
			if( playerToken.isPlaying()){
				//-- can't step if playing
				return;
			}
		}
		taskController.reqNextByUsr();		
	}
	
	
	@Override
	public void reqPrevByUsr() {
		for(PlayerController playerToken : playerTokens){
			if( playerToken.isPlaying()){
				//-- can't step if playing
				return;
			}
		}
		taskController.reqPrevByUsr();		
	}

	
	@Override
	public void setTaskInst(TaskInstance taski){
		for(Display disp : taski.getTask().getDisplays()){
			if(disp instanceof HasAudio){
				WavClip wav = ((HasAudio)disp).getAudio();
				AudioPlayer player = new AudioPlayer();
				if(wav==null){ //-- create empty player if no wav is available
					player.setPlayerDevice(
							sessionControllerWithAudio.getAudioPlayerDeviceFactory().getPlayer());
				}else{ //-- if has wav: create player with audio
					player.setPlayerDevice(
							sessionControllerWithAudio.getAudioPlayerDeviceFactory().getPlayer(wav));
					player.setAudio(wav);
				}

				AudioRule arule = ((HasAudio) disp).getAudioRule();

				PlayerController playerToken = new PlayerController(player);
				playerToken.setModel(disp);
				playerToken.setAudioRule(arule);
				modelPlayerMap.put(disp, playerToken);
				this.addPlayerToken(playerToken);

			}
		}
		taskController.setTaskInst(taski);
	}
	
	@Override
	public void onViewVisible(){
		for(PlayerController playerToken : playerTokens){
			playerToken.onViewVisible();
		}
		taskController.onViewVisible(); //-- calls parent's onViewVisible()
	}
	
	@Override
	public void onBeforeNext() {
		for(PlayerController playerToken : playerTokens){
			playerToken.pausePlaying();
		}
		resetEnrolledN();
	}

	@Override
	public void setEnrollController(EnrollController enrollController) {
		taskController.setEnrollController(enrollController);
	}

	@Override
	public int getAttemptN(){
		return taskController.getAttemptN();
	}

	@Override
	public StepRule getStepRule() {
		return taskController.getStepRule();
	}

	@Override
	public void setView(TaskView view){
		//-- set audio players
		for(Map.Entry<ExtendedResource<?>, Object> pair : view.getModelViewMap().entrySet()){
			ExtendedResource<?> model = pair.getKey();
			Object modelView = pair.getValue();
			
			PlayerController playerToken = modelPlayerMap.get(model);
//			System.out.println(modelView.getClass() + "\t" + (modelView instanceof AudioPlayerView) + "\t" + playerToken);
			if(playerToken!=null && modelView instanceof AudioPlayerView){
				((AudioPlayerView)modelView).setPlayActionListener(playerToken);
				playerToken.setAudioPlayerView((AudioPlayerView)modelView);
			}
		}
		//set view for audio
		taskController.setView(view);
	}
	

	private volatile int enrolledN = 0;
	
	synchronized void resetEnrolledN(){
		enrolledN =0;
	}
	synchronized void incrEnrolledN(){
		enrolledN++;
	}
	synchronized int getEnrolledN(){
		return enrolledN;
	}
	
	
	@Override
	public void enrollResponse(ResponseContent respContent){
		//-- in order to avoid playing several files at the same time:
		for(PlayerController playerToken : playerTokens){
			if(playerToken.isPlaying()){
				//-- silently enroll
				incrEnrolledN();
				taskController.getSessionController().enrollResponse(respContent);
				//-- don't step or anything
				return;
			}
		}
		taskController.enrollResponse(respContent);
	}

	@Override
	public SessionController getSessionController() {
		return taskController.getSessionController();
	}
}

