package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.SurveyTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;
import info.pinlab.ttada.view.swing.PlayerTopPanel;

public class ManualSurveyTaskPanelTest {

	public static void main(String[] args) {
		PlayerTopPanel.setNimbusLF();
		
		SurveyTask task = new SurveyTask();
		task.addDisplay("<html><h1>We need info!</h1></html>");
		task.addUsrIdEntry("1 ID")
			.addTextEntry("2 Age", false, "age")
			.addTextEntry("3 Dialect", false, "dialect");
		
		
		TaskSet tset = new TaskSet();
		tset.add(new InfoTask("Hello!"));
		tset.add(task);
		tset.add(new InfoTask("Thanks!"));
		
		
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		
		cli.getSessionFactory().setTaskSet(tset);
		cli.run(args);
		
		
//		PlayerTopView frame = new PlayerTopPanel();
//		frame.setLabel("Testing user setter");
//		frame.setTaskView(task);
//		frame.startGui();
	}

}
