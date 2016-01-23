package info.pinlab.ttada.session;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.utils.FileStringTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * <p>
 * Each line in the CSV file is a task. This format is useful for multitask tasks. 
 *</p>
 *
 * <u>Example</u>
 *  <pre>
# this is just a plain comment,,,,,,
#
# the following lines are for configuration
#CONF,  shuffle,    true
#CONF,  nextbyresp
info,   Hello user!
multi,L / R, wav/right.wav,right,right,<html><span style="color:blue;font-weight:bold;">light</span></html>,light
info,Thanks!
 *  </pre>
 * 
 * 
 * Usage
 * 
 * <pre>
TaskSetCsvParser parser = new TaskSetCsvParser();
parser.setCsvFile("stimuli.csv");
parser.setResourceDir(tmpDir);
TaskSet tset = preser.parse();

 * </pre>
 * 
 * 
 * @author Gabor Pinter
 *
 */
public class TaskSetCsvParser {
	public static Logger logger = Logger.getLogger(TaskSetCsvParser.class);
	private static Map<String, Class<? extends Task>> labelToClazzMap = new HashMap<String, Class<? extends Task>>(); 

	public static String COMMENT = "#";
	public static String CONF_MARK = "#CONF";
	public static String MULTIPLIER_MARK = "*";
	
	static String [] AUDIO_EXT = new String[]{".wav"};
	static String CHOICE_MARK = "::";

	private Class<?> classForResourceloading = null;

	private int tsetBlockSize = -1;
	private String restInfoTxt = "<html><h1 style=\"color:green;\">Have some rest</h1></html>";

	
	class TaskBits{
		final String originalLine;
		final int lineN;
		final boolean isConfLine ;
		final boolean isEmpty ;
		
		final Task task;
		final String [] lineChunks;
		
		final Map<Integer, Display> displays = new TreeMap<Integer, Display>(); 
		final Map<Integer, Boolean> hasProcessed = new HashMap<Integer, Boolean>();
		int multiplier = 1;
		
		
		
		public TaskBits(String line, int lineN){
			this.originalLine = line;
			this.lineN = lineN;

			line = line.trim();
			if (line.startsWith(CONF_MARK)){
				this.isConfLine = true;
				this.isEmpty = false;
			}else if (line.startsWith("#")){ //-- comment line
				this.isEmpty = true;
				this.isConfLine = false;
			}else{
				this.isConfLine = false;
				this.isEmpty = false;
			}
			
			lineChunks = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); //-- split comma only if there is zero or 2k quote ahead
																		//-- (?=     : lookahead
																		//-- (       :  group
																		//-- [^\"]*  : zero or n non-comma
																		//-- \"      : comma
																		//-- [^\"]*  : zero or n non-comma
																		//-- )*      :  this group (with 2 quotes) 0 or n times
			
			for(int i = 0 ; i < lineChunks.length ; i++){
				hasProcessed.put(i, false);
			}
			this.hasProcessed.put(0,true); //-- for the class
			
			//-- create task! --//
			if(!isConfLine && !isEmpty){
				String taskType = removeQuotes(lineChunks[0].toLowerCase());

				int multiplierAt = taskType.indexOf(MULTIPLIER_MARK); //-- handle  settings such as multi*3 : repeat 3 times
				if(multiplierAt>0){
					String taskTypeCandidate   = taskType.substring(0, multiplierAt);
					String multiplierCandidate = taskType.substring((multiplierAt+1));
					try{
						this.multiplier =  Integer.parseInt(multiplierCandidate);
						taskType = taskTypeCandidate;
					}catch (NumberFormatException e){
						//-- not a number!!
						this.multiplier = 1;
					}
				}
				
				Class<? extends Task> clazz = labelToClazzMap.get(taskType);
				if(clazz==null){
					throw new IllegalArgumentException("No such task as '"+ taskType +"'");
				}else{
					try {
						this.task =  clazz.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
						throw new IllegalArgumentException("Can't instantiate  '"+ taskType +"'");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						throw new IllegalArgumentException("Can't access '"+ taskType +"'");
					}
				}
			}else{
				this.task = null;
			}
		}
		public Task getTask(){
			return this.task;
		}
		
		public void addDisp (int ix, Display disp){
			displays.put(ix, disp);
			hasProcessed.put(ix, true);
		}
		

