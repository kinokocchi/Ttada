package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerView;

public interface AudioPlayController {
	public void setAudioPlayer(AudioPlayer player);
	public AudioPlayer getAudioPlayer();
	public void setAudioPlayerView(AudioPlayerView playerView);
	public void setAudioRule(AudioRule arule);
}
