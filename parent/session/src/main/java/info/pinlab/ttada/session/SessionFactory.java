package info.pinlab.ttada.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import info.pinlab.pinsound.PlayerDeviceFacotry;
import info.pinlab.pinsound.RecorderDeviceController;
import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.cache.disk.DiskCache;
import info.pinlab.ttada.cache.disk.DiskCache.DiskCacheBuilder;
import info.pinlab.ttada.cache.disk.DiskEnrollController;
import info.pinlab.ttada.cache.disk.LocalSaveHook;
import info.pinlab.ttada.core.cache.CacheManager;
import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.cache.MemCache;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.cache.RemoteCacheBuilder;
import info.pinlab.ttada.core.cache.SimpleCacheManagerImpl.SimpleCacheManagerBuilder;
import info.pinlab.ttada.core.control.EnrollController;
import info.pinlab.ttada.core.control.SessionController;
import info.pinlab.ttada.core.control.SessionControllerWithAudio;
import info.pinlab.ttada.core.model.HasAudio;
import info.pinlab.ttada.core.model.HasAudioRule;
import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializerFactory;
import info.pinlab.ttada.core.ser.WavAdapter;
import info.pinlab.ttada.core.view.EnrollViewFactory;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.TaskSetCsvParser.CsvErrorMonitorListener;
import info.pinlab.utils.FileStringTools;
import info.pinlab.utils.WindowsUtils;




/**
 * 
 * Handles configuration files, check storages for cache, finds sensible defaults... etc.
 * 
 * CLI.
 * 
 * Order of init:   <br>
 		initLog(); <br>
		initClassMaps(); <br>
		initConf(runtimeConf); <br>
		initJsonAdapter(); <br>
		initCachesAndEnrollController(); <br>
		initTaskSet(); <br>
		initGui(); <br>
		initAudio(); <br>
 * 
 *<p>
 * 
 * <u> Usage</u>
 * <pre>
 * new InitializerInstance()
 *     .setConfig(conf)
 *     .setTaskSet(tset)
 *     .build()  //-- returns a {@link SessionController} object
 *     .startSession();
 * </pre>
 * 
 * </p>
 * @author Gabor Pinter
 */
public class SessionFactory {
	public static Logger logger = Logger.getLogger(SessionFactory.class);
	
	private Registry effectiveConf = null;
	private Registry runtimeConf = null;
	
	private static CacheManager cm ;
	private static EnrollController diskEnroller = null;
	private static EnrollController restEnroller = null;
	private static TaskSet tset = null;
	private SessionControllerWithAudio session = null;

	
	private static SimpleJsonSerializerFactory localJsonSerializerFactory, remoteJsonSerializerFactory;
	private static SimpleJsonSerializer localJsonSerializer, remoteJsonSerializer;
	
	private static Map<Class<?>, Class<?>> interfaceMap; 
	private static Map<String, Class<?>> tag2ClazzMap; 
	
	private static RemoteCache remoteCache = null;

	//-- GUI
	private PlayerTopView topView = null;
	private EnrollViewFactory enrollViewFactory = null;

	//-- AUDIO
	private static PlayerDeviceFacotry audioPlayerDeviceFactory;
//	private static AudioPlayer audioPlayer;
	private static RecorderDeviceController recorderDevController;

	
	private boolean IS_RECORDING = true;

	

	
	public SessionFactory(){
		interfaceMap = new HashMap<Class<?>, Class<?>>();
		tag2ClazzMap = new HashMap<String, Class<?>>();
	}
	
	
	
	public SessionFactory setConfig(Registry conf){
		runtimeConf = conf;
		return this;
	}
	
	
	/**
	 * If taskSet is set through this method, no other task sets will be configured.
	 *  
	 * @param taskSet
	 */
	public SessionFactory setTaskSet(TaskSet taskSet){
		tset = taskSet;
		return this;
	}
	
	
	
