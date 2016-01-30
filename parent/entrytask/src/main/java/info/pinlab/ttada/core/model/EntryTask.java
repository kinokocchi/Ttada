package info.pinlab.ttada.core.model;

import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.AbstractTask;

public class EntryTask extends AbstractTask{

	public String prompt = "Type here:";
	public String btnLabel = "Ok";
	
	private static final StepRule defaultStepRule ; 
	
	static{
		defaultStepRule = new StepRuleBuilder()
				.setNextByResp(true) //-- next if enter pushed
				.build();
	}
	
	
	public EntryTask(){
		super.setStepRule(defaultStepRule);
		
	}
	
	
	public void setPrompt(String s){
		prompt = s;
	}
	
	
	public void setButtonLabel(String label){
		btnLabel = label;
	}
	
	
	
}
