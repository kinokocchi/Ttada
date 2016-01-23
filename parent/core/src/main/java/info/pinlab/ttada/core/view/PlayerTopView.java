package info.pinlab.ttada.core.view;

import info.pinlab.ttada.core.control.SessionController;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.task.Task;

import java.util.List;

public interface PlayerTopView {
	public void setLabel(String label);
	public void setSessionController(SessionController session);
	/**
	 * Start gui on separate thread and return immediately! 
	 */
	public void startGui();
	
	//-- TaskView should invoke onVisible for controller!
	public TaskView setTaskView(Task task);
	public void setTaskView(TaskView view);
	public void setResponse(Response resp);
	
	public NavigatorView getNaviView();
	
	public void dispose();
	
	
	
	
	
	public boolean showCloseConfirmDialog();
//	public boolean showProcessingRespDialog(int n);
	public void showWarning(String title, String msg);
	public void showInfo(String title, String msg);

	/**
	 * Returns index of selected device name
	 * 
	 * @param deviceNames
	 * @return
	 */
	public int showAudioDevSelector(List<String> deviceNames);
	
	public void setTaskWindowVisible(boolean b);
	
	
	public void setEnrollViewVisible(EnrollView view, boolean isVisible);
	public boolean isEnrollViewVisible();
	
	
//	public void disposeEnrollView(final EnrollView view);

}
