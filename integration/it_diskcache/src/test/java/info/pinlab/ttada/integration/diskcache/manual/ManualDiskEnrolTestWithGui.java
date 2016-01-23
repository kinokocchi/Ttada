package info.pinlab.ttada.integration.diskcache.manual;

import org.apache.log4j.BasicConfigurator;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualDiskEnrolTestWithGui {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		
		
		TaskSet tset = new TaskSet();

		tset.add(	new InfoTask("1Hello!")				);
		tset.add(	new InfoTask("2Bello!")				);
		tset.add(	new InfoTask("3Hello!")				);
		tset.add(	new InfoTask("4Bello!")				);
		tset.add(	new InfoTask("5Hello!")				);
		tset.add(	new InfoTask("6Bello!")				);
		
		cli.getSessionFactory().setTaskSet(tset);
		
		cli.run(args);
	}
}
