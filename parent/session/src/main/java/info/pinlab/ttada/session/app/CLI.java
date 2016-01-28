package info.pinlab.ttada.session.app;


import info.pinlab.ttada.session.Registry;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.SessionFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Entry point for Pinplayer apps.
 * deployed programs starts with 'main' in this class
 * 
 * Usage
 * init(): inits recources
 * startSession: start GUI

 * In between: set up your TaskSet! 
 * 
<pre>
@code
public static void main(String[] args) throws Exception{
	CLI cli = new CLI();
	cli.addRegItem(Key.LOCAL_AVAILABLE, true);
	cli.run(args);
}

</pre>
 * 
 * 
 * 
 * 
 * 
 * @author Gabor Pinter
 *
 */
public class CLI{
	public static Logger LOG = LoggerFactory.getLogger(CLI.class);
	/* hold all configuration here after #init */
	private final Registry runtimeConf = Registry.getEmptyInstance();
//	static SessionControllerWithAudio session = null;

	static Map<String, Key> shortCutArgMap = new HashMap<String, Key>();
	static Map<String, Boolean> shortCutIsBool = new HashMap<String, Boolean>();
	static Map<Key, String> keyMap = new HashMap<Key, String>();

	static{
//		BasicConfigurator.configure();
		
		shortCutArgMap.put ("-usrdir", 		Key.LOCAL_DIR);
		shortCutArgMap.put ("-tasksetref", 	Key.TASK_SET_REF);
		shortCutArgMap.put ("-tsetref", 	Key.TASK_SET_REF);
		
		shortCutArgMap.put ("-csv", 		Key.TASK_SET_BRIEF);
		shortCutIsBool.put ("-cshuffle", 	true);

		
		shortCutArgMap.put ("-tshuffle", 	Key.TASK_SET_SHUFFLE_TASK);
		shortCutIsBool.put ("-tshuffle", 	true);
		
		shortCutArgMap.put ("-cshuffle", 	Key.TASK_SET_SHUFFLE_CHOICE);
		shortCutIsBool.put ("-cshuffle", 	true);
		
		shortCutArgMap.put ("-udir", 		Key.USER_DIR);
		shortCutArgMap.put ("-usrdir", 		Key.USER_DIR);
		shortCutArgMap.put ("-userdir", 	Key.USER_DIR);
		
		shortCutArgMap.put ("-usr", 		Key.USER);
		shortCutArgMap.put ("-user", 		Key.USER);
		
		shortCutArgMap.put ("-savetset", 	Key.TASK_SET_IS_SAVE_LOCALLY);
		shortCutIsBool.put ("-savetset", 	true);

		shortCutArgMap.put ("-savewav", 	Key.RESPONSE_SAVE_AS_WAV);
		shortCutIsBool.put ("-savewav", 	true);
		
		shortCutArgMap.put ("-printjson", 	Key.TASK_SET_JSON_IS_PRINT);
		shortCutIsBool.put ("-printjson", 	true);
		
		shortCutArgMap.put ("-gui", 		Key.PLAYER_HAS_GUI);
		shortCutIsBool.put ("-gui", 		true);

		shortCutArgMap.put ("-remote", 		Key.REMOTE_AVAILABLE);
		shortCutIsBool.put ("-remote",		true);

		shortCutArgMap.put ("-local", 		Key.LOCAL_AVAILABLE);
		shortCutIsBool.put ("-local", 		true);

		shortCutArgMap.put( "-json", 		Key.TASK_SET_JSON);

		shortCutArgMap.put( "-refs", 		Key.TASK_SET_JSON_USE_REFS);
		shortCutIsBool.put ("-refs", true);
		
		shortCutArgMap.put( "-debug", 		Key.DEBUG_WINDOW);
		shortCutIsBool.put ("-debug", 		true);

		
		
	}


