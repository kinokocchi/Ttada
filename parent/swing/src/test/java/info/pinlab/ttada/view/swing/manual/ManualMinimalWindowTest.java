package info.pinlab.ttada.view.swing.manual;

import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.view.swing.PlayerTopPanel;

import org.apache.log4j.BasicConfigurator;


/**
 * Simplest GUI window example. Does nothing. No callbacks. It has to be stopped manually.
 * 
 * @author Gabor Pinter
 *
 */
public class ManualMinimalWindowTest {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		PlayerTopPanel panel = new PlayerTopPanel();
		panel.setLabel("Minimal test");
		
		Task task = new InfoTask("The simplest demo!"); 
		panel.setTaskView(task);
		panel.startGui();
	}

}
