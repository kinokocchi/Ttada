package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.core.model.task.EntryTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.view.swing.TopPanel;

public class ManualEntryTaskPanelTest {

	public static void main(String[] args) {
		TopPanel.setNimbusLF();
		
		Task task1 = new EntryTask();
		task1.addDisplay("Testing entry task task");
		
		
		PlayerTopView frame = new TopPanel();
		frame.setLabel("Entry Task");
		frame.setTaskView(task1);
		frame.startGui();
	}

}
