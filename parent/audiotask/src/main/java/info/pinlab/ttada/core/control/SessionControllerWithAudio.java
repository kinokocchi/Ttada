package info.pinlab.ttada.core.control;

import info.pinlab.pinsound.PlayerDeviceFacotry;
import info.pinlab.pinsound.RecorderDeviceController;

public interface SessionControllerWithAudio extends SessionController{
	public void setAudioPlayerDeviceFactory(PlayerDeviceFacotry device);
	public void setAudioRecorderDeviceController(RecorderDeviceController device);
	
	public PlayerDeviceFacotry getAudioPlayerDeviceFactory();
	public RecorderDeviceController getAudioRecorderDeviceController();
	
//	public void setAudioPlayer(AudioPlayer player);
//	public void setAudioRecorder(AudioRecorder recorder);
}
