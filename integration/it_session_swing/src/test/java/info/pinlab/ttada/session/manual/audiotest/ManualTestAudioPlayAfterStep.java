package info.pinlab.ttada.session.manual.audiotest;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualTestAudioPlayAfterStep {

	
	private static TaskSet getTasks() throws Exception{
		TaskSet tset = new TaskSet();
		tset.add(new InfoTask("<html><h1>Testing auto steps</h1></html>"));
		StepRule srule = new StepRuleBuilder().setNextByResp(true).build();
		AudioRule arule = new AudioRuleBuilder().setDelay(0).build();
		
		WavClip wav = new WavClip(ManualTestAudioPlayAfterStep.class.getResourceAsStream("sample.wav"));
		for(int i = 0; i < 4 ; i++){
			MultichoiceTask mtask = new MultichoiceTask();
			mtask.setStepRule(srule);
			mtask.addDisplay("Task #" + (i+1));

			AudioDisplay adisp = new AudioDisplay(wav);
			adisp.setAudioRule(arule);
			mtask.addDisplay(adisp);
			
			mtask.addChoice("One choice " + i);
			tset.add(mtask);
		}
		return tset;
	}
	
	public static void main(String[] args) throws Exception{
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, false);
		
		
		TaskSet tset = new TaskSet();
		tset.add(getTasks());

		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
	}
	
}
