package info.pinlab.ttada.view.swing.manual;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.app.CLI;
import info.pinlab.ttada.view.swing.audio.AudioPlayerButtonTest;

public class ManualRecWhilePlayTest {

	public static void main(String[] args) throws Exception{
		String path = "sample.wav";
		InputStream is = AudioPlayerButtonTest.class.getResourceAsStream(path);
		assertTrue( "Can't read wav file!", is!=null);
		WavClip wav = new WavClip(is);
		AudioDisplay disp = new AudioDisplay(wav);
		disp.setAudioRule(new AudioRuleBuilder().setDelay(0).build());
		
		RecordTask task = new RecordTask();
		task.addDisplay(disp);
		task.setRecRule(new AudioRuleBuilder().setDelay(0).build());
		
		TaskSet tset = new TaskSet();
		tset.add(task);
		
		CLI cli = new CLI();
		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
		
		
	}

}
