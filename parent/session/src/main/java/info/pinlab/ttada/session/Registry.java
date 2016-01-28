package info.pinlab.ttada.session;

import info.pinlab.ttada.cache.disk.LocalSaveHookForTxtResponse;
import info.pinlab.ttada.cache.disk.LocalSaveHookForWavResponse;
import info.pinlab.utils.FileStringTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class Registry implements Iterable<Registry.Key> {
	public static Logger LOG = LoggerFactory.getLogger(Registry.class);
	public static int id_ = 0;
	private static final Map<Object, Registry> propertyCache ;

	private static Registry root;
	private static Registry defaultConf;
	

    
	
	
	public enum Key{
		NAMESPACE(				"info.pinlab.namespace", 				"info.pinlab."), 

		PINPLAYER_VERSION(		"info.pinlab.app.pinplayer.version", 	"n/a"), 

		
		USER(					"info.pinlab.app.user.id", 				"UNK"),
		DEFAULT_USER(			"info.pinlab.app.user.id.default", 			"default"),
		DEFAULT_USER_ALLOWED(	"info.pinlab.app.user.id.default.allowed",	"false"),
		EMPTY_USER_ALLOWED(		"info.pinlab.app.user.id.empty.allowed",	"false"),

		ALLOW_AUDIO(			"info.pinlab.app.allow_audio", 				"true"),
		HAS_AUDIO(				"info.pinlab.app.has_audio", 				"false"),
		
		SESSION_ID(				"info.pinlab.player.session.id",		""), 	//  

		DIST_PROPERTY_FILE(	"info.pinlab.default.conf.filename", 		"distribution-default.properties"),
		USER_DIR(		"info.pinlab.user.dir", 						".pinlab"),
		USER_CONF_FILE(	"info.pinlab.user.default.conf.file",			"user-default.properties"),
//		USER_DEFAULT_DIR(		"info.pinlab.user.dir.default", 		".pinlab"),
		
//		PLAYER_SHUFFLE_TASKS(	"info.pinlab.player.shuffle.tasks",		"false"),
//		PLAYER_SHUFFLE_CHOICES(	"info.pinlab.player.shuffle.choices",	"false"),
		PLAYER_HAS_GUI(			"info.pinlab.player.hasgui",			"true"),
		PLAYER_HAS_SOUND(		"info.pinlab.player.hassnd",			"false"),
		PLAYER_GEN_LOCAL_JSON(	"info.pinlab.player.gen.local.json",	"true"),
		PLAYER_RESOURCE_ROOT(	"info.pinlab.player.resource.root.dir",	"."),
		PLAYER_REPONSE_SAVE_DIR("info.pinlab.player.out.dir",			"responses"),
		
		PLAYER_TITLE(			"info.pinlab.player.title",				"PinPlayer"),
		PLAYER_GUI_TOP_FQCN(    "info.pinlab.player.app.guitop",        "info.pinlab.ttada.view.swing.PlayerTopPanel"),
		
		
		SOURCE_ENUM_BUNDLED( 	"info.pinlab.source.bundled",			"bundled"),	
		SOURCE_ENUM_LOCAL(	 	"info.pinlab.source.local",				"local"),	
		SOURCE_ENUM_REMOTE( 	"info.pinlab.source.remote",			"remote"),	

		TASK_SET_REF(		 	"info.pinlab.player.taskset.id",				""), 		//  a TaskSet to play
		//-- TaskSet resource from files:
		TASK_SET_FILE_TYPE(	"info.pinlab.player.taskset.file.type",			"conf"),	//  from wehre to load task set? : "bundled", "csv", "remote", "local", "json", "conf"
		TASK_SET_RESOURCELOADER_CLASS("info.pinlab.player.taskset.resourceloader.class",			""),	//  if from .jar, where to load resources? 
		TASK_SET_CSV(		 	"info.pinlab.player.taskset.file.csv",			"taskset.csv"),  //  absolute path to .csv to play
		TASK_SET_JSON(			"info.pinlab.player.taskset.file.json",			"taskset.json"),
		TASK_SET_CONF(			"info.pinlab.player.taskset.file.conf",			"taskset.conf"),
		//-- directory for media resources (relative to TaskSet csv|json|conf files) 
		TASK_SET_MEDIA_DIR(		"info.pinlab.player.taskset.resource.dir",			""),

		TASK_SET_BRIEF(			 	"info.pinlab.player.taskset.brief",				""), 		//  description about taskset
		TASK_SET_SHUFFLE_TASK(	 	"info.pinlab.player.taskset.shuffletasks",		"false"), 	// shuffles TaskSet and uploads permutation
		TASK_SET_SHUFFLE_CHOICE( 	"info.pinlab.player.taskset.shufflechoices",	"false"), 	// shuffles TaskSet and uploads permutation
		TASK_SET_IS_SAVE_LOCALLY(	"info.pinlab.player.taskset.save.local",		"false"), 	//  whether to save taskset locally 
		TASK_SET_IS_SAVE_REMOTELY(	"info.pinlab.player.taskset.save.remote",		"false"), 	//  whether to save taskset locally 
		TASK_SET_JSON_IS_PRINT(		"info.pinlab.player.taskset.printjson",			"false"), 	//  whether to save taskset locally 
		TASK_SET_JSON_USE_REFS(		"info.pinlab.player.taskset.userefs",			"true"),	// whether to use references (normaliziation) during json serialization 
		
		
		AUDIO_PLAY_DEV_LOADER_FQCN(	"info.pinlab.player.loader", 	"info.pinlab.snd.oal.OpenAlPlayerFactory"),
		AUDIO_REC_DEV_LOADER_FQCN (	"info.pinlab.recorder.loader", 	"info.pinlab.snd.oal.OpenAlRecorderFactory"),
		REC_DEVICE_ID(				"info.pinlab.rec.device",		"-1"), 	// '-1' default, 0...n
		
		DEBUG_WINDOW(			"info.pinlab.debug.window",				"true"),

		
		//** LOCAL STUFF **//
		LOCAL_AVAILABLE(		"info.pinlab.pin.app.cache.local",           "false"),
		LOCAL_DIR(				"info.pinlab.pin.app.cache.local.dir",       ".pinlab"),
		LOCAL_ABSOLUTE_PATH(	"info.pinlab.pin.app.cache.local.path",	 	 ""),
		LOCAL_SAVE_HOOKS(		"info.pinlab.pin.app.cache.local.hooks",	LocalSaveHookForTxtResponse.class.getCanonicalName()
																	+ ";" + LocalSaveHookForWavResponse.class.getCanonicalName()
				                                                            ),
		
//		LOCAL_RESOURCE_DIR(		"info.pinlab.pin.app.local.resource.dir",         ""),  //-- load resources from here when reading TaskSet conf/csv files
		
		//-- look for desktop
		LOCAL_DIR_ON_DESKTOP(	"info.pinlab.pin.app.cache.local.ondesktop",       "true"),
		
		RESPONSE_SAVE_AS_WAV(	"info.pinlab.pin.app.resp.save.wav",        "true"),  //-- shall WavClip responses saved LOCALLY as wav?
		RESPONSE_SAVE_AS_TEXT(	"info.pinlab.pin.app.resp.save.text",       "true"),  //-- shall text responses LOCALLY saved  into a text file?  
		
		
		
		//** REMOTE STUFF **//
		REMOTE_AVAILABLE(		"info.pinlab.pin.app.remote",						"false"),
		REMOTE_CACHE_BUILDER_FQCN("info.pinlab.pin.app.remote.handler.fqcn",		"info.pinlab.ttada.restcache.HttpClient43Cache$HttpClient43CacheBuilder"), //-- fully qualified classname to handle remote requests; implements remoteCache interface
		REMOTE_ENROLLER_FQCN(	"info.pinlab.pin.app.remote.enroller.fqcn",			"info.pinlab.ttada.restcache.RestEnrollController"), 
		REMOTE_PROTOCOL(		"info.pinlab.pin.app.remote.server.protocol", 		"http"),
		REMOTE_IP(				"info.pinlab.pin.app.remote.server.ip", 			"127.0.0.1"),
		REMOTE_HOST(			"info.pinlab.pin.app.remote.server.host",			"localhost"),
		REMOTE_PORT(			"info.pinlab.pin.app.remote.server.port", 			"8000"),
		REMOTE_PING_PATH(		"info.pinlab.pin.app.remote.server.ping.path", 		"ping"),
		REMOTE_APPPING_PATH(	"info.pinlab.pin.app.remote.server.appping.path",	"appping"),
		REMOTE_LOGIN_PATH(		"info.pinlab.pin.app.remote.server.login.path", 	"app-login"),
		REMOTE_LOGIN_ID(		"info.pinlab.pin.app.remote.server.login.id", 		"pinplayer-app"),
		REMOTE_LOGIN_PWD(		"info.pinlab.pin.app.remote.server.login.pwd", 		"password-for-rest-server"),
		REMOTE_RESOURCE_ROOT(	"info.pinlab.pin.app.remote.server.rest.rootpath", 	"rest"),
		; 
		final String keyName;
		private final String keyDefValue;
		
		Key(String key, String value){
			this.keyName = key;
			this.keyDefValue = value;
		}
		public String getDefaultValue(){
			return keyDefValue;
		}
		public String getKeyName(){
			return keyName;
		}
	}


	
	/**
	 * Loading configuration files.
	 * 1. hard-wired defaults
	 * 2. distribution defaults - from file
	 * 3. user defaults - from file 
	 */
	static {
		root = new Registry(System.getProperties(), null);
		LOG.info("Root conf created as 'conf_" + root.id +"'");
		defaultConf = new Registry(null, root);
		LOG.info("Default conf created as 'conf_" + defaultConf.id +"'");

		propertyCache = new HashMap<Object, Registry>();
		
		//-- read in distribution-default values;
		for(Key k : Key.values()){
			defaultConf.properties.put(k.keyName, k.keyDefValue);
		}
		
//		//-- read in distribution-default values;
//		InputStream is = Configurator.class.getResourceAsStream(Key.DIST_CONF_FILE.keyDefValue);
//		if(is!=null){
//		try {
//			LOG.info("Loading distribution conf '" + Key.DIST_CONF_FILE.keyDefValue + "' to conf_" + defaultConf.id);
//			defaultConf.properties.load(is);
//		} catch (IOException ignore) {	}
//		}else{
//			LOG.info("No distribution conf  at '" + Key.DIST_CONF_FILE.keyDefValue + "'");
//		}
//		
		
//		//-- read in user-specific default --//
//		final File usrDir = new File(System.getProperty("user.home")  +  FileStringTools.SEP + Key.USER_DIR.keyDefValue);
//		if(!usrDir.exists()){
//			if(usrDir.mkdirs()){
//				LOG.info("Creating user directory in '" + usrDir.getAbsolutePath() + "'");
//			}else{
//				//-- can't create user dir!
//			}
//			//-- try "desktop" in English not Japanese 
//		}
//		final File usrConfFile = new File(usrDir.getAbsolutePath() +  FileStringTools.SEP + Key.USER_CONF_FILE.keyDefValue);
//		if(usrConfFile.exists() && usrConfFile.isFile()){
//			LOG.info( "Found user conf file '" + usrConfFile.getAbsolutePath() + "'" );
//			try {
//				defaultConf.put(new FileInputStream(usrConfFile));
//				LOG.info("Loading user conf into 'conf_" + defaultConf.id+"'");
//			} catch (FileNotFoundException e) {
//				LOG.info( e.getClass().getSimpleName() + " Can't read user conf file in '" + usrConfFile.getAbsolutePath() + "'" );
//			} catch (IOException e) {
//				LOG.info( e.getClass().getSimpleName() + " Can't read user conf file in '" + usrConfFile.getAbsolutePath() + "'" );
//			}
//		}else{
//			LOG.info( "No user conf file found in '" + usrConfFile.getAbsolutePath() + "'" );
//		}
	}
	
	/**
	 * Returns configuration with default values.
	 * Defaults are created by loading:
	 * <ul>
	 *   <li> Hard-coded defaults </li>
	 *   <li> Distribution defaults </li>
	 *   <li> User defaults </li>
	 * </ul>
	 * 
	 * @return
	 */
	public static Registry getDefaultInstance(){
		Registry conf = new Registry(defaultConf);
		LOG.info("New conf (default's child) created 'conf_" + conf.id +"'");
		return conf;
	}
	
	public static Registry getEmptyInstance(){
		Registry conf = new Registry(null, null);
		LOG.info("New empty conf 'conf_" + conf.id );
		return conf;
	}
	
	
	public static Registry getInstance(Object id){
		if(propertyCache.containsKey(id)){
			return propertyCache.get(id);
		}
		Registry conf = getDefaultInstance();
		propertyCache.put(id, conf);
		LOG.info("New conf for '" + id + "' named " + conf.id );
		return conf;
	}
	
	/**
	 * Creates a new configurator with the given parent.
	 * 
	 * @param id
	 * @param parent
	 * @return
	 */
	public static Registry getInstance(Object id, Registry parent){
		if(propertyCache.containsKey(id)){
			return propertyCache.get(id);
		}
		Registry conf = new Registry(parent);
		propertyCache.put(id, conf);
		LOG.info("New conf for '" + id + "' named " + conf.id );
		return conf;
	}
	
	private final Properties properties;
	private final Registry parent;
	
	private Registry(Properties prop, Registry parent){
		id = id_++;
		this.parent = parent;
		if(prop==null){
			properties = new Properties();
		}else{
			properties = prop;
		}
	}
	
	public final int id;
	private Registry(Registry parent){
		id = id_++;
		this.parent = parent;
		properties = new Properties();
	}
	
	static Registry load(InputStream is) throws IOException{
		Registry conf = new Registry(null, null);
		conf.properties.load(is);
		return conf;
	}
	
	
	/**
	 * Shallow copy!
	 * Overwrite present values!
	 * 
	 * @param master
	 */
	public void put(Registry master){
		Enumeration<Object> keys = master.properties.keys();
		while(keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			String val = master.properties.getProperty(key);
			put(key, val);
		}
	}
	
	public void put(Map<Key, String> master){
		for(Key key: master.keySet()){
			put(key.keyName, master.get(key));
		}
	}
	
	
	public void put(InputStream is) throws IOException{
		Properties newProp = new Properties();
		newProp.load(is);
		Enumeration<Object> keys = newProp.keys();
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			String newVal = newProp.getProperty(key);
			if( key!=null && !key.isEmpty() /* non-empty key */ 
					/*&& newVal!=null && !newVal.isEmpty() */){
				put(key, newVal);
			}
		}
//		for(Entry<Object, Object> entry : newProp.entrySet()){
//			String key = entry.getKey().toString();
//			String newVal = entry.getValue().toString();
//			if(key!=null && !key.isEmpty() && newVal!=null && !newVal.isEmpty()){
//				put(key, newVal);
//			}
//		}
	}
	
	synchronized public void put(Key key, boolean val){
		put(key.keyName, val);
	}
	
	synchronized public void put(Key key, String val){
		put(key.keyName, val);
	}
	
	synchronized public void put(String key, boolean val){
		put(key, (val ? "true" : "false"));
	}
	
	synchronized private void put(String key, String val){
		String oldVal = this.get(key);
		if(oldVal!=null && !oldVal.equals(val)){
			LOG.info("conf_"+id+ " Overriding key '" + key + "'='" + oldVal +"' >> '" + val + "'");
		}else{
			LOG.info("conf_"+id+ " Adding key '" + key + "'='" + "'" + val + "'");
		}
		properties.put(key, val);
	}

	synchronized public boolean hasKey(Key key){
		return hasKey(key.keyName);
	}
	
	synchronized public boolean hasKey(String key){
		if(properties.containsKey(key)){
			return true;
		}else{
			if (this.parent != null)
				return parent.hasKey(key);
			return false;
		}
	}
	synchronized public List<String> getKeys(){
		return getKeys(false);
	}
	
	public List<String> getKeys(boolean getAll){
		List<String> keys = recursiveGetKeys();
		if(getAll){
			return keys;
		}else{
			List<String> filtered = new ArrayList<String>();
			for(String s : keys){
				if (s.startsWith(Key.NAMESPACE.keyDefValue)){
					filtered.add(s);
				}
			}
			return filtered;
		}
		
	}
	
	public List<String> recursiveGetKeys(){
		List<String> keys = new ArrayList<String>();
		if(parent!=null)
			keys.addAll(parent.recursiveGetKeys());
		
		for(Entry<Object, Object> entry : properties.entrySet()){
			keys.add(entry.getKey().toString());
		}
		return keys;
	}
	
	synchronized public boolean getBoolean(Key key){
		return getBoolean(key.keyName);
	}
	synchronized public boolean getBoolean(String key){
		String val = this.get(key);
		if(val!=null){
			return FileStringTools.getBoolean(val);
		}
		return false;
	}

	
	synchronized public List<String> getList(Key key){
		return getList(key.keyName);
	}
	
	/**
	 * Splits registry item by ';' 
	 * 
	 * @return a list of strings
	 */
	synchronized public List<String> getList(String key){
		List<String> list = new ArrayList<String >();
		String value = this.get(key);
		if (value == null || value.isEmpty()){
			return list;
		}
		for (String chunk : value.split(";")){
			list.add(chunk);
		}
		return list;
	}
	
	synchronized public Integer getInt(Key key){
		return getInt(key.keyName);
	}
	synchronized public Integer getInt(String key){
		String val = this.get(key);
		if(val!=null){
			try{
				return Integer.parseInt(val);
			}catch(NumberFormatException e){
				LOG.error("conf_"+id+  " It is not an integer, is it? '" + val +"'");
			}
		}
		return null;
	}
	
	
	/**
	 * Recursive get.
	 * 
	 * @param key
	 * @return
	 */
	synchronized public String get(Key key){
		return get(key.keyName);
	}
	
	
	synchronized public String get(String key){
		Object val = properties.get(key);
//		System.out.println("  #" + id + " KEY '" + key +"' ::" + val);
		if(val==null && parent!=null){
			return parent.get(key);
		}
		return val == null ? null : val.toString();
	}
	
	
	/**
	 * Prints only non-null values!
	 */
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		List<String> keys  = new ArrayList<String>();
		for(Key key : Key.values()){
			keys.add(key.keyName);
		}
		Collections.sort(keys);
		for(String key : keys){
			if (this.get(key)==null){
				continue;
			}
			sb	.append(key)
				.append("\t")
				.append(this.get(key))
				.append("\n")
			;
		}
		return sb.toString();
	}

	static Key getKeyFromString(String keyAsString){
		keyAsString = keyAsString.toLowerCase();
		for(Key key : Key.values()){
			if(key.keyName.equals(keyAsString)){
				return key;
			}
		}
		return null;
	}
	
	@Override
	public Iterator<Key> iterator(){
		List<Key> keys = new ArrayList<Registry.Key>();
		for(String key : properties.stringPropertyNames()){
			Key theKey = getKeyFromString(key);
			if(theKey!=null){
//			System.out.println(key);	
				keys.add(theKey);
			}
		}
		return keys.iterator();
	}
}
