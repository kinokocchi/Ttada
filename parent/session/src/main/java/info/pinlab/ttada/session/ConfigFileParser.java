package info.pinlab.ttada.session;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.RecordTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.utils.FileStringTools;


public class ConfigFileParser {
	static Logger LOG = LoggerFactory.getLogger(ConfigFileParser.class);

	private static Map<String, Class<? extends Task>> labelToClazzMap = new HashMap<String, Class<? extends Task>>(); 

	ResourceCollection currentTask ; 

	private static class ResourceCollection{
		final Task task ;
		final StepRuleBuilder stepRuleBuilder;
		final AudioRuleBuilder aruleBuilder;
		
		ResourceCollection(Task task){
			this.task = task;
			stepRuleBuilder = new StepRuleBuilder();
			aruleBuilder = new AudioRuleBuilder();
		}
		Task build(){
			task.setStepRule(stepRuleBuilder.build());
			if( task instanceof RecordTask){
				((RecordTask) task).setRecRule(aruleBuilder.build());
			}
			return task;
		}
	}

	static{
		labelToClazzMap.put("@info", InfoTask.class);
		labelToClazzMap.put("@rec", RecordTask.class);
		labelToClazzMap.put("@multi", MultichoiceTask.class);
	}

	
	private TaskSet tset = null;
	private String path = null;
	private String confAsString = null;
	private boolean isHaltOnError = true;
	private int currentLine = -1;

	private InputStream is = null;
	
	
	private void setInputStream(InputStream is){
		this.path = null;
		this.is = is;
	}
	
	private void setFilePath(String path){
		this.path = path;
		InputStream is = ConfigFileParser.class.getResourceAsStream(this.path);
		if(is==null){
			throw new IllegalArgumentException("Can't file resource : " + path + " in folder '" + ConfigFileParser.class.getPackage().getName() + "'");
		}
	}

	private void setConfAsText(String txt){
		this.confAsString = txt;
	}
	
	
	private ConfigFileParser(){
		tset = new TaskSet();
	}
	
	
	public TaskSet parse(){
		if(is!=null){
//			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					currentLine++;
					parseLine(line);
					//					sb.append(line).append("\n");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(confAsString != null){
			String[] lines = confAsString.split("\\r?\\n");
			for(String line : lines){
				currentLine++;
				parseLine(line);
			}
		}
		
		if(currentTask!=null){
			tset.add(currentTask.build());
			currentTask = null;
		}
		return tset;
	}


	private void parseLine(String line){
		line = line.trim();
		if (line.length()==0){
			return; //-- empty line
		}
		List<String> chunks = FileStringTools.split(line);
		
		if(chunks.size()==0){ //-- empty line
			return;
		}

		if(chunks.size()==1){
//			String taskType = chunks.get(0);
			Class<? extends Task> clazz = labelToClazzMap.get(line);
			if(clazz==null){
				reportErr("No such task as '"+ line +"'");
				System.exit(-1);
			}else{
				if(currentTask!=null){
					tset.add(currentTask.build());
					currentTask = null;
				}
				try{
					Task task =  clazz.newInstance();
					currentTask = new ResourceCollection(task);
				}catch(IllegalAccessException e){
					reportErr(e.getMessage());
					System.exit(-1);
				}catch(InstantiationException e){
					reportErr(e.getMessage());
					System.exit(-1);
				}
			}
			return;
		}
		//-- if size is bigger
		
		String key = chunks.get(0).trim();
		String val = chunks.get(1).trim();
		
		if("$text".equals(key)){
			int ix = line.indexOf("$text");
			currentTask.task.addDisplay(new TextDisplay(line.substring(ix+"$text".length()+1)));
			return;
		}
		
		if("$maxreclen".equals(key)){
			int ms = FileStringTools.getDur(val);
			currentTask.aruleBuilder.setRecLen(ms);
			return;
		}
		
		if("$maxrecn".equals(key)){
//			System.out.println("'" + key +"'");
			int recN = Integer.parseInt(val);
			currentTask.aruleBuilder.setMaxRecN(recN);
			return;
		}
		if("$maxplayn".equals(key)){
			int playN = Integer.parseInt(val);
			currentTask.aruleBuilder.setMaxPlayN(playN);
			return;
		}
	}


	private void reportErr(String msg){
		LOG.error("[" + currentLine +"]  "+ msg);
		if(isHaltOnError){
			LOG.error("Exiting on error!");
			System.exit(0);
		}
	}
	

	public TaskSet getTaskSet(){
		return tset;
	}


	synchronized static public TaskSet parseText(String text){
		ConfigFileParser parser = new ConfigFileParser();
		parser.setConfAsText(text);
		return parser.parse();
	}

	synchronized static TaskSet parseInputStream(InputStream is){
		ConfigFileParser parser = new ConfigFileParser();
		parser.setInputStream(is);
		return parser.parse();
	}
	
	synchronized static TaskSet parseFile(String path){
		ConfigFileParser parser = new ConfigFileParser();
		parser.setFilePath(path);
		return parser.parse();
	}
	
}
