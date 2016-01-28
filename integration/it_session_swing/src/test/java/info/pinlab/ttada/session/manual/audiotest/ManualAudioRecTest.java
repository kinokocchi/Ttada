package info.pinlab.ttada.session.manual.audiotest;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.SessionFactory;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.UnsupportedAudioFileException;


public class ManualAudioRecTest {

	TaskSet tset;

	public ManualAudioRecTest() throws Exception{
		WavClip wav = null;

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
		if(wav!=null){
			mtask.addDisplay(new AudioDisplay(wav));
		}

		tset.add(mtask);
	}


	public static void main(String[] args) throws Exception{
		ManualAudioRecTest test = new ManualAudioRecTest();
		Registry conf = Registry.getDefaultInstance();
		conf.put(Key.REMOTE_AVAILABLE, false);
		conf.put(Key.LOCAL_AVAILABLE, false);
		conf.put(Key.RESPONSE_SAVE_AS_WAV, false);

		SessionFactory init = new SessionFactory();
		init.setConfig(conf);
		init.setTaskSet(test.tset);
		init.build().startSession();

		//		
		//		ResponseContent respCont = new ResponseContentEmpty(System.currentTimeMillis(), 10);
		//		SessionController sess = Initializer.getSession();
		//		sess.enrollResponse(respCont);


	}

}
