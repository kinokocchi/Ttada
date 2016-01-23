package info.pinlab.ttada.session.manual.audiotest;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.app.CLI;


/**
 * 
 * Testing if playing & recording together works.
 * 
 * @author Gabor Pinter
 *
 */
public class ManualTestPlayAndRecTogether {

	
	public static TaskSet getTaskSet () throws Exception{
		WavClip wav1 =null, wav2 = null;

		InputStream is  = ManualTestWithAudio.class.getClass().getResourceAsStream("sample.wav");
		if(is==null){
			is = ManualTestWithAudio.class.getResourceAsStream("sample.wav");
		}
		try {
			wav1 = new WavClip(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		InputStream is2  = ManualTestWithAudio.class.getClass().getResourceAsStream("sample2.wav");
		if(is2==null){
			is2 = ManualTestWithAudio.class.getResourceAsStream("sample2.wav");
		}
		try {
			wav2 = new WavClip(is2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		

		RecordTask rtask  = new RecordTask();
		rtask.addDisplay("This is a record task");
		rtask.addDisplay(new AudioDisplay(wav1));
//		rtask.addDisplay(new AudioDisplay(wav2));
		
		TaskSet tset = new TaskSet();
		tset.add(rtask);
		tset.add(new InfoTask("Test ends here"));
		return tset;
	}
	
	
	public static void main(String[] args) throws Exception{
		CLI cli = new CLI();
		cli.getSessionFactory().setTaskSet(getTaskSet());
		cli.run(args);
	}

}
