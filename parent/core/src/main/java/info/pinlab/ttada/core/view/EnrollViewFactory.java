package info.pinlab.ttada.core.view;

import info.pinlab.ttada.core.control.EnrollReqListener;

public interface EnrollViewFactory {
	public EnrollView buildEnrollView();
	public EnrollView buildEnrollView(EnrollReqListener enrollListener);
}
