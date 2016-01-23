package info.pinlab.ttada.session.manual;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.SessionFactory;
import info.pinlab.ttada.session.app.CLI;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

public class ManualCliTest {
	static WavClip wav1, wav2;
	

	
	public static void main(String[] igonre) throws Exception{
		TaskSet tset;
//		tset = createTaskSetWithAutoStartPlaying();
//		tset = createTaskSetWith2AudioFiles();
//		tset = createTaskSetWith2AudioOnTheSameSlide();
		tset = createTaskSetWithRecording();
//		tset = createTaskSetWithText();
		
		SessionFactory init = new SessionFactory();
		init.setTaskSet(tset);
		
		String [] args = new String[]{
		Key.LOCAL_AVAILABLE.getKeyName()      + "=" + "true",
		Key.REMOTE_AVAILABLE.getKeyName()     + "=" + "false",

		Key.RESPONSE_SAVE_AS_WAV.getKeyName() + "=" + "true" , 
		
		Key.REMOTE_PROTOCOL.getKeyName() + "=" + "http",
		Key.REMOTE_HOST.getKeyName() + "=" + "localhost", 
		Key.REMOTE_PORT.getKeyName() + "=" + "8000",
//		Key.REMOTE_HOST.keyName + "=" + "192.168.11.2", 
//		Key.REMOTE_PORT.keyName + "=" + "8080",
		
		Key.REMOTE_PING_PATH.getKeyName()         + "=" + "/resttest/ping/",
		Key.REMOTE_APPPING_PATH.getKeyName()    + "=" + "/resttest/app-ping/",
		Key.REMOTE_LOGIN_PATH.getKeyName()        + "=" + "/resttest/app-login/",
//		Key.REMOTE_REST_ROOT.keyName         + "=" + "/rest",
		Key.REMOTE_RESOURCE_ROOT.getKeyName()     + "=" + "/mongotest/resource/wav_test/",
		
		Key.REMOTE_LOGIN_ID.getKeyName()          + "=" + "pinplayer-app",
		Key.TASK_SET_IS_SAVE_LOCALLY.getKeyName() + "=" + "false",

		Key.TASK_SET_BRIEF.getKeyName() + "=" + "tast set brief test",
		Key.USER.getKeyName() + "=" + "marian",
		
		Key.PLAYER_HAS_GUI.getKeyName() + "=" + "true"
				
		};
		
		CLI.main(args);
	}
	

	private static void loadWavs(){
		try {
			InputStream is = ManualCliTest.class.getResourceAsStream("sample.wav");
			wav1 = new WavClip(is);
			is = ManualCliTest.class.getResourceAsStream("sample2.wav");
			wav2 = new WavClip(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	
//	static void  createTaskSetWithText(){
//		TaskSet taskSet = new TaskSet();
//		
//		MultichoiceTask task = new MultichoiceTask();
//		task.addChoice("A").addChoice("B").addChoice("C");
//		taskSet.add(task);
//		
//		taskSet.add(new InfoTask("End"));
//		Initializer.setTaskSet(taskSet);
//	}
	
	static TaskSet  createTaskSetWithAutoStartPlaying(){
		loadWavs();
		TaskSet taskSet = new TaskSet();
		Task info = new InfoTask("SAMPLE 1");
		AudioDisplay disp = new AudioDisplay(wav1);
		disp.setAudioRule(new AudioRuleBuilder().setDelay(1000).canStop(false).canPause(false).build());
		info.addDisplay(disp);
		taskSet.add(info);
		
		taskSet.add(new InfoTask("Thank you!"));
		return taskSet;

	}
	
	static TaskSet createTaskSetWithRecording(){
		loadWavs();
		TaskSet taskSet = new TaskSet();

		RecordTask task = new RecordTask();
		task.addDisplay("Recording task!");
//		task.addDisplay(new AudioDisplay(wav1));
		taskSet.add(task);

		taskSet.add(new InfoTask("Thank you!"));
		
		return taskSet;

	}
	
	static TaskSet createTaskSetWith2AudioOnTheSameSlide(){
		loadWavs();
		
		TaskSet taskSet = new TaskSet();
		
		Task info = new InfoTask("Two sounds?");
		info.addDisplay(new AudioDisplay(wav1));
		info.addDisplay(new AudioDisplay(wav2));
		taskSet.add(info);
		
		taskSet.add(new InfoTask("Thank you!"));
		
		return taskSet;
	}
	
	
	static TaskSet createTaskSetWith2Audio(){
		loadWavs();
		
		TaskSet taskSet = new TaskSet();
		
		Task info = new InfoTask("SAMPLE 1");
		info.addDisplay(new AudioDisplay(wav1));
		
		
		taskSet.add(info);
		Task info2 = new InfoTask("SAMPLE 2");
		info2.addDisplay(new AudioDisplay(wav2));
		taskSet.add(info2);
		
		taskSet.add(new InfoTask("Have some rest!"));
		
		taskSet.add(new InfoTask("Thank you!"));
		
		return taskSet;
	}
	

}
