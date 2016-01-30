package info.pinlab.ttada.view.swing;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentAudio;
import info.pinlab.ttada.core.view.RecordTaskView;
import info.pinlab.ttada.view.swing.audio.AudioRecorderBar;

public class RecordTaskPanel extends AbstractTaskPanel 
 		implements RecordTaskView, ShortcutConsumer{
	private static final long serialVersionUID = -6428138207174096890L;
	
	private AudioRecorderBar recBar;
//	private RecordTaskController recTaskController = null;
	protected static Set<Integer> shortcuts = new HashSet<Integer>();

	
	static{
		shortcuts.add(KeyEvent.VK_SPACE); //-- play audio
		shortcuts.add(KeyEvent.VK_SPACE|KeyEvent.CTRL_DOWN_MASK); //-- record audio
	}
	
	
	
	
	public RecordTaskPanel (){
		super();
		recBar = new AudioRecorderBar();
		super.setBottomPanel(recBar);
//		modelViewMap.put(, this);
	}

	@Override
	public ResponseContent getResponse(){
		//-- ask for the controller for response!
		return null;
//			return new ResponseContentEmpty(System.currentTimeMillis(), 0);
//		if(recTaskController!=null){
//		}else{
//			return new ResponseContentEmpty(System.currentTimeMillis(), 0);
////			return new ResponseContentEmpty(timeStamp, (super.displayT - timeStamp));
//		}
//		long timeStamp = System.currentTimeMillis();
//		}
//		throw new IllegalAccessError("This should not be called : audio is not stored here!");
	}

	@Override
	public void setState(Response response) {
		if(response==null){
			return;
		}
		
		ResponseContent content = response.getContent();
		if(content instanceof ResponseContentAudio){
			ResponseContentAudio audioResp = (ResponseContentAudio) content; 
			WavClip wav = audioResp.getWav();
			recBar.setPlayMaxLenInMs(wav.getDurInMs());
			recBar.setReadyToPlayState();
			recBar.setReadyToRecState();
		}
		
	}
	
	
	
	
	@Override
	public void setTaskController(TaskController controller){
		super.setTaskController(controller);

//		if(controller instanceof RecordTaskController){
//			recTaskController = (RecordTaskController) controller;
//		}
		
		//-- set listeners if controller implements them --//
		if(controller instanceof AudioRecorderListener){
			recBar.setRecActionListener((AudioRecorderListener)controller);
		}
		if(controller instanceof AudioPlayerListener){
			recBar.setPlayActionListener((AudioPlayerListener)controller);
		}
	}

	
	@Override
	public void setRecActionListener(AudioRecorderListener l) {
		recBar.setRecActionListener(l);
	}
	@Override
	public void setPlayActionListener(AudioPlayerListener l) {
		recBar.setPlayActionListener(l);
	}

	
	//-- Wrapper for calls
	@Override
	public void setBusyState(){
		recBar.setBusyState();
	}

	@Override
	public void setReadyToRecState() {
		recBar.setReadyToRecState();
	}

	@Override
	public void setRecEnabled(boolean b) {
		recBar.setRecEnabled(b);
	}

	@Override
	public void setRecMaxPosInMs(long ms) {
		recBar.setRecMaxPosInMs(ms);
	}

	@Override
	public void setRecPosInMs(long ms) {
		recBar.setRecPosInMs(ms);
	}

	@Override
	public void setRecordingState() {
		recBar.setRecordingState();
	}

	@Override
	public void setPlayEnabled(boolean b) {
		recBar.setPlayEnabled(b);
		
	}

	@Override
	public void setPlayMaxLenInMs(long ms) {
		recBar.setPlayMaxLenInMs(ms);
	}

	@Override
	public void setPlayPosInMs(long ms){
		recBar.setPlayPosInMs(ms);
	}

	@Override
	public void setPlayingState() {
		recBar.setPlayingState();
	}

	@Override
	public void setReadyToPlayState() {
		//-- shortcut: SPACE to toggle recording
		recBar.setReadyToPlayState();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode() | e.getModifiersEx();
		
		switch (keyCode) {
		case KeyEvent.VK_SPACE|KeyEvent.CTRL_DOWN_MASK:
			recBar.doToggleRecBtn();
			break;
		case KeyEvent.VK_SPACE:
			if(this.taskController.getAttemptN()>0){
				recBar.doTogglePlayBtn();
			}else{
				recBar.doToggleRecBtn();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public Set<Integer> getShortcutKeys(){
		return shortcuts;
	}
}
