package info.pinlab.ttada.view.swing;

import javax.swing.JComponent;

import info.pinlab.ttada.core.view.TaskView;


public interface TaskViewPanel extends HasPanel, TaskView{
	
	public void setDefaultFocus(JComponent comp);
	

}
