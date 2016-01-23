package info.pinlab.ttada.session.not;

import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.cache.MemCache;
import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl.SimpleCacheManagerBuilder;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.ConfigFileParser;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigFileStreamTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		
		SimpleCacheManagerBuilder scmb = new SimpleCacheManagerBuilder();
		//-- Order is important --//
		//-- Memory Cache is always available! --//
		scmb.addCache(MemCache.getInstance(), true);
		CachedValue.setCacheManager(scmb.build());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testConfFileWithMultichoice(){
		String multiTask = "@multi\n"
				+"$text multitask label\n"
				+"$choice  chioce a | choice b | choice c\n"
				+"\n"
				;

		TaskSet parsedTset = ConfigFileParser.parseText(multiTask);
		assertTrue(parsedTset != null);
		assertTrue(parsedTset.size() == 1);

		
	}
	
	@Test
	public void testTexts(){
		String [] texts = {
				"single", 
				"two words",
				"  ignore trimmable spaces  ",
				"  more than    one space  ",
				"  and a	tab  ",
				"日本語"				
		};
		
		StringBuffer sb = new StringBuffer();
		for (String text : texts){
			sb.append("@rec \n")
			.append("$text ").append(text)
			.append("\n\n")
			;
		}
//
		TaskSet parsedTset = ConfigFileParser.parseText(sb.toString());
		assertTrue(parsedTset != null);
		assertTrue(parsedTset.size() == texts.length);

		int ix = 0 ; 
		for(Task task: parsedTset){
			String text = texts[ix++].trim(); 
			TextDisplay disp = (TextDisplay) task.getDisplays().get(0);
			String disptext = disp.getText().trim();
//			System.out.println(disptext + " = " + text);
			assertTrue(text.equals(disptext));
		}
	
	}
	
	
	@Test
	public void testConfFileWithTwoRecTasks(){
		//-- place this resource into readable path
		TaskSet tset = new TaskSet();
		RecordTask recTask1 = new RecordTask();
		recTask1.addDisplay("egy");
		AudioRule arule1 = new AudioRule.AudioRuleBuilder()
			.setRecLen(4000)
			.setMaxRecN(3)
			.setMaxPlayN(2)
			.build();
		recTask1.setRecRule(arule1);
		tset.add(recTask1);

		RecordTask recTask2 = new RecordTask();
		recTask2.addDisplay("ketto");
		AudioRule arule2 = new AudioRule.AudioRuleBuilder()
			.setRecLen(11000)
			.setMaxRecN(13)
			.setMaxPlayN(10)
			.build();
		recTask2.setRecRule(arule2);
		tset.add(recTask2);
		
		
		//-- same tasks as config file:
		String twoRecTask = "@rec \n" + 
				"$text egy\n" +
				"$maxreclen 4 # ignore comment!\n" + 
				"$maxrecn 3 \n" +
				"$maxplayn 2  \n" +
				"\n\n"  +
				"@rec \n" +
				"$text ketto\n" + 
				"$maxreclen 11\n"  +
				"$maxrecn 13\n"  +
				"$maxplayn 10\n" +
				"\n"
				;
		
		TaskSet parsedTset = ConfigFileParser.parseText(twoRecTask);
		assertTrue(parsedTset != null);
		assertTrue(parsedTset.size() == tset.size());
		
		for(int i = 0; i < tset.size() ; i++){
			RecordTask task = (RecordTask) tset.get(i);
			RecordTask parsedTask = (RecordTask) parsedTset.get(i);
			assertTrue(task.getStepRule().equals(parsedTask.getStepRule()));
			AudioRule arule = task.getRecRule();
			AudioRule parsedARule = parsedTask.getRecRule();
			assertTrue(arule.equals(parsedARule));
			
			assertTrue(task.equals(parsedTask));
		}
		assertTrue(tset.equals(parsedTset));
	}
}