		public List<Task> build(){
			List<Task> taskList = new ArrayList<Task>();
			if (isConfLine || isEmpty) return taskList;
			
			//-- order is important!
			parseChoices(this);
			parseAudioDisplays(this);
			parseTaskBrief(this);
			parseTextDisplays(this);			
			
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			
			String brief = task.getBrief();
			if(brief!=null){
				sb.append(brief);
				isFirst = false;
			}
			
			for(Display disp : displays.values()){
				task.addDisplay(disp);
				if(disp instanceof AudioDisplay){
					if(!isFirst) sb.append("|");
					sb.append(disp.getBrief());
					isFirst = false;
				}
			}
			task.setBrief(sb.toString());
			
			if (this.multiplier<1){ //-- avoid negative or funny values
				this.multiplier = 1;
			}
			for(int i=0 ; i < this.multiplier ; i++){
				taskList.add(task);
			}
			return taskList;
		}
	}
	
	
	static{
		labelToClazzMap.put("info",  InfoTask.class);
		labelToClazzMap.put("rec",   RecordTask.class);
		labelToClazzMap.put("multi", MultichoiceTask.class);
	}
	
	static private String removeQuotes(String chunk){
		String chunk_ = chunk.trim();
		if(chunk_.startsWith("\"") && chunk_.endsWith("\"")){
			chunk_ = chunk_.substring(1, chunk_.length()-1).trim();
		}
		return chunk_;
	}
	
	public interface CsvErrorMonitorListener{
		public void illegalLineFound(int lineN, IllegalArgumentException exp);
	}
	
	private CsvErrorMonitorListener errMonitor = null;
	
	public void setErrorMonitor(CsvErrorMonitorListener errorMonitor){
		errMonitor = errorMonitor;
	}
	