	/**
	 * The following methods are called: <br> 
	 * {@link SessionFactory#initLog() } <br>
	 * {@link SessionFactory#initConfig(Registry) } <br>
	 * {@link SessionFactory#initGui() } <br>
	 * {@link SessionFactory#initJsonAdapter() } <br>
	 * 
	 * 
	 * 
	 * @return
	 */
	public SessionController build(){
		initLog();
		initConfig();
		
		try {
			initGui();
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
			
			//-- no gui...  
			effectiveConf.put(Registry.Key.PLAYER_HAS_GUI, false);
		}
		
		initJsonAdapter();
		initCachesAndEnrollController();
		initTaskSet();

		if(hasAudio()){
			initAudio();
		}
		
		
		initSession();
		
		return session;
		
	}
	
	
	public void startSession(){
//		initSession();
//		onBeforeStartSession();
	}
	
	
	private void initLog(){
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		logger = Logger.getLogger(SessionFactory.class);
	}

	
	
	
	
	public void initConfig(){
		logger.info("INIT properties");
		//--  #1 distribution default
		Registry defaultConf = Registry.getDefaultInstance();
		effectiveConf = defaultConf;

		//---------------------
		//--  #2 .jar's default properties
		//-- gets defaults from the property file bundled in jar
		InputStream is = null;
		String jarConfFile = null;

		//-- check where to find dist conf file in jar
		if(runtimeConf!=null){
			jarConfFile = runtimeConf.get(Key.DIST_PROPERTY_FILE);
			if(jarConfFile!=null){
				is = Registry.class.getResourceAsStream(jarConfFile);
				if(is==null){
					logger.warn("No distribution conf file found at '" + jarConfFile + "'");
				}
			}
		}
		//-- if not property file in jar
		if(is==null){
			jarConfFile = Key.DIST_PROPERTY_FILE.getDefaultValue();
			if(jarConfFile!=null){
				is = Registry.class.getResourceAsStream(jarConfFile);
				if(is==null){
					logger.warn("No distribution conf file found at '" + jarConfFile+ "'");
				}
			}
		}
		if(is!=null){
			try {
				effectiveConf.put(is);
			}catch (IOException ignore) {	}
		}

		//---------------------
		//--  #3 local default
		//-- reads form local machine's defaults
		String userDir = null;
		String userConfFile = null;
		//-- first find where to look for local file!
		if(runtimeConf!=null){
			userDir = runtimeConf.get(Key.USER_DIR);
			userConfFile = runtimeConf.get(Key.USER_CONF_FILE);
		}
		if(userDir==null){
			userDir = effectiveConf.get(Key.USER_DIR);
		}
		if(userConfFile==null){
			userConfFile = effectiveConf.get(Key.USER_CONF_FILE);
		}
		if(userDir==null){
			userDir = Key.USER_DIR.getDefaultValue();
		}
		if(userConfFile==null){
			userConfFile = Key.USER_CONF_FILE.getDefaultValue();
		}
		File confFile = new File(
				System.getProperty("user.home")+ FileStringTools.SEP 
				+ userDir + FileStringTools.SEP + userConfFile
				);
		if(confFile.exists() && confFile.isFile()){
			try {
				InputStream confIs = new FileInputStream(confFile);
				Registry localConf = Registry.load(confIs);
				effectiveConf.put(localConf);
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//---------------------localJsonAdapter
		// #4 command line
		if(runtimeConf!=null){
			effectiveConf.put(runtimeConf);
		}
	}

	
	
	public boolean hasAudio(){
		if (!effectiveConf.getBoolean(Key.ALLOW_AUDIO)){
			logger.info("Audio is not allowed in this session!");
			return false;
		}
		
		if (tset==null || tset.size()==0){
			effectiveConf.put(Key.HAS_AUDIO, false);
			return false;
		}

		for(Task task : tset){
			if (task instanceof HasAudio || task instanceof HasAudioRule){
				effectiveConf.put(Key.HAS_AUDIO, true);
				return true;
			}
			for(Display disp : task.getDisplays()){
				if(disp instanceof AudioDisplay){
					effectiveConf.put(Key.HAS_AUDIO, true);
					return true;
				}
			}
		}
		effectiveConf.put(Key.HAS_AUDIO, false);
		return false;
	}

	
	
	public void initGui() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		logger.info("INIT GUI");
		//-- Swing, or whatever --//
		
		Class<?> clazz = Class.forName(effectiveConf.get(Registry.Key.PLAYER_GUI_TOP_FQCN));
		topView = (PlayerTopView)clazz.newInstance();
		
		if(topView instanceof EnrollViewFactory){
			enrollViewFactory = (EnrollViewFactory) topView;
		}
	}

	//-- add here all the task types that are created! --//
	private void initJsonAdapter(){
		logger.info("INIT JsonAdapter");
		boolean isRefsInJson = FileStringTools.getBoolean(
				effectiveConf.get(Key.TASK_SET_JSON_USE_REFS)
				);
		
		localJsonSerializerFactory = new SimpleGsonSerializerFactory();
		remoteJsonSerializerFactory = new SimpleGsonSerializerFactory();

		remoteJsonSerializerFactory.registerTypeAdapter(WavClip.class, new WavAdapter());
		localJsonSerializerFactory.registerTypeAdapter(WavClip.class, new WavAdapter());
		
		localJsonSerializerFactory.setUseRefsInJson(isRefsInJson);
		remoteJsonSerializerFactory.setUseRefsInJson(false);  //-- don't use refernces! for remote cahce!
		
		
		localJsonSerializer = localJsonSerializerFactory.build();
		remoteJsonSerializer = remoteJsonSerializerFactory.build();
	}	
	
	/**
	 * Initializes local disk cache. 
	 * 
	 * 
	 * @return DiskCache instance, null if not local disk is available
	 */
	public DiskCache initDiskCache( ){
		//--------------------------//
		//-- Set local disk Path! --//
		String path = effectiveConf.get(Key.LOCAL_ABSOLUTE_PATH);

		if(!path.isEmpty()){
			//-- if it is not empty -> override everything else!
			logger.info("Found absolute path for disk store '" + path +"'");
		}else{ //-- no absolute path!
			//-- check if it has to be on desktop!
			if(effectiveConf.getBoolean(Key.LOCAL_DIR_ON_DESKTOP)){
				if(isWin()){
					path = WindowsUtils.getCurrentUserDesktopPath();
				}
				if(isLinux()){
					//-- check for windows!
					//TODO: make it work on Japanese!
					path = System.getProperty("user.home") +"/" + "Desktop";
				}
			}else{ //-- if not on desktop: relative to home folder
				path = System.getProperty("user.home");
			}

			String relativeDir = effectiveConf.get(Key.LOCAL_DIR);
			if(relativeDir.isEmpty()){
				path = effectiveConf.get(Key.LOCAL_DIR);
			}else{
				path = path + FileStringTools.SEP + relativeDir;  
			}
		}
		//-- Set local disk Path! --//
		//--------------------------//

		DiskCacheBuilder dcb = new DiskCacheBuilder();
		dcb
		.setDiskCacheRootPath(new File(path))
		.setClassInterfaceMap(interfaceMap)
		.setTag2ClassMap(tag2ClazzMap)
//		.isSaveRespTextOnTheFly(effectiveConf.getBoolean(Key.RESPONSE_SAVE_AS_TEXT))
//		.isSaveRespWavOnTheFly(effectiveConf.getBoolean(Key.RESPONSE_SAVE_AS_WAV))
		.setJsonAdapter(localJsonSerializer)
		;
		
		//-- make sure conf is correct true
		effectiveConf.put(Key.LOCAL_AVAILABLE, true);
		DiskCache diskCache = dcb.build();
		
		return diskCache; 
	}
	
	private RemoteCache initRemoteCache( ){
		effectiveConf.put(Key.REMOTE_AVAILABLE, false); //-- make unavailable until not successful initialized 
		remoteCache = null;
		final String cacheBuilderClazzFQCN = effectiveConf.get(Key.REMOTE_CACHE_BUILDER_FQCN);
		if (cacheBuilderClazzFQCN==null){
			logger.error("Remote cache FQCN is not defined!");
			return null;
		}
		
		RemoteCacheBuilder cacheBuilder = null;
		try {
			logger.info("Creating Remote cache builder '" + cacheBuilderClazzFQCN + "'");
			Class<?> clazz = Class.forName(cacheBuilderClazzFQCN);
			cacheBuilder = (RemoteCacheBuilder)clazz.newInstance();
		} catch (ClassNotFoundException e){
			logger.error("No such class as '"+ cacheBuilderClazzFQCN +"'");
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			logger.error("Can't instantiate '"+ cacheBuilderClazzFQCN +"'");
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			logger.error("Can't access '"+ cacheBuilderClazzFQCN +"'");
			e.printStackTrace();
			return null;
		}


		cacheBuilder.setScheme(effectiveConf.get(Key.REMOTE_PROTOCOL));
		cacheBuilder.setHost(effectiveConf.get(Key.REMOTE_HOST));
		final int port = Integer.parseInt(effectiveConf.get(Key.REMOTE_PORT));
		cacheBuilder.setPort(port);
		cacheBuilder.setPingPath(effectiveConf.get(Key.REMOTE_PING_PATH));
		cacheBuilder.setAppPingPath(effectiveConf.get(Key.REMOTE_APPPING_PATH));
		
		cacheBuilder.setLoginPath(effectiveConf.get(Key.REMOTE_LOGIN_PATH));
		cacheBuilder.setLoginId(effectiveConf.get(Key.REMOTE_LOGIN_ID));
		cacheBuilder.setLoginPwd(effectiveConf.get(Key.REMOTE_LOGIN_PWD));
		
		cacheBuilder.setRestRoot(effectiveConf.get(Key.REMOTE_RESOURCE_ROOT));
		cacheBuilder.setSerializer(remoteJsonSerializer);
		remoteCache = cacheBuilder.build();

		
		final String fullUrl = 
				effectiveConf.get(Key.REMOTE_PROTOCOL) + "//:"  
						+ effectiveConf.get(Key.REMOTE_HOST) + ":" 
						+ effectiveConf.get(Key.REMOTE_PORT) + "/"
						+ effectiveConf.get(Key.REMOTE_LOGIN_PATH); 
//		logger.info("Attempting to set up remote connection " + fullUrl);
		//-- attempt to login --//
		boolean status =  remoteCache.connect();
		if(status){//!remoteCache.isConnected()
			logger.info("Successfully connected to remote server '" + remoteCache.getRemoteUri() +"'");
			effectiveConf.put(Key.REMOTE_AVAILABLE, true);
			return remoteCache;
		}else{
			logger.error("Couldn't connect to '" + fullUrl + "'");
			return null;
		}
	}
	
	

	private void initCachesAndEnrollController(){
		logger.info("INIT caches & enroll");
		SimpleCacheManagerBuilder scmb = new SimpleCacheManagerBuilder();
		//-- Order is important --//
		//-- Memory Cache is always available! --//
		scmb.addCache(MemCache.getInstance(), true);

		
		//-- Disk cache : on request --//
		if(effectiveConf.getBoolean(Key.LOCAL_AVAILABLE)){
			//-- no caching without serializer!
			
//			logger.info("Creating Local Disk cache for responses");
			DiskCache diskCache = initDiskCache();
			if(diskCache!=null){
				scmb.addCache(diskCache , /* default */ true); //-- should be used if available!
				diskEnroller = new DiskEnrollController();
				diskEnroller.setCache(diskCache);
				
				//-- add hooks if present
				for(String hookFqcn : effectiveConf.getList(Key.LOCAL_SAVE_HOOKS)){
					//-- init class
					logger.info("Instantiating hook '" + hookFqcn +"'");
					try{
						final Class<?> clazz = Class.forName(hookFqcn);
						Constructor<?> hookConstructor = clazz.getConstructor(File.class);
						LocalSaveHook hook = (LocalSaveHook) hookConstructor.newInstance(diskCache.getRootDir());
						((DiskEnrollController) diskEnroller).addSaveHook(hook);
					} catch (ClassNotFoundException e) {
						logger.error("Failed to find class '" + hookFqcn +"'");
						e.printStackTrace();
					} catch (InstantiationException e) {
						logger.error("Failed to instantiate class '" + hookFqcn +"'");
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch(NoSuchMethodException e){
						e.printStackTrace();
						logger.error("Class  '" + hookFqcn +"' has no appropriate constructor ( public XY(File absPath)");
					} catch(InvocationTargetException e){
						e.printStackTrace();
						logger.error("Error while trying to instantiate Class '" + hookFqcn +"'");
					}
				}
			}
		}else{
			logger.info("Local disk is disabled by choice. Not creating Local Disk cache for responses");
		}
		
		
		//-- remote cache : on request --//
		if(effectiveConf.getBoolean(Key.REMOTE_AVAILABLE)){
			
			if(remoteCache ==null){
				remoteCache = initRemoteCache();
			}
			
			if(remoteCache==null){
				logger.error("Couldn't initialize remote cache! Skipping. Setting " + Key.REMOTE_AVAILABLE.keyName + " as FALSE");
				effectiveConf.put(Key.REMOTE_AVAILABLE, false);
			}else{
				boolean hasEnroller = true;
				logger.info("Materializing Remote enroller '" + effectiveConf.get(Key.REMOTE_ENROLLER_FQCN) + "'");
				try {
					final Class<?> clazz = Class.forName(effectiveConf.get(Key.REMOTE_ENROLLER_FQCN));
					restEnroller = (EnrollController) clazz.newInstance();
					restEnroller.setCache(remoteCache);
				} catch (ClassNotFoundException e) {
					hasEnroller = false;
					e.printStackTrace();
				} catch (InstantiationException e) {
					hasEnroller = false;
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					hasEnroller = false;
					e.printStackTrace();
				}

				if(hasEnroller){
//					logger.info("Set remote cache as default cache!");
					scmb.addCache(remoteCache, false /* nondefault */);
				}else{
					logger.error("Failed to setup remote cache because Remote Enroller couldn't be set up");
				}
				
//				//-- remote available
//				final String taskSetRef = effectiveConf.get(Key.TASK_SET_REF);
//				if(taskSetRef == null){
//					logger.error("TaskSet reference ID is NULL! Can't load task set!");
//				}else{
//					int taskSetRefAsInt = Integer.parseInt(taskSetRef);
//					taskSet = remoteCache.get(new Pointer(taskSetRefAsInt), TaskSet.class);
//				}
			} //-- remote cache
		}
		
		// -- other caches ? --//
		cm = scmb.build();

		CachedValue.setCacheManager(cm);
		
		localJsonSerializerFactory.setCacheManager(cm);
		remoteJsonSerializerFactory.setCacheManager(cm);
		
//		jsonFactory.setCacheManager(cm);
//		localJsonAdapter.setCacheManager(cm);
//		remoteJsonAdapter.setCacheManager(cm);
		
		
//		if(restEnroller!=null){
//			restEnroller.setResponseSet(new ResponseSet());
//		}
//		if(diskEnroller!=null){
//			diskEnroller.setResponseSet(new ResponseSet());
//		}
	}
	
	

	/*
	 * <p> Load task set - from wherever </p>
	 * 
	 * 1. remote task set <br>
	 *   + if remote available <br>
	 *   + if remote ref is given <br>
	 * 2. local .csv file <br>
	 * 3. bundled .conf file <br>
	 * 4. bundled .json file <br>
	 * 
	 */
	private void initTaskSet(){
		logger.info("INIT TaskSet");
		if(tset!=null){
			logger.info("TaskSet is already set, so NOT initializing here");
		}else{
			String tsetResourceType = effectiveConf.get(Key.TASK_SET_FILE_TYPE);
			String resFileName = null;
			if ("csv".equals(tsetResourceType)){
				resFileName = effectiveConf.get(Key.TASK_SET_CSV);
				logger.info("Looking for TestSet '" + resFileName  +  "' as '" + tsetResourceType + "' resource" );
				tset = readTaskSetFromCsv(resFileName);
			}else{
				if ("json".equals(tsetResourceType)){
					resFileName = effectiveConf.get(Key.TASK_SET_JSON);
					logger.info("Looking for TestSet '" + resFileName  +  "' as '" + tsetResourceType + "' resource" );
					tset = readTaskSetFromJson(resFileName);
				}else{
					if ("conf".equals(tsetResourceType)){
						resFileName = effectiveConf.get(Key.TASK_SET_CONF);
						logger.info("Looking for TestSet '" + resFileName  +  "' as '" + tsetResourceType + "' resource" );
						tset = readTaskSetFromConf(resFileName);
					}else{
						logger.info("Not local TestSet type '" + resFileName  +  "' as '" + tsetResourceType + "' resource" );
					}
				}
			}
		}
			
//			if(Key.SOURCE_ENUM_REMOTE.equals(taskSetSrc)){
//				if(effectiveConf.getBoolean(Key.REMOTE_AVAILABLE)){ //-- if remote available
//					if(taskSetRef.isEmpty()){
//						logger.error("TaskSet reference ID is empty! Can't load task set from remote server!");
//					}else{
//						int taskSetRefAsInt = Integer.parseInt(taskSetRef);
//						//-- try to load from local first
//						try{
//							logger.info("Trying to fetch TaskSet from local cache before checking remote!");
//							remoteTset = loadTaskSetFromLocalCache(taskSetRefAsInt);
//						}catch(Exception ignore){
//							remoteTset = null;
//						}
//						if(remoteTset!=null){
//							logger.info("Managed to fetch TaskSet from local! ");
//						}else{
//							logger.info("Couldn't fetch TaskSet from local. Trying to fetch from remote cache.");
//							remoteTset = loadTaskSetFromRemoteCache(taskSetRefAsInt);
//						}
//					}
//				}
//			}
			
		if(tset == null){
			final String taskSetRef = effectiveConf.get(Key.TASK_SET_REF);
			if(!taskSetRef.isEmpty()){
				final int taskSetHashCode = Integer.parseInt(taskSetRef); 
				tset = cm.get(new Pointer(taskSetHashCode), TaskSet.class);
				if(tset==null){
					logger.error("Couldn't build TaskSet from task set reference!");
					throw new IllegalStateException("Couldn't build TaskSet from remote cache!");
				}
			}
		}else{
			logger.info("Not checking for TaskSet as ut is already loaded");
		}

		if(tset!=null && tset.getBrief().isEmpty()){//-- set brief if it is not set!
			tset.setBrief(effectiveConf.get(Key.TASK_SET_BRIEF));
		}
		
		if(effectiveConf.getBoolean(Key.TASK_SET_SHUFFLE_TASK)){
			tset.shuffle();
		}
		if(effectiveConf.getBoolean(Key.TASK_SET_SHUFFLE_CHOICE)){
			MultichoiceTask.shuffleChoices(tset);
		}
	}
	
	
	private TaskSet readTaskSetFromConf(String confFileName){
		logger.info("Reading bundled TaskSet .conf from '" + confFileName + "'");
		InputStream is = findInputStream(confFileName);
		if(is == null){
			logger.info("Couldn't find TaskSet .conf in jar '" + confFileName + "'");
			return null;
		}
		String confAsString = null;
		try{
			confAsString = FileStringTools.getStreamAsString(is);
			is.close();
			if(confAsString != null){
				TaskSet confTaskSet = ConfigFileParser.parseText(confAsString);
				logger.info("Task set size " + confTaskSet.size());
				return confTaskSet;
			}
		}catch(IOException e){
			logger.info("Couldn't read TaskSet .conf from file '" + confFileName + "'");
			e.printStackTrace();
		}
		return null;
	}
	
	
	private TaskSet readTaskSetFromCsv(String csvFile){
		logger.info("Reading bundled external .csv from '" + csvFile + "'");
		InputStream is = findInputStream(csvFile);
		if(is==null){
			logger.info("Couldn't find TaskSet .csv '" + csvFile + "'");
			return null;
		}
		String csvAsString = null;
		try {
			csvAsString = FileStringTools.getStreamAsString(is);
			is.close();
			logger.debug("Json task set "+ csvAsString.length() + " chars long");
			if(csvAsString!=null){
				TaskSetCsvParser parser = new TaskSetCsvParser();
				parser.setCsvFileAsString(csvAsString);
				File csvFileFile = new File(csvFile);
				if (csvFileFile.exists()){
					parser.setCsvFileDir(csvFileFile.getAbsoluteFile().getParent());
				}
				
				
				String loaderClassFqcn = this.effectiveConf.get(Key.TASK_SET_RESOURCELOADER_CLASS);
				if(loaderClassFqcn!=null && !loaderClassFqcn.isEmpty()){
					try {
						Class<?> loaderClazz = Class.forName(loaderClassFqcn);
						parser.setClassForResourceLoading(loaderClazz);
					} catch (ClassNotFoundException ignore) {
						logger.error("Couldn't init '" + loaderClassFqcn + "' for resource loading in .csv");
					}
				}
				parser.setResourceDir(effectiveConf.get(Key.TASK_SET_MEDIA_DIR));
				CsvErrorMonitorListener errListener = new CsvErrorMonitorListener() {
					@Override
					public void illegalLineFound(int lineN, IllegalArgumentException exp) {
						logger.error("Error in .csv file " + exp.getMessage());
					}
				};
				parser.setErrorMonitor(errListener);
				
				TaskSet csvTaskSet = null;
				csvTaskSet = parser.parse();
				logger.info("Task set size " + csvTaskSet.size());
				return csvTaskSet;
			}
		} catch (IOException e) {
			logger.info("Couldn't read TaskSet .csv from file '" + csvFile + "'");
			e.printStackTrace();
		}
		return null;
	}
	
	
	private TaskSet readTaskSetFromJson(String jsonFileName){
		logger.info("Reading TaskSet .json from '" + jsonFileName + "'");
		InputStream is = findInputStream(jsonFileName);
		if(is==null){
			logger.info("Couldn't find TaskSet .json in jar '" + jsonFileName + "'");
			return null;
		}
		
		String jsonAsString = null;
		try {
			jsonAsString = FileStringTools.getStreamAsString(is);
			is.close();
			logger.debug("Json task set "+ jsonAsString.length() + " chars long");
			if(jsonAsString!=null){
				final SimpleJsonSerializer gson = new SimpleGsonSerializerFactory().build();
				TaskSet jsonTaskSet = gson.fromJson(jsonAsString, TaskSet.class);
				logger.info("Task set size " + jsonTaskSet.size());
				return jsonTaskSet;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Couldn't read TaskSet .json from file '" + jsonFileName + "'");
		return null;
	}
	
	
	private InputStream findInputStream(String resource){
		logger.info("Looking for resource'" + resource + "'");
		InputStream is = null;
		
		String loaderClassFqcn = this.effectiveConf.get(Key.TASK_SET_RESOURCELOADER_CLASS);
		if(loaderClassFqcn!=null && !loaderClassFqcn.isEmpty()){
			logger.info("Instantiating class '" + loaderClassFqcn + "' to load resource '" + resource +"'");
			try {
				Class<?> loaderClazz = Class.forName(loaderClassFqcn);
				logger.info("Class '" + loaderClassFqcn + "' instantiated successfully");
				is = loaderClazz.getResourceAsStream(resource);
				if(is==null){
					is = loaderClazz.getClassLoader().getResourceAsStream(resource);
				}
				if(is==null){
					logger.warn("Couldn't load resource '" + resource + "' from clazz '" + loaderClassFqcn +"'");
				}
//				Object obj = loaderClazz.newInstance();
//				is = obj.getClass().getResourceAsStream(resource);
			} catch (ClassNotFoundException ignore) {	
				logger.warn("Couldn't load resource '" + resource + "' from clazz '" + loaderClassFqcn +"' (no class found)");
			}
		}

		if(is!=null){
			return is;
		}
		
		is = SessionFactory.class.getResourceAsStream(resource);
		if(is==null){
			is = SessionFactory.class.getClassLoader().getResourceAsStream(resource);
		}
		if(is!=null){
			logger.info("Found bundled resource '" + resource + "'");
			return is;
		}else{	
			logger.warn("No boundled resoures named  '" + resource + "'");
		}

		//-- check file system
		
		
		File resourceFile = new File(resource);
		if(!resourceFile.isFile()){ //-- no file exist here!
			resourceFile = new File(new File("."), resource);
		}
		
		try {
			is = new FileInputStream(resourceFile);
			if (is != null){
				logger.info("Found external resource '" + resource + "'");
			}
		} catch (FileNotFoundException e) {
			logger.warn("No external resoures named  '" + resourceFile.getAbsolutePath() + "'");
		}
		return is;
	}
	
	
	/**
	 * Runtime loads audio library
	 */
	public void initAudio() throws IllegalStateException{
		String playerDevLoaderClassName = effectiveConf.get(Key.AUDIO_PLAY_DEV_LOADER_FQCN);
		Object playerObj = getClassForName(playerDevLoaderClassName);
		if(playerObj!=null){
			if(playerObj instanceof PlayerDeviceFacotry){
				audioPlayerDeviceFactory = (PlayerDeviceFacotry) playerObj;
				logger.info("Loading audio player device controller '" + playerDevLoaderClassName +"'");
//				player = new AudioPlayer();
//				player.setPlayerDevice(audioPlayerDeviceFacotry);
			}else{
				logger.error("Not a player device controller  '" + playerObj.getClass().getName() +"'");
			}
		}else{
			logger.error("Not initializing audio player '" + playerDevLoaderClassName +"' becuase it was not defined!");
		}

		if(IS_RECORDING){
			String recorderDevLoaderClassName = effectiveConf.get(Key.AUDIO_REC_DEV_LOADER_FQCN);
			Object recObj =  getClassForName(recorderDevLoaderClassName);
			if(recObj != null){
				if(recObj instanceof RecorderDeviceController){
					recorderDevController = (RecorderDeviceController)recObj;
					logger.info("Loaded audio recorder device controller '" + recorderDevLoaderClassName +"'");
					recorderDevController.setRecorderDevice(effectiveConf.getInt(Key.REC_DEVICE_ID));
					//-- init things --//new AudioRecorder();
//					recorder.setRecorderDevice(recorderDevController);
				}else{
					logger.error("Not a player device controller  '" + recObj.getClass().getName() +"'");
				}
			}else{
				logger.error("Not initializing audio recorder '" + recorderDevLoaderClassName +"' becuase it was not defined!");
			}
		}else{
			recorderDevController = null;
//			Initializer.recorder = null;
		}
	}

	
	
	/**
	 * Pieces are put together here
	 */
	private void initSession(){
		session = new SessionImpl(effectiveConf);
		

		//-- init GUI --//
		if(effectiveConf.getBoolean(Key.PLAYER_HAS_GUI)){
			session.setTopView(topView);
			topView.setSessionController(session);
			session.setEnrollViewFactory(enrollViewFactory); //-- in order to create views  
		}
		
		//TODO: enroll view and control setters should be separated
		if(diskEnroller!=null){
			session.addEnrollController(diskEnroller);
//			session.addResponseSet(diskEnroller.getResponseSet());
		}
		if(restEnroller!=null){
			session.addEnrollController(restEnroller);
//			session.addResponseSet(restEnroller.getResponseSet());
		}

		session.setTaskSet(tset);
		
		
		//-- set cache manager --//
//		session.setCacheManager(Initializer.cm);

		session.setUserId(effectiveConf.get(Key.USER));
		
//		//-- set audio devices --//
//		if(audioPlayer != null){
//			session.setAudioPlayer(audioPlayer);
//		}
		if(audioPlayerDeviceFactory!=null){
			session.setAudioPlayerDeviceFactory(audioPlayerDeviceFactory);
		}
		if(recorderDevController != null){
			session.setAudioRecorderDeviceController(recorderDevController);
		}
	}
	
	
	
	private static Object getClassForName(String clazzForName) throws IllegalStateException{
		String err = null;
		if(clazzForName==null){
			err = "No class name is given!";
			throw new IllegalStateException(err);
		}else{
			Class<?> clazz;
			try {
				clazz = Class.forName(clazzForName);
				return clazz.newInstance();
			} catch (ClassNotFoundException e) {
				err = "Can't find class  '" + clazzForName +"'";
				e.printStackTrace();
				throw new IllegalStateException(err);
			} catch (InstantiationException e) {
				err = "Can't init class '" + clazzForName +"'";
				e.printStackTrace();
				throw new IllegalStateException(err);
			} catch (IllegalAccessException e) {
				err = "Can't access class '" + clazzForName +"'";
				e.printStackTrace();
				throw new IllegalStateException(err);
			}
		}
	}
	
	
	private static boolean isWin(){
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}
	private static boolean isLinux(){
		return System.getProperty("os.name").toLowerCase().startsWith("lin");
	}

	
	public static void main(String[] args) {
		
	}

}
