package info.pinlab.ttada.session.manual.steptest;

import info.pinlab.ttada.core.model.EntryTask;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualStepTestWithShortcut {

	public static void main(String[] args) {
		
		TaskSet tset = new TaskSet();
		tset.add(new EntryTask());
		tset.add(new InfoTask("1 after entry"));
		tset.add(new InfoTask("2 after entry"));
		
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, false);
		
		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
	}

}
