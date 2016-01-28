package info.pinlab.ttada.view.swing.manual;

import org.apache.log4j.BasicConfigurator;

import info.pinlab.ttada.core.model.task.EntryTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.view.swing.PlayerTopPanel;

public class ManualEntryTaskPanelTest {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		PlayerTopPanel.setNimbusLF();
		
		Task task1 = new EntryTask();
		task1.addDisplay("Testing entry task task");
		
		
		PlayerTopView frame = new PlayerTopPanel();
		frame.setLabel("Entry Task");
		frame.setTaskView(task1);
		frame.startGui();
	}

}
