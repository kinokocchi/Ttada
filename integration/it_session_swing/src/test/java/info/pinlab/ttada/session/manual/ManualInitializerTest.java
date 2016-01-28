package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.SessionFactory;


/**
 * 
 * Reads 'tastset.conf' from reseources
 * 
 * @author Gabor Pinter
 *
 */
public class ManualInitializerTest {


	
	private static TaskSet initTaskSet(){
		TaskSet tset  = new TaskSet();
		tset.add(new InfoTask("ManualInitializer Test"));
		return tset;
	}
	
	public static void main(String [] args) throws Exception{
		Registry conf = Registry.getDefaultInstance();
		conf.put(Key.LOCAL_AVAILABLE, false);
		conf.put(Key.REMOTE_AVAILABLE, false);
		
		
		new SessionFactory()
			.setConfig(conf)
			.setTaskSet(initTaskSet())
			.build().startSession();
	}
}
