package info.pinlab.ttada.core.view;

public interface Notification {
	
	public void setLogMsg(String msg);
	
	public void showMessage(String msg);
	public void showWarning(String warn);
	public void showError(String warn);
	
}
