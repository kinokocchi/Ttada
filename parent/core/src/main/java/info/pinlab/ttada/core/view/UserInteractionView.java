package info.pinlab.ttada.core.view;

import java.io.File;

public interface UserInteractionView {
	public void showWarning(String title, String msg);
	public void showInfo(String title, String msg);
	public File showDirChooser(String title);

}
