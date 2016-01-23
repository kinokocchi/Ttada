package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioRecorder;

/**
 * Anything that controls audio 
 * 
 * 
 * @author Gabor Pinter
 *
 */
public interface AudioPlayRecController{
	
	public void setAudioRecorder(AudioRecorder recorder);
	public void setAudioPlayer(AudioPlayer player);
	public AudioPlayer getAudioPlayer();
	
	public void setAudioRecRule(AudioRule arule);
	public AudioRule getAudioRecRule();

}
