package info.pinlab.ttada.session.manual.audiotest;

import java.io.InputStream;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.app.CLI;

public class ManualMultichoiceWithAudio {

	
	public static TaskSet getTaskSet () throws Exception{
		TaskSet tset = new TaskSet();

		WavClip wav1 =null;
		String audioFile = "delilah.wav";
		InputStream is  = ManualTestWithAudio.class.getClass().getResourceAsStream(audioFile);
		if(is==null){
			is = ManualTestWithAudio.class.getResourceAsStream(audioFile);
		}
		wav1 = new WavClip(is);
		
		AudioRule arule = new AudioRuleBuilder().setDelay(0).build();
		StepRule srule  = new StepRuleBuilder().setNextByResp(true).build();
		
		for(int i = 0; i < 5 ; i++){
			MultichoiceTask mtask = new MultichoiceTask();
			mtask.setStepRule(srule);
			mtask.addChoice("A").addChoice("B").addChoice("C");
			mtask.setRowN(3);
			
			AudioDisplay disp = new AudioDisplay(wav1);
			disp.setAudioRule( arule);
			mtask.addDisplay(disp);
			tset.add(mtask);
		}		
//		RecordTask rtask  = new RecordTask();
//		rtask.addDisplay("This is a record task");
//		rtask.addDisplay(new AudioDisplay(wav1));
//		tset.add(rtask);
		
		tset.add(new InfoTask("Test ends here"));
		return tset;
	}
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		CLI cli = new CLI();
		cli.getSessionFactory().setTaskSet(getTaskSet());
		cli.run(args);
	}
}
