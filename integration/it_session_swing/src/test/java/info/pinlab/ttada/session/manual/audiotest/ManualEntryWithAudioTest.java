package info.pinlab.ttada.session.manual.audiotest;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.EntryTask;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.app.CLI;

public class ManualEntryWithAudioTest {

	static String[][] gaps = new String [][]{
			{"delilah-like.wav",  "Hey there Delilah, <br> What's it ___ in New York City?"},
			{"delilah-distance.wav",  "Hey there Delilah, <br> Don't worry about the ___ !"}
	};

	
	
	static public TaskSet getGapTasks(){
		TaskSet tset = new TaskSet();
		
		StepRule srule  = new StepRuleBuilder().setNextByResp(true).build();
		AudioRule arule = new AudioRuleBuilder().setDelay(0).build();

		for(String[] gap : gaps){
			EntryTask task = new EntryTask();
			task.setStepRule(srule);
			
			AudioDisplay disp = new AudioDisplay(getWav(gap[0]));
			disp.setAudioRule(arule);
			task.addDisplay(disp);
			task.addDisplay(new TextDisplay("<html><div style=\"font-size:110%;\">" + gap[1] + "</div></html>"));
			task.addDisplay(new TextDisplay(" "));
			tset.add(task);
		}
		return tset;
	}
	
	
	
	public static WavClip getWav(String wavName) {
		InputStream is = ManualEntryWithAudioTest.class.getResourceAsStream(wavName);
		if(is==null){
			is = ManualEntryWithAudioTest.class.getClass().getResourceAsStream(wavName);
		}
		if(is==null){
			is = ManualEntryWithAudioTest.class.getClassLoader().getResourceAsStream(wavName);
		}
		
		if(is==null){
			is = ManualTestWithAudio.class.getResourceAsStream(wavName);
		}
		if(is==null){
			is  = ManualTestWithAudio.class.getClass().getResourceAsStream(wavName);
		}
		WavClip wav = null;
		try {
			wav = new WavClip(is);
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return wav;
	}
	
	
	
	public static void main(String[] args) throws Exception{

		TaskSet tset = getGapTasks();
		tset.add(new InfoTask("End!"));
		
		CLI cli = new CLI();
		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
	}

}
