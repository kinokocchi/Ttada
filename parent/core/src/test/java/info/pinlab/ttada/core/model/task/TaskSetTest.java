package info.pinlab.ttada.core.model.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;

public class TaskSetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
//		BasicConfigurator.resetConfiguration();
//		BasicConfigurator.configure();
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
	public void testInsertAfterEvery_1(){
		Task insertedTask = new InfoTask("Rest");
		int insertIx = 1;
		for(; insertIx < 10 ; insertIx++ ){
			TaskSet tset = new TaskSet();
			for(int i = 0 ; i < 10 ; i++){
				tset.add(new EntryTask());
			}
			tset.insertAfterEvery(insertIx, insertedTask , true);
			
			//-- check
			int cnt = 0;
			for(Task t: tset){
				if(t.isResponsible()){
					cnt++;
				}
				if (t.equals(insertedTask)){
					assertTrue(cnt == insertIx);
					cnt = 0;
				}
				assertTrue(cnt <= insertIx);
			}
			assertFalse(tset.get(tset.size()-1) instanceof InfoTask);
		}
	}

	
	@Test
	public void testRandom(){
		Map<String, Integer> stats = new HashMap<String, Integer>();
		
		int TSET_SZ = 10;
		int SZ_MULTIPLIER = 30;
		int TRIAL_SZ = TSET_SZ * SZ_MULTIPLIER;
		
		TaskSet tset = new TaskSet();
		for(int i = 0 ; i < TSET_SZ ; i++){
			tset.add(new EntryTask().addDisplay("" + i));
		}

		for(int i = 0; i < TRIAL_SZ ; i++){
			tset.shuffle();
			Display disp = tset.get(0).getDisplays().get(0);
			String txt = ((TextDisplay) disp).getText();

			Integer val = 0;
			if(stats.containsKey(txt)){
				val = stats.get(txt);
			}
			stats.put(txt, val+1);
		}
		
		assertTrue(stats.keySet().size() == TSET_SZ);
		for(Integer cnt : stats.values()){
			assertTrue(cnt > SZ_MULTIPLIER / 4);
		}
	}
	
	
	
	@Test
	public void testAddTaskSet(){
		TaskSet tset1 = new TaskSet();
		for(int i = 0 ; i < 10 ; i++){
			tset1.add(new InfoTask("Task " + i));
		}
		TaskSet tset2 = new TaskSet();
		for(int i = 0 ; i < 5 ; i++){
			tset2.add(new InfoTask("Task " + i));
		}
		assertTrue(tset1.size()==10);
		assertTrue(tset2.size()==5);
		
		TaskSet tset3 = new TaskSet();
		tset3.add(tset1);
		assertTrue(tset3.size()==10);
		tset3.add(tset2);
		assertTrue(tset3.size()==15);
		assertTrue(tset1.add(tset2).size()==15);
		
	}
	
	
	
	@Test
	public void testRemoveAll(){
		TaskSet tset = new TaskSet();
		for(int i = 0 ; i < 10 ; i++){
			tset.add(new InfoTask("Task " + i));
		}
		assertTrue(tset.size() == 10);
		tset.removeAll();
		assertTrue(tset.size() == 0);
		
		
	}

}
