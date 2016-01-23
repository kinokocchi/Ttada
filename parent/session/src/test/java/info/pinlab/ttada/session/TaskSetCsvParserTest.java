package info.pinlab.ttada.session;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.TaskSetCsvParser.CsvErrorMonitorListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TaskSetCsvParserTest {
	public static Logger logger = Logger.getLogger(TaskSetCsvParser.class);
	static WavClip wav = null;
	static File tmpDir = null;
	static File wavDir = null; 

	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		tmpDir = File.createTempFile("pinlab-testing_", ""); tmpDir.delete();
		logger.info("Creating tmp dir '"+ tmpDir.getAbsolutePath() + "'");
		tmpDir.mkdirs();
		wavDir = new File(tmpDir, "wavs");
		wavDir.mkdirs();
		
		InputStream is = TaskSetCsvParserTest.class.getClassLoader().getResourceAsStream("sample.wav");
		if(is==null){
			is = TaskSetCsvParserTest.class.getResourceAsStream("sample.wav");
		}
		if(is!=null){
			wav = new WavClip(is);
			//-- write into wav dir 
			FileOutputStream fos = new FileOutputStream(new File(wavDir, "sample.wav"));
			fos.write(wav.toWavFile());
			fos.flush();
			fos.close();
			
		}else{
			fail("Couldn't load sample.wav");
			
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(tmpDir!=null){
			logger.info("Removing tmp dir '"+ tmpDir.getAbsolutePath() + "'");
			tmpDir.delete();
		}
	}

	
	
	@Test
	public void parseInfoTest(){
//		String taskAsString = "multi,  a::1, b::2";
		String msgWithQuote = "  \"  Hello parser!   \"   ";  // with extra quotes & space
		String msgNoQuote = "Hello parser!";                  // with extra quotes
		String taskAsString = "   info  ,  " + msgWithQuote;
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		
		assertTrue(tset.size() == 1); 
		Task task = tset.get(0);
		assertTrue(task != null); 
		assertTrue(task instanceof InfoTask);
		
		assertTrue(task.getDisplays().size() == 1);
		Display disp = task.getDisplays().get(0);
		assertTrue(disp instanceof TextDisplay);
		String txt = ((TextDisplay)disp).getText();
		assertTrue(txt.equals(msgNoQuote));
		
	}

	
	@Test
	public void parseMultiTaskTest(){
		String lab = "single";
		String val = "choice";
		String taskAsString = "multi,   \"  " + lab + "  \"   ::   \" " + val + " \"  ";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		
		assertTrue(tset.size() == 1); 
		Task task = tset.get(0);
		assertTrue(task != null); 
		assertTrue(task instanceof MultichoiceTask);
		MultichoiceTask multi = (MultichoiceTask)task;
		assertTrue(multi.getChoiceN() == 1);
		
		TextDisplay disp =  (TextDisplay) multi.getChoiceX(0);
		assertTrue(lab.equals(disp.getText()));
		assertTrue(val.equals(disp.getBrief()));
	}
	
	
	@Test
	public void parseMultiTaskTest2(){
		String taskAsString = "multi,   A::1, B::2, C::3,,,,";  // empty should be ignored!

		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		
		assertTrue(tset.size() == 1); 
		Task task = tset.get(0);
		assertTrue(task != null); 
		assertTrue(task instanceof MultichoiceTask);
		MultichoiceTask multi = (MultichoiceTask)task;
		assertTrue(multi.getChoiceN() == 3);
		
		if (    multi.getChoiceX(0).getBrief() == "1"
             &&	multi.getChoiceX(1).getBrief() == "2"
             &&	multi.getChoiceX(1).getBrief() == "3" ){
			//-- choices are not shuffled!!!
		}
	}

	
	
	
	
	@Test
	public void parseMultiTaskChoiceShuffle(){
		String taskAsString =
				TaskSetCsvParser.CONF_MARK +", shufflechoice  \n"
				+ "multi,   A::1, B::2, C::3, D::4, E::5";  // empty should be ignored!

		int TRIAL_N = 5;
		for(int i = 0;i<TRIAL_N;i++){  // out of N trials, one should shuffle the choices
			TaskSetCsvParser parser = new TaskSetCsvParser();
			parser.setCsvFileAsString(taskAsString);
			TaskSet tset = parser.parse();

			assertTrue(tset.size() == 1); 
			Task task = tset.get(0);
			MultichoiceTask multi = (MultichoiceTask)task;
			assertTrue(multi.getChoiceN() == 5);

			boolean sameOrder = checkChoiceOrder(multi, new String[]{"1", "2", "3", "4", "5"});
			if(!sameOrder){
				return; //-- PASSED! - it was shuffled : different order achieved
			}
		}			
		//-- at this point: all orders were identical!
		fail("Choices were not shuffled - out of " + TRIAL_N + " trials");
		
	}
	
	private boolean checkChoiceOrder(MultichoiceTask multi, String [] briefs){
		for(int i =0 ;i < briefs.length; i++){
			if( briefs[i] != multi.getChoiceX(i).getBrief()){
				return false;
			};
		}
		return true;
	}
	
	
	@Test
	public void testResourceReadFromFile() throws FileNotFoundException{
//		String dir = wavDir.getName();
		String taskAsString =
				TaskSetCsvParser.CONF_MARK +", shufflechoice  \n"
				+ "multi,  some text to show,  A::1, "+ wavDir.getName() + "/sample.wav, B::2, C::3, D::4, E::5";  // empty should be ignored!

		
		//-- create dir for for wav
		
		File confFile = new File(tmpDir, "conf.csv");
		
		PrintWriter writer = new PrintWriter(confFile.getAbsolutePath());
		writer.print(taskAsString);
		writer.close();
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFile(confFile.getAbsolutePath());
		parser.setResourceDir(tmpDir);
//		parser.setErrorMonitor(new CsvErrorMonitorListener() {
//			@Override
//			public void illegalLineFound(int lineN, IllegalArgumentException exp) {
//				System.out.println(lineN +"\t" + exp.getMessage());
//			}
//		});
		TaskSet tset = parser.parse();

		assertTrue(tset.size() == 1);
		
		MultichoiceTask multi = (MultichoiceTask)tset.get(0);
		AudioDisplay adisp = null;
		for(Display disp : multi.getDisplays()){
			if(disp instanceof AudioDisplay){
				adisp = (AudioDisplay) disp;
			}
		}
		assertTrue(adisp != null);
	}
	
	
	@Test
	public void testResourceReadFromJar() throws FileNotFoundException{
		//-- checking if it can read wavs from different jar  
		
		String taskAsString =  "multi,  sample.wav,  A::1, B::2, C::3, D::4, E::5";  // empty should be ignored!

		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		
		parser.setClassForResourceLoading(this.getClass());
		TaskSet tset = parser.parse();
		
		assertTrue(tset.size() == 1);
		
		MultichoiceTask multi = (MultichoiceTask)tset.get(0);
		AudioDisplay adisp = null;
		for(Display disp : multi.getDisplays()){
			if(disp instanceof AudioDisplay){
				adisp = (AudioDisplay) disp;
			}
		}
		assertTrue(adisp != null);
	}
	
	
	
	@Test
	public void testDisplayOrder(){
		String taskAsString =  "multi,  a text display ,  sample.wav, !brief ,";  // empty should be ignored!

		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		parser.setClassForResourceLoading(this.getClass());
		TaskSet tset = parser.parse();
		
		assertTrue(tset.size() == 1);
		MultichoiceTask multi = (MultichoiceTask)tset.get(0);
		List<Display> displays = multi.getDisplays();
		assertTrue(displays.size() == 2);

		//-- check order
		assertTrue(displays.get(0) instanceof TextDisplay);
		assertTrue(displays.get(1) instanceof AudioDisplay);
	}
	
	
	@Test
	public void testConfig(){
		String taskAsString ="#CONF, nextbyresp   \n" +
				"info, haha\n" +   
		"multi, haha, a::b, b::b\n";  
		
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();

		assertTrue(tset.size() == 2);
		for(Task task : tset){
			assertTrue(task.getStepRule().isNextByResp());
		}
	}
	
	@Test
	public void testInfoLine(){
		String taskAsString = "#CONF, nextbyresp   \n" +
				"info, a , <html> <h1 style=\"color: #334380;\"> Welcome </h1> <h2> Experiment - practice</h2> </html>,";
				
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();

		assertTrue(tset.size() == 1);
	}
	
	@Test
	public void testBrief(){
		String taskAsString = "multi, checking brief, !S1, A::kase, B::kasha  ";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		assertTrue(tset.size() == 1);

		String brief = tset.get(0).getBrief().trim();
		assertTrue(!brief.isEmpty());
		assertTrue(brief.equals("S1"));
		
	}

	@Test
	public void testTaskSetName(){

		String taskAsString = "#CONF, name, taskset-nametest   ,\n"+
							"multi, checking brief, A::kase, B::kasha  ";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		assertTrue(tset.size() == 1);
//		System.out.println(tset.getBrief());
		assertTrue(tset.getBrief().equals("taskset-nametest"));
	}
	
	
	@Test
	public void testParseQuotedInfo(){
		
		String taskAsString = 
				"\"info\", \"quoted test\", not quoted\n"+
				"\"info\", \"with , comma within quote\"\n"+
				"  \"   info  \"   , info has extra spaces within quote\n"+
				"info,  <html><h1> html text </h1></html> \n"+ 
				"info,  \"<html><h1> html text </h1></html>\"\n" 
				;
		

		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
//		System.out.println(tset.size());
		assertTrue(tset.size() == 5);

		
	}


	@Test
	public void testEmptyLines(){
		String taskAsString = "#CONF, title, taskset title\n"
				+ "\n"  //-- empty line!
				+ "info, above line is empty!";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		parser.setErrorMonitor(new CsvErrorMonitorListener() {
			@Override
			public void illegalLineFound(int lineN, IllegalArgumentException exp) {
				fail("CSV has a parsing error! On line #" + lineN + "' " + exp);
			}
		});
		TaskSet tset = parser.parse();
		assertTrue(tset.size() == 1);
		
		
	}
	
	@Test
	public void testMultipliedTask(){
		String taskAsString = "info*3,  I'm cloned!!!\n";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		assertTrue(tset.size() == 3);
	}


	@Test
	public void testBockSize(){ //-- inserts 'info' task after every N
		String taskAsString = "multi*10,  dummy test, a::a, b::b \n"
				+ "#CONF, blocksize, 3, Resting slide!";
		
		TaskSetCsvParser parser = new TaskSetCsvParser();
		parser.setCsvFileAsString(taskAsString);
		TaskSet tset = parser.parse();
		assertTrue(tset.size() == 13);  //-- 10 multi + 3 info = 13
		
		assertTrue(tset.get(3 /*  4th */ ) instanceof InfoTask);   //-- multi, multi,multi,info <- 4th
		assertTrue(tset.get(7 /*  8th */ ) instanceof InfoTask);    
		assertTrue(tset.get(11 /* 4th */ ) instanceof InfoTask);   
		
		//-- EVEN AFTER SHUFFLE!
		taskAsString = "multi*10,  dummy test, a::a, b::b \n"
				+ "#CONF, blocksize, 3, Resting slide!\n"
				+ "#CONF, shuffle   ";
		
		parser.setCsvFileAsString(taskAsString);
		tset = parser.parse();
		assertTrue(tset.size() == 13);  //-- 10 multi + 3 info = 13
		
		assertTrue(tset.get(3 /*  4th */ ) instanceof InfoTask);   //-- multi, multi,multi,info <- 4th
		assertTrue(tset.get(7 /*  8th */ ) instanceof InfoTask);    
		assertTrue(tset.get(11 /* 4th */ ) instanceof InfoTask);
	}

	
	
	
	@Test
	public void testExtertnalResource(){
		//TODO:
		//-- files shouldbe copied to /tmp folder or sg
		
		
		
	}
	
}

















