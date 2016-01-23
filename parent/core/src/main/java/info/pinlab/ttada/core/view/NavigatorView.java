package info.pinlab.ttada.core.view;

import info.pinlab.ttada.core.control.StepReqListener;

public interface NavigatorView {
	public void setStepController(StepReqListener controller);
	public void setLabel(String lab);
}
