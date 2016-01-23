package info.pinlab.ttada.session.manual.audiotest;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.TaskSet;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

public class ManualTestWithAudio {

	TaskSet tset ; 
	WavClip wav;
	public ManualTestWithAudio() throws Exception{
		
		InputStream is  = ManualTestWithAudio.class.getClass().getResourceAsStream("sample.wav");
		if(is==null){
			is = ManualTestWithAudio.class.getResourceAsStream("sample.wav");
		}
		try {
			wav = new WavClip(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		tset = new TaskSet();
		
		RecordTask rtask  = new RecordTask();
		rtask.addDisplay("This is a record task");
		tset.add(rtask);
		
		
		MultichoiceTask mtask = new MultichoiceTask().addChoice("Sound a").addChoice("Sound b");
		mtask .addDisplay("Multichoice task with sound");
		mtask.addDisplay(new AudioDisplay(wav));
		
		
		tset.add(mtask);
		
//		Initializer.setTaskSet(tset);
		
	}
	
	
	public static void main(String[] args) throws Exception{
		new ManualTestWithAudio();
//		Initializer.init();
//		Initializer.getSession().startSession();
	}

}