//	private Configurator conf = null;
//	private Configurator runtimeConf = null;  //-- can change during parsing the TaskSet file
	private final TaskSet tset ;
	
	private String resourceDir = null;
	private File resourceDirAbsPath = null;
	private File csvFileDir = null;
	
	private String csvFilePath = null;
	private String csvFileAsString = null;
	private InputStream csvFileIS = null;
	
	private StepRuleBuilder stepRule = new StepRuleBuilder();
	private AudioRuleBuilder audioRule = new AudioRuleBuilder();

	private boolean isShuffleTasks = false;
	private boolean isShuffleChoices = false;

	private int currentLine = -1;	
	
	private boolean errInSeparateThread = false;
	

	
	public TaskSetCsvParser(){
		this(null, false);
	}
	
	public TaskSetCsvParser(CsvErrorMonitorListener errMonitor, boolean errInSeparateThread){
		this.tset = new TaskSet();
		this.errMonitor = errMonitor;
		this.errInSeparateThread = errInSeparateThread;
	}
	
	private void init() throws IllegalStateException{
//		if(conf==null){
//			conf = Configurator.getDefaultInstance();
//		}
//		this.resourceDir = conf.get(Key.TASK_SET_MEDIA_DIR);
//		
		tset.removeAll();

		//-- check if input available
		if(csvFilePath == null && csvFileIS == null && csvFileAsString == null){
			String msg = "No input data for CSV TaskSet parsing!";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		if(csvFilePath!=null){
			csvFileDir = new File(csvFilePath).getAbsoluteFile().getParentFile();
		}
		
		//-- Resouce dir
		if(resourceDir!=null){
			if(resourceDir.startsWith("/") || /* for WIN */ (resourceDir.length()>0 && resourceDir.charAt(1)==':')){
				//-- absolute path
				resourceDirAbsPath = new File(resourceDir);
			}else{
				if(csvFileDir !=null){
					//-- relative to csv file 
//					resourceDirAbsPath = new File(csvFilePath).getAbsoluteFile().getParentFile();
					resourceDirAbsPath  = new File(csvFileDir, resourceDir);
				}else{
					resourceDirAbsPath = new File(resourceDir);
				}
			}
			if(resourceDirAbsPath!=null && !resourceDirAbsPath.isDirectory()){
				logger.error("Resource path is not a directory '" + resourceDirAbsPath.getAbsolutePath() +"'");
				resourceDirAbsPath = null;
			}
		}
	}
	
	
	public void setResourceDir(File dir){
		setResourceDir(dir.getAbsolutePath());
	}
	
	public void setResourceDir(String dir){
		resourceDir = dir;
	}
	
	public void setCsvFileDir(String dir){
		csvFileDir = new File(dir);
		if(!csvFileDir.isDirectory()){
			logger.error("Csv file dir is NOT set to a directory '" + dir + "'");
		}
	}
	
	public void setClassForResourceLoading(Class<?> clazz){
		classForResourceloading = clazz;
	}

	public TaskSetCsvParser setCsvFile(String csv){
		csvFilePath = csv;
		csvFileIS = null;
		csvFileAsString = null;
		return this;
	}
	
	public TaskSetCsvParser setCsvFileAsInputStream(InputStream csvIs){
		csvFileAsString = null;
		csvFilePath = null;
		csvFileIS = csvIs;
		return this;
	}
	
	public TaskSetCsvParser setCsvFileAsString(String csv){
		csvFileAsString = csv;
		csvFilePath = null;
		csvFileIS = null;
		return this;
	}
	
	
	public TaskSet parse(){
		init();
		
		if(csvFileAsString != null){
			//-- fine, data already loaded, will parse at the end
		}else{
			if(csvFileIS != null){
				try {
					csvFileAsString = FileStringTools.getStreamAsString(csvFileIS);
				} catch (FileNotFoundException e) {
					csvFileAsString = null;
					logger.error(e);
					e.printStackTrace();
				} catch (IOException e) {
					csvFileAsString = null;
					logger.error(e);
					e.printStackTrace();
				}
			}else{
				if(csvFilePath != null){
					if(classForResourceloading!=null){
						csvFileIS  = classForResourceloading.getResourceAsStream(csvFilePath);
						if(csvFileIS!=null){
							return parse();
						}
					}else{
						try {
							csvFileAsString = FileStringTools.getFileAsString(csvFilePath);
						} catch (FileNotFoundException e) {
							csvFileAsString = null;
							logger.error(e);
							e.printStackTrace();
						} catch (IOException e) {
							csvFileAsString = null;
							logger.error(e);
							e.printStackTrace();
						}
					}
				}
			}
		} //-- if csvFileAsString != null
		
		String[] lines = csvFileAsString.split("\\r?\\n");
		for(String line : lines){
			currentLine++;
			try{
				if(line.trim().isEmpty()) continue;   //-- skip empty line!
				
				TaskBits taskBits = new TaskBits(line, currentLine);
				
				if(taskBits.isConfLine){
					parseConfLine(taskBits.lineChunks);
				}else{
					Task task = taskBits.getTask();
					if(task!=null) task.setStepRule(stepRule.build());
				}
				List<Task> tasks = taskBits.build(); //-- build tasks
				tset.add(tasks);
			}catch(final IllegalArgumentException exp){
				logger.error("Line #"+ (currentLine+1) + " " + exp.getMessage());

				//-- start listener callback in different thread!
				if(errMonitor != null){
					if(errInSeparateThread){
						new Thread(new Runnable() {
							@Override 
							public void run() {
								errMonitor.illegalLineFound(currentLine, exp); 			}
						}).start();
					}else{ //-- not in separate thread
						errMonitor.illegalLineFound(currentLine, exp);
					}
				}//-- errMon != null
			}//-- catch IllegalArgumentException
		}//-- foreach line
		
		if(this.tsetBlockSize > 0){
			tset.insertAfterEvery(tsetBlockSize, new InfoTask(this.restInfoTxt), true);
		}
		
		if(isShuffleTasks){
			tset.shuffle();
		}
		return tset;
	}
	
	
	private void parseConfLine(String[] chunks) throws IllegalArgumentException{
		String key = (chunks.length > 1) ? removeQuotes(chunks[1]).toLowerCase() : null;
		String val = (chunks.length > 2) ? removeQuotes(chunks[2]) : null;
		
		if ("dir".equals(key) && val != null){
			String dir = val + System.getProperty("file.separator");
			logger.info("Resource dir was set to '" + dir + "'");
			resourceDir = dir; 
			return;
		}
		
		if ("nextbyresp".equals(key)){
			if(val==null || val.isEmpty()){
				stepRule.setNextByResp(true);
			}else{
				stepRule.setNextByResp(FileStringTools.getBoolean(val));
			}
			return;
		}
		if ("nextbyusr".equals(key)){
			if(val==null || val.isEmpty()){
				stepRule.setNextByUsr(true);
			}else{
				stepRule.setNextByUsr(FileStringTools.getBoolean(val));
			}
			return;
		}
		if ("playdelay".equals(key)){
			if(val==null || val.isEmpty()){
				logger.error("No value for play delay! ");
				return;
			}
			audioRule.setDelay(FileStringTools.getDur(val));
			return;
		}
		if ("name".equals(key) || "title".equals(key)){
			if(val==null || val.isEmpty()){
				logger.error("No value for block/taskset name! ");
				return;
			}
			this.tset.setBrief(val);
			return;
		}
		if ("canpause".equals(key)){
			if(val==null || val.isEmpty()){
				audioRule.canPause(true);
			}else{
				audioRule.canPause(FileStringTools.getBoolean(val));
			}
			return;
		}
		
		if ("shuffle".equals(key)){
			if(val==null || val.isEmpty()){
				isShuffleTasks = true;
			}else{
				isShuffleTasks = FileStringTools.getBoolean(val);
			}
			return;
		}

		if ("shufflechoice".equals(key)){
			if(val==null || val.isEmpty()){
				isShuffleChoices = true;
			}else{
				isShuffleChoices = FileStringTools.getBoolean(val);
			}
			return;
		}
		
		if ("blocksize".equals(key)){
			//-- example,   #CONF, blocksize, 10, "Resting message" 
			if(val==null || val.isEmpty()){
				throw new IllegalArgumentException("int value is missing for CONF 'blocksize'");
			}else{
				try{
					this.tsetBlockSize = Integer.parseInt(val);
					if (chunks.length > 3){
						restInfoTxt = removeQuotes(chunks[3]); 
					}
				}catch(NumberFormatException exp){
					throw new IllegalArgumentException("Wrong Integer value '" + val + "' for 'blocksize' CONF key");
				}
			}
			return;
		}
		throw new IllegalArgumentException("No such configuration key as '" + key + "'");
	}

	
	
	
	private void parseTextDisplays(TaskBits taskBit) throws IllegalArgumentException{
		for(int i = 1; i < taskBit.lineChunks.length ; i++){
//			System.out.println(taskBit.lineChunks[i] +" \t" + taskBit.hasProcessed.get(i));
			
			if(	taskBit.hasProcessed.get(i)){ //-- already a display!
				continue;
			}
			String chunk = removeQuotes(taskBit.lineChunks[i].trim());
//			System.out.println("CHUNK to add " + chunk);
			taskBit.addDisp(i, new TextDisplay(chunk));
		}
	}
	
	
	private void parseTaskBrief(TaskBits taskBit) throws IllegalArgumentException{
		for(int i = 1; i < taskBit.lineChunks.length ; i++){
			if(	taskBit.hasProcessed.get(i)){ //-- already a display!
				continue;
			}
			String chunk = removeQuotes(taskBit.lineChunks[i].trim());
			
			if (chunk.startsWith("!")){
				String brief = chunk.substring(1).trim();
				taskBit.getTask().setBrief(brief);
				taskBit.hasProcessed.put(i, true);
			}
		}
	}

	
	
	/**
	 * Find any audio in chunks -> if any
	 * 
	 * @param chunks
	 * @return
	 * @throws IllegalArgumentException
	 */
	private List<AudioDisplay> parseAudioDisplays(TaskBits taskBit) throws IllegalArgumentException{
		Map<String, WavClip> wavs = new HashMap<String, WavClip>();
		Map<String, Integer> wav2ix = new HashMap<String, Integer>();

		
		CHUNKS: for(int i = 1; i < taskBit.lineChunks.length ; i++){
			if(taskBit.lineChunks[i] == null) continue;
			for(String ext : AUDIO_EXT){
				String chunk = removeQuotes(taskBit.lineChunks[i].trim()).trim();
				if(chunk.endsWith(ext)){
					wavs.put(chunk, null);
					wav2ix.put(chunk, i);
					continue CHUNKS;
				}
			}
		}
		
		READ_WAV: for(String wavPath : wavs.keySet()){
			WavClip wavClip = null;
			
			String errMsg = "";  
			
			File wavFilePath = null;
			if( (wavPath.charAt(0)=='/') ||  //-- absolute path?
				(wavPath.charAt(1)==':')){   //-- *nix or windows?
				wavFilePath = new File(wavPath);
			}else{  //-- not absolute path
				if(resourceDirAbsPath!=null){
					wavFilePath = new File(resourceDirAbsPath, wavPath);
				}else{
					wavFilePath = new File(wavPath);
				}
			}
			if(wavFilePath.exists()){
				try {
					logger.debug("Reading wav file from '" + wavFilePath.getAbsolutePath());
					WavClip wav = new WavClip(wavFilePath.getAbsolutePath());
					if(wav!=null){
						wavs.put(wavPath, wav);
						continue READ_WAV;
					}
				} catch (IOException e) {
					logger.error("Error while reading wav file from '" + wavFilePath.getAbsolutePath());
					e.printStackTrace();
				}
			}else{
				errMsg += "No wav file at '" + wavFilePath.getAbsolutePath();
//				throw new IllegalArgumentException("No wav file at '" + wavFilePath.getAbsolutePath());
			}
			
			
			//-- try relative to csv file!
			if(   csvFileDir!=null && 
				(!( wavPath.charAt(0)!='/' || wavPath.charAt(1)==':'))
			   ){
				wavFilePath = new File(csvFileDir, wavPath);
				if(wavFilePath.exists()){
					try {
						logger.debug("Reading wav file from '" + wavFilePath.getAbsolutePath());
						WavClip wav = new WavClip(wavFilePath.getAbsolutePath());
						if(wav!=null){
							wavs.put(wavPath, wav);
							continue READ_WAV;
						}
					} catch (IOException e) {
						logger.error("Error while reading wav file from '" + wavFilePath.getAbsolutePath());
						e.printStackTrace();
					}
				}else{
					errMsg += "No wav file at '" + wavFilePath.getAbsolutePath();
//					throw new IllegalArgumentException("No wav file at '" + wavFilePath.getAbsolutePath());
				}
			}
			
			
			
			
			
			//-- try 
			if(wavClip == null && classForResourceloading!=null){
				InputStream is = classForResourceloading.getResourceAsStream(wavPath);
				if (is==null){
					ClassLoader loader = classForResourceloading.getClassLoader();
					if (loader!=null){
						is = loader.getResourceAsStream(wavPath);
					}else{ //-- try this classloader
						is = this.getClass().getResourceAsStream(wavPath);
					}
				}
				if(is!=null){
					try {
						wavClip = new WavClip(is);
						if(wavClip!=null){
							wavs.put(wavPath, wavClip);
							continue READ_WAV;
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					}
				}else{ // is == null
					if(classForResourceloading != null){
						errMsg += "\nCan't read '" + wavPath +"' from classloader '" + classForResourceloading.getName() +"'";
					}else{
						errMsg += "\nCan't read '" + wavPath +"' from classloader '" + this.getClass().getName() +"'";
					}
				}
				
				if (!errMsg.isEmpty()){
					throw new IllegalArgumentException(errMsg);
				}
			}
		} //-- READ_WAV
		
		List<AudioDisplay> disps = new ArrayList<AudioDisplay>();
		for(String wavPath : wavs.keySet()){
			
			AudioDisplay adisp = new AudioDisplay(wavs.get(wavPath));
			adisp.setBrief(wavPath);
			adisp.setAudioRule(audioRule.build());
			disps.add(adisp);
			
			int ix = wav2ix.get(wavPath);
			taskBit.addDisp(ix, adisp);
		}
		return disps;
	}
	
	
	
	private void parseChoices(TaskBits taskBit) throws IllegalArgumentException{
		Task task = taskBit.getTask();
		if (!(task instanceof MultichoiceTask)){
			//-- not a mulitchoice task!!!  do nothing here!
			return;
		}
		
		MultichoiceTask multi = (MultichoiceTask) task;
		
		//-- add choices!
		for(int i = 1 ; i < taskBit.lineChunks.length ; i++){
			if(taskBit.hasProcessed.get(i)) continue;
			
			int choiceAt = taskBit.lineChunks[i].indexOf(CHOICE_MARK);
			if(choiceAt >= 0 ){
				String label = removeQuotes(taskBit.lineChunks[i].substring(0, choiceAt).trim()).trim();
				String value = removeQuotes(taskBit.lineChunks[i].substring(choiceAt + CHOICE_MARK.length()).trim()).trim();
				if (value.isEmpty()){
					value = label;
				}
				multi.addChoice(label, value);
				taskBit.hasProcessed.put(i, true);
			}
		}
		if(isShuffleChoices){
			multi.shuffleChoices();
		}
	}
}
