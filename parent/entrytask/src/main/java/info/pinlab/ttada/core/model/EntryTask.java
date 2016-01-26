package info.pinlab.ttada.core.model;

import info.pinlab.ttada.core.model.task.AbstractTask;

public class EntryTask extends AbstractTask{

	public String prompt = "Type here:";
	public String btnLabel = "Ok";
	
	
	
	public void setPrompt(String s){
		prompt = s;
	}
	
	
	public void setButtonLabel(String label){
		btnLabel = label;
	}
	
	
	
}
