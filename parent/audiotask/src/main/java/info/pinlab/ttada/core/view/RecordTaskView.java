package info.pinlab.ttada.core.view;

import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.pinsound.app.AudioRecorderView;

public interface RecordTaskView extends AudioRecorderView, AudioPlayerView {

	public void setRecActionListener(AudioRecorderListener l);
	public void setPlayActionListener(AudioPlayerListener l) ;
	
	
	
	
}
