package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualCliTestWithTset {

	public static void main(String[] args) {
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		
		TaskSet tset = new TaskSet();
		tset.add(new InfoTask("Runtime task added!"));
		
		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
	}
}