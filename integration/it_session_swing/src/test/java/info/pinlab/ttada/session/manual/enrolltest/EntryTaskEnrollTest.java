package info.pinlab.ttada.session.manual.enrolltest;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.InstructionTextDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.EntryTask;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

/**
 * Buggy enroll for Entry tasks!
 * 
 * @author Gabor Pinter
 *
 */
public class EntryTaskEnrollTest {

	
	private static InstructionTextDisplay gapInstruction = new InstructionTextDisplay("<html><center>"
			+ "<span>音声単を聴いて、単語を入力してください</span></center>"
			+ "<ul style=color:gray>"
				+ "<li>Enter キーで回答してください"
				+ "<li>Ctrl + Spaceキーで曲を一時停止／再生することができます"
				+ "<li> 画面上部の矢印で前の問題に戻ったり(Alt + ←)、次の問題に進む(Alt + →)ことができます</ul>"
			+ "</html>");
	
	static String[] words = new String[]{
			"dictation/w_reconsider.wav_16k.wav",
			"dictation/w_relative.wav_16k.wav",
			"dictation/w_secondary.wav_16k.wav",
			"dictation/w_soapy.wav_16k.wav",
			"dictation/w_tolarence.wav_16k.wav",
			"dictation/w_tuesday.wav_16k.wav",
			"dictation/w_understand.wav_16k.wav",
	};
	
	
	
	
	static TaskSet initTasks(){
		TaskSet tset = new TaskSet();

		
		AudioRule arule = new AudioRuleBuilder()
				.setDelay(0)
				.build(); 
		StepRule srule = new StepRuleBuilder()
				.setNextByUsr(true)
				.setNextByResp(true)
				.build();
		
		for(String word : words){
			EntryTask task = new EntryTask();
			task.setBrief(word);
			task.addDisplay(gapInstruction);
			task.setStepRule(srule);
			
			try {
				WavClip wav = new WavClip(EntryTaskEnrollTest.class.getResourceAsStream(word));
				AudioDisplay disp = new AudioDisplay(wav);
				disp.setBrief(word);
				disp.setAudioRule(arule);
				task.addDisplay(disp);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
			tset.add(task);
		}
		tset.shuffle();
		return tset;
	}

	
	public static void main(String[] args) {
		TaskSet tset = new TaskSet();
		tset.setBrief("dictation");

		tset.add(new InfoTask("Start!"));
		tset.add(initTasks());
		
		CLI cli = new CLI();
		cli.getSessionFactory().setTaskSet(tset);
		cli.addRegItem(Key.USER, "Enroll DEBUG");
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		cli.run(args);
	}

}