	public static String getCmdLineArgName(Key key){
		for(Map.Entry<String, Key> entry : shortCutArgMap.entrySet()){
			if(key.equals(entry.getValue())){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static String getCmdLineFlagName(Key key){
		return getCmdLineFlagName(key, true);
	}
	
	public static String getCmdLineFlagName(Key key, boolean trueFlag){
		String trueValueKey = CLI.getCmdLineArgName(key);
		if(trueValueKey==null)
			return null;
		if(trueFlag){
			return trueValueKey;
		}
		return "-no" + trueValueKey.substring(1);
	}


	/**
	 * Parsing runtime arguments. 
	 * 
	 * <p>
	 * <u>Usage</u>
	 * <pre>
	 *  CLI.main(); 
	 *  CLI.main(args);
	 *  
	 *  //-- Custom run:
	 *  CLI cli = new CLI();
	 *  Session session = cli.getInit(); 
	 *  //-- do whatever with {@link SessionFactory} object
	 *  cli.run();
	 * 
	 * </pre>
	 * </p>
	 * 
	 * @param args
	 */
	private void parseArgs(String args[]){
		LOG.info("Parsing runtime arguments");
		FOREACH_ARG: for(int i=0; i<args.length ; ){
			String arg = args[i];
//			System.out.println(i + " " + arg);
			if(arg.startsWith("-no")){
				String trueKey = "-" + arg.substring(3);
//				System.out.println("ARG " + arg +" " + trueKey);
				if (shortCutArgMap.containsKey(trueKey)){
					runtimeConf.put(shortCutArgMap.get(trueKey), false);
					i++; continue FOREACH_ARG;
				}
			}
			if(arg.startsWith("-")){
				if(shortCutIsBool.containsKey(arg)){
					if(args.length >= i+1
							& ("true".equals(args[i+1]) || "false".equals(args[i+1])) 
							){
						runtimeConf.put(shortCutArgMap.get(arg), args[i+1]);
						i += 2; continue FOREACH_ARG;
					}else{
						runtimeConf.put(shortCutArgMap.get(arg), true);
						i++; continue FOREACH_ARG;
					}
				}
				if (shortCutArgMap.containsKey(arg)){
					runtimeConf.put(shortCutArgMap.get(arg), args[(i+1)]);
					i += 2; continue FOREACH_ARG;
				}
				//-- arg starts with "-" but not a key
				throw new IllegalArgumentException("No such key as '" + arg + "'");
			}
			//-- arg doesnot start with "-"
			int ix = arg.indexOf("=");
			
			if(ix>-1){
				String val = arg.substring(ix+1);
				arg = arg.substring(0,ix);
				for(Key key: Key.values()){
					if(key.getKeyName().equals(arg)){
						LOG.info("Runtime arg '"+  key +  "' = '" + val +"'");
						runtimeConf.put(key, val);
						i++; continue FOREACH_ARG;
					}
				}
				LOG.error("No such key as '" + arg +"'");
			}
			throw new IllegalArgumentException("No such key as '" + arg + "'");
		}//-- foreach arg
	}
	
	
	private final SessionFactory sessionFactory;
	public CLI(){
		sessionFactory = new SessionFactory();
	}
	
	public SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public void addRegItem(Key key, String val){ 	this.runtimeConf.put(key, val);  	}
	public void addRegItem(Key key, boolean val){ 	this.runtimeConf.put(key, val);  	}
	
	
	public void run(String[] args){
		List<String> argsAsList = new ArrayList<String>();

		//-- add VM level properties --//
		Properties props = System.getProperties();
		Enumeration<?> propEnum = props.propertyNames();	
		while(propEnum.hasMoreElements()){
			String key = (String)propEnum.nextElement();
			if(key.startsWith("info.pinlab.")){
				argsAsList.add(key +"=" + props.getProperty(key));
			}
		}
		for(String arg : args){
			argsAsList.add(arg);
		}
//		runtimeConf.put(Key.LOCAL_DIR_ON_DESKTOP, true);
//		runtimeConf.put(Key.LOCAL_DIR, "pinplayer");
//		runtimeConf.put(Key.TASK_SET_IS_SAVE_LOCALLY, true);
//		runtimeConf.put(Key.TASK_SET_SHUFFLE_CHOICE, true);
//		runtimeConf.put(Key.TASK_SET_SHUFFLE_TASK, true);
//		runtimeConf.put(Key.TASK_SET_JSON_IS_PRINT, true);
//		runtimeConf.put(Key.TASK_SET_JSON_USE_REFS, false);
//		runtimeConf.put(Key.PLAYER_WITH_GUI, true);

		parseArgs(argsAsList.toArray(new String[argsAsList.size()]));
		
//		TaskSet tset = new TaskSet();
//		tset.add(new InfoTask("CLI's task set!"));
//		Initializer.setTaskSet(tset);
//		init();
		
		sessionFactory
			.setConfig(runtimeConf)
			.build()
			.startSession();
	}
	
	
	
	public static void main() throws Exception{
		CLI.main(new String[]{});
	}
	
	
	public static void main(String [] args) throws Exception{
		new CLI().run(args);
		
//		BasicConfigurator.configure();
//		InitializerInstance init = new InitializerInstance();
//		
//		List<String> argsAsList = new ArrayList<String>();
//
//		//-- add VM level properties --//
//		Properties props = System.getProperties();
//		Enumeration<?> propEnum = props.propertyNames();	
//		while(propEnum.hasMoreElements()){
//			String key = (String)propEnum.nextElement();
//			if(key.startsWith("info.pinlab.")){
//				argsAsList.add(key +"=" + props.getProperty(key));
//			}
//		}
//		for(String arg : args){
//			argsAsList.add(arg);
//		}
////		runtimeConf.put(Key.LOCAL_DIR_ON_DESKTOP, true);
////		runtimeConf.put(Key.LOCAL_DIR, "pinplayer");
////		runtimeConf.put(Key.TASK_SET_IS_SAVE_LOCALLY, true);
////		runtimeConf.put(Key.TASK_SET_SHUFFLE_CHOICE, true);
////		runtimeConf.put(Key.TASK_SET_SHUFFLE_TASK, true);
////		runtimeConf.put(Key.TASK_SET_JSON_IS_PRINT, true);
////		runtimeConf.put(Key.TASK_SET_JSON_USE_REFS, false);
////		runtimeConf.put(Key.PLAYER_WITH_GUI, true);
//
//		parseArgs(argsAsList.toArray(new String[argsAsList.size()]));
//		
////		TaskSet tset = new TaskSet();
////		tset.add(new InfoTask("CLI's task set!"));
////		Initializer.setTaskSet(tset);
////		init();
//		
//		init
//			.setConfig(runtimeConf)
//			.build()
//			.startSession();
		
	}
}

