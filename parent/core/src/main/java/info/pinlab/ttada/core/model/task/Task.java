package info.pinlab.ttada.core.model.task;

import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.rule.StepRule;

import java.util.List;

public interface Task extends ExtendedResource<Task>{
	public Task addDisplay(Display disp);
	public Task addDisplay(String text); //-- shortcut for text display: very common
	public List<Display> getDisplays();
	
	public Task setStepRule(StepRule rule);
	public StepRule getStepRule();
	
	public boolean isResponsible();
	public void isResponsible(boolean b);
	
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object other);
	
	public Task setBrief(String brief);
	public String getBrief();
}
