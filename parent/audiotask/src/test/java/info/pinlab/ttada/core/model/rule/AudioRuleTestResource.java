package info.pinlab.ttada.core.model.rule;

import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;

import java.util.Random;

public class AudioRuleTestResource extends AbstractTestResourceProvider<AudioRule>{

	public AudioRuleTestResource(){
		super(AudioRule.class);
		
		AudioRuleBuilder builder = new AudioRuleBuilder();
		
		//-- default, empty --//
		super.addResource(builder.build());

		
		Random rand = new Random(System.currentTimeMillis());
		for (Boolean b1 : new boolean[]{true, false} ){
			for (Boolean b2: new boolean[]{true, false} ){
				for (Boolean b3: new boolean[]{true, false} ){
					for (Boolean b4: new boolean[]{true, false} ){
						builder.canPause(b1);
						builder.canPlay(b2);
						builder.canRec(b3);
						builder.canStop(b4);
						
						builder.setDelay(rand.nextInt(100)*10);
						builder.setMaxPlayN(rand.nextInt(4)-1);
						builder.setMaxRecN(rand.nextInt(4)-1);
						builder.setRecLen(1000+rand.nextInt(10)*100);
						
						super.addResource(builder.build());
					}
				}
			}
		}
	}
	

	
	
}
