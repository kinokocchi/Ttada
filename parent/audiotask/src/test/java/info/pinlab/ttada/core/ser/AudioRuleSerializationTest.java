package info.pinlab.ttada.core.ser;

import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.AudioRuleTestResource;
import info.pinlab.ttada.gson.SerializerUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AudioRuleSerializationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		SerializerUtil.addClassTag("AudioRule", AudioRule.class);
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception{
		SerializerUtil.removeClassTag("AudioRule");
	}

	
	@Test
	public void testAudioRuleSerialization() {
		for(AudioRule arule : new AudioRuleTestResource().getResources()){
			SerializerUtil.serializeAndCompare(arule, AudioRule.class);
		}
	}
	
	
	@Test
	public void testNegativeNvalues(){
		AudioRuleBuilder builder = new AudioRuleBuilder();
		
		AudioRule rule1 = builder.setMaxPlayN(-10).build();
		AudioRule rule2 = builder.setMaxPlayN(-1).build();
		assertTrue(rule1.equals(rule2));

	
		AudioRule rule3 = builder.setMaxRecN(-10).build();
		AudioRule rule4 = builder.setMaxRecN(-1).build();
		assertTrue(rule3.equals(rule4));
		
		AudioRule rule5 = builder.setRecLen(-100).build();
		AudioRule rule6 = builder.setMaxRecN(-9999).build();
		assertTrue(rule5.equals(rule6));

	}
	
}
