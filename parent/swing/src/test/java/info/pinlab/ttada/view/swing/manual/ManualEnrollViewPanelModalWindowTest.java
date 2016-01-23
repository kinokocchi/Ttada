package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.cache.disk.DiskCache;
import info.pinlab.ttada.cache.disk.DiskCache.DiskCacheBuilder;
import info.pinlab.ttada.cache.disk.DiskEnrollController;
import info.pinlab.ttada.core.control.EnrollController;
import info.pinlab.ttada.core.control.SessionController;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
import info.pinlab.ttada.session.SessionImpl;
import info.pinlab.ttada.view.swing.PlayerTopPanel;

import org.apache.log4j.BasicConfigurator;


public class ManualEnrollViewPanelModalWindowTest {


	private static TaskSet getDummyTasks(){
		TaskSet tset = new TaskSet();
		tset.add(new InfoTask("Dummy tasks for Modal Window test"));

		for(int i = 0; i < 5 ; i++){
			tset.add(new InfoTask("Dummy " + i));
		}
		return tset;
	}


	public static void main(String [] args ){
		BasicConfigurator.configure();

		SessionController session = new SessionImpl();
		session.setTaskSet(getDummyTasks());

		PlayerTopPanel.setNimbusLF();
		PlayerTopPanel top = new PlayerTopPanel();
		session.setEnrollViewFactory(top);
		

		SimpleJsonSerializer serializer = new SimpleGsonSerializerFactory().build();
		//-- set enroller
		EnrollController diskEnroller = new DiskEnrollController();
		DiskCache cache = new DiskCacheBuilder().setJsonAdapter(serializer).build();
		diskEnroller.setCache(cache);
//		EnrollController fallbackEnroller = new DiskEnrollController();

		
		session.addEnrollController(diskEnroller);
//		session.addResponseSet(diskEnroller.getResponseSet());


		//-- GUI
		top.setSessionController(session);

		
		top.setEnrollViewVisible(diskEnroller.getEnrollView(), true);
		session.startSession();
		
		System.out.println("END SESSION");

	}
}
