package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.InstructionTextDisplay;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.view.swing.TopPanel;

public class ManualMultichoiceTaskPanelTest {

	
	public static void main(String [] args) throws Exception{
		TopPanel.setNimbusLF();
		
		Task task = new MultichoiceTask().addChoice("A").addChoice("B").addChoice("C");
		task.addDisplay(new InstructionTextDisplay("Instruction goes top"));
		task.addDisplay("Testing multichoice task");
		
		PlayerTopView frame = new TopPanel();
		frame.setLabel("Testing multichoice");
		frame.setTaskView(task);
		frame.startGui();
	}
}

