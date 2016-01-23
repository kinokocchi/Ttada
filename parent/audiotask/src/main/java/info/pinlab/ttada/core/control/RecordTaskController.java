package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.HasAudioRule;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentAudio;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.view.RecordTaskView;
import info.pinlab.ttada.core.view.TaskView;
import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.pinsound.app.AudioRecorder;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.pinsound.app.AudioRecorderView;


public class RecordTaskController extends AbstractTaskController
implements AudioRecorderListener, AudioPlayerListener, AudioPlayRecController{
	
	private AudioRule aRecRule = null;
	private AudioRecorder recorder = null;
	private AudioPlayer recordPlayer = null;
	
	private long recStartT = -1;
	private long recStopT = -1;
	
	private RecordTaskView view = null;
	
	public RecordTaskController(){}
	int recN = 0;
	

	
	@Override
	public void reqNextByUsr(){
		if(  (recorder!=null && recorder.isRecording())
		  || (recordPlayer!=null && recordPlayer.isPlaying())){
			return;
		}
		super.reqNextByUsr();
	}
	
	
	@Override
	public void reqPrevByUsr(){
		if(  (recorder!=null && recorder.isRecording())
				  || (recordPlayer!=null && recordPlayer.isPlaying())){
					return;
				}
		super.reqPrevByUsr();
	}
	

	
	@Override
	public void setView(TaskView view) {
		super.setView(view);
		this.view = (RecordTaskView) view;
		if(view instanceof AudioPlayerView){
			if(recordPlayer!=null){
				recordPlayer.setAudioPlayerView((AudioPlayerView) this.view);
				((AudioPlayerView) this.view).setPlayActionListener(this);
			}
		}
		if(view instanceof AudioRecorderView){
			if(recorder != null){
				recorder.setAudioRecorderView((AudioRecorderView)this.view);
			}
		}
	}
	

	@Override
	public void setTaskInst(TaskInstance taski){
		final Task task = taski.getTask();
		if (task instanceof HasAudioRule){
				this.setAudioRecRule(((HasAudioRule)task).getAudioRule());
		}
		super.setTaskInst(taski);
	}


	public ResponseContent getRecording(){
		if(recorder!=null){
			WavClip wav = recorder.getWavClip();
			if(wav==null){
				LOG.warn("No wav available from recorder, but recorder is set! " + this);
				return new ResponseContentEmpty(recStopT, (int)(recStopT - recStartT));
			}else{
				ResponseContentAudio respAudio = new ResponseContentAudio(recStopT, 
						(int)(recStopT - recStartT), wav);
				return respAudio;
			}
		}else{
			LOG.error("No audio recorder is set!");
			return new ResponseContentEmpty(recStopT, (int)(recStopT - recStartT));
		}
	}
	
	
	
	
	
	@Override
	public void reqRecStart(){
		if(aRecRule != null){
			if(!aRecRule.canRec){
				LOG.warn("User can't record on this task!");
				return;
			}
			if(aRecRule.maxRecN >= 0 && recN > aRecRule.maxRecN ){
				LOG.warn("Too many recording tries! " + recN + "/" + aRecRule.maxRecN);
				return;
			}
		}
		view.setRecMaxPosInMs(aRecRule.maxRecLen);
		recorder.setMaxRecLenInMs(aRecRule.maxRecLen);

		recStartT = System.currentTimeMillis();
		if(recorder==null){
//			stepLock(false);
			LOG.error("No recorder available!");
			return;
		}
		if(recorder.isRecording()){
			LOG.error("Already recording!");
			return;
		}
		recorder.reqRecStart();
		recN++;
	}
	
	volatile boolean isWavEnrolled = true;
	synchronized void isWavEnrolled(boolean b){
		isWavEnrolled = true;
	}
	synchronized boolean isWavEnrolled(){
		return isWavEnrolled;
	}

	

	@Override
	public void reqRecStop() {
		recStopT = System.currentTimeMillis();
		if(recorder!=null){
			recorder.reqRecStop();
		}
	}


	@Override
	public void reqPauseToggle() {
		if(recordPlayer!=null){
			recordPlayer.reqPauseToggle();
		}
	}


	@Override
	public void reqPlay(){
		if(recordPlayer!=null){
			recordPlayer.reqPlay();
		}
	}


	@Override
	public void reqPosInMs(long ms) {
		recordPlayer.reqPosInMs(ms);
	}


	@Override
	public void reqStop() {
		if(recordPlayer!=null){
			recordPlayer.reqStop();
		}
	}

	
	@Override
	public void setAudioRecorder(AudioRecorder arecorder) {
		this.recorder = arecorder;
		recorder.setAfterRecHook(new Runnable() {
			@Override
			public void run(){
				recStopT = System.currentTimeMillis();
				if(recordPlayer==null){
					LOG.error("No player set for this Record Task Controller!");
					return;
				}
//				logger.debug("Setting up wav for device '" + recordPlayer.getPlayerDevice() +"'");
				WavClip wav = recorder.getWavClip();
				recordPlayer.setAudio(wav);
				final ResponseContent resp = getRecording(); 
				enrollResponse(resp); //-- by AbstractTaskController
				if(view!=null){
					view.setPlayMaxLenInMs(wav.getDurInMs());
					view.setReadyToPlayState();
				}
			}
		});
	}

	@Override
	public void setAudioPlayer(AudioPlayer aplayer) {
		this.recordPlayer = aplayer;
		if(view !=null && view instanceof AudioPlayerView){
			recordPlayer.setAudioPlayerView((AudioPlayerView) this.view);
		}
		
		recordPlayer.setAfterPlayHook(new Runnable() {
			@Override
			public void run(){
				view.setReadyToRecState();
			}
		});
		
	}

	private class RecorderWorker implements Runnable{
		final long delay ;
		final long resolution ;
		private RecorderWorker(long delay, long res){
			this.delay = delay;
			this.resolution = res;
		}
		@Override
		public void run() {
			final long startT = System.currentTimeMillis();
			while(System.currentTimeMillis() - startT < delay){
				try {
					Thread.sleep(resolution);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			RecordTaskController.this.reqRecStart();
		}
	}
	
	public void onViewVisible(){
		if(aRecRule!=null && aRecRule.delay >= 0){ //-- delayed recording?
			new Thread(	new RecorderWorker(aRecRule.delay, 10)).start();
		}
		super.onViewVisible();
	}


	@Override
	public AudioPlayer getAudioPlayer() {
		return recordPlayer;
	}

	@Override
	public void setAudioRecRule(AudioRule arule){
		this.aRecRule = arule;
	}

	@Override
	public AudioRule getAudioRecRule() {
		return this.aRecRule;
	}


	@Override
	public void onBeforeNext() {
		reqStop();
	}
	
}
