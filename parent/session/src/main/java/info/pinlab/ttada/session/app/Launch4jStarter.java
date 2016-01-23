package info.pinlab.ttada.session.app;

import info.pinlab.ttada.session.Registry;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


/**
 * 
 * Entry point for Launch4j applications. It reads 
 * 
 * @author Gabor Pinter
 *
 */
public class Launch4jStarter {

	/**
	 * Launcher for log
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		String exeDir = System.getProperty("launch4j.exedir");
		
//		System.out.println(exeDir);
		
		if( exeDir==null || exeDir.isEmpty()){
			exeDir = System.getProperty("user.dir");
		}

		List<String> argsAsList = new ArrayList<String>();
		argsAsList.add(Registry.Key.TASK_SET_MEDIA_DIR.getKeyName() + "=" + exeDir +"/etc/");
		argsAsList.add(Registry.Key.TASK_SET_FILE_TYPE.getKeyName() + "=" + "csv");
		argsAsList.add(Registry.Key.TASK_SET_CSV.getKeyName() + "=" + exeDir + System.getProperty("file.separator") + "/etc/" + "sample-taskset.csv");
		
		
		Properties props = System.getProperties();
		Enumeration<?> propEnum = props.propertyNames();
		while(propEnum.hasMoreElements()){
			String key = (String)propEnum.nextElement();
			if(key.startsWith("info.pinlab.")){
				argsAsList.add(key +"=" + props.getProperty(key));
			}
		}
		
		//-- adding cmd line arguments
		for(String arg : args){
			argsAsList.add(arg);
		}
		
		String [] argsAugmented = argsAsList.toArray(new String[argsAsList.size()]);
		
		for(String arg : argsAugmented){
			System.out.println(arg);
		}
		CLI.main(argsAugmented);
	}
}
