package info.pinlab.ttada.core.model.task;


import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.utils.HashCodeUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTask implements Task {

	private List<CachedValue<Display>> displays = null;//null avoids serialization! vs old: = new ArrayList<Display>();
	private CachedValue<StepRule> steprule = null;
	private String brief;
	
	transient boolean isResponsible = true; //-- no serialization
	
	@Override
	public boolean isResponsible(){
		return isResponsible;
	}
	
	@Override
	public void isResponsible(boolean b){
		isResponsible=b;
	}
	
	
	@Override
	public Task setBrief(String brief){
		this.brief = brief;
		return this;
	}
	@Override
	public String getBrief(){
		return this.brief;
	}
	
	public AbstractTask(){
		displays = new ArrayList<CachedValue<Display>>();
		StepRule steprule_ = (new StepRule.StepRuleBuilder()).build(); //-- default step rule
		steprule = CachedValue.getCacheWrapperForValue(steprule_, StepRule.class);
	}

	
	@Override
	public Task addDisplay(String text){
		addDisplay(new TextDisplay(text));
		return this;
	}
	
	@Override
	public Task addDisplay(Display disp){
		if(disp==null)
			throw new IllegalArgumentException("addDisplay() : Arg can't be null!");
		
		displays.add(CachedValue.getCacheWrapperForValue(disp, Display.class));
//		displays.add(CacheFactory.getChacheWrapperForValue(Display.class, disp));
//		hash = 0;
		return this;
	}
	@Override
	public List<Display> getDisplays(){
		List<Display> disps = new ArrayList<Display>();
		for(CachedValue<Display> d : displays){
			disps.add(d.getValue());
		}
		return disps;
	}

	@Override
	public Task setStepRule(StepRule rule){
		steprule = CachedValue.getCacheWrapperForValue(rule, StepRule.class);
//		steprule = rule;
//		hash = 0;
		return this;
	}
	
	@Override	
	public StepRule getStepRule(){
		return steprule.getValue();
	}
	
	

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof AbstractTask))
			return false;
		
		AbstractTask other = (AbstractTask)obj;

		int sz = this.displays.size();
		if(sz != other.displays.size())
			return false;
		
		if(!this.steprule.equals(other.steprule))
			return false;
		
		for(int i = 0 ; i < sz ; i++){
			Display thisDisp = this.displays.get(i).getValue();
			Display otherDisp = other.displays.get(i).getValue();
			if(thisDisp==null){// handle nulls
				if(otherDisp == null){
					continue;
				}else{
					return false;
				}
			}else{
				if(otherDisp==null){
					return false;
				}
			}
			
			if(thisDisp.equals(otherDisp)){
				//-- GOOD, same display objects! --//
			}else{
				return false;
			}
		}

		if(this.hashCode()!=0 && other.hashCode()!=0){
			if(this.hashCode()!=other.hashCode())
				return false;
		}
		return true;
	}
	
	@Override
	public Class<? super Task> getInterfaceClass() {
		return Task.class;
	}

	
	@Override
	public int hashCode(){
		int hash = 132;
		for(CachedValue<Display> d : displays){
			hash = HashCodeUtil.hash(hash, d.getValue());
		}
		if(this.steprule != null){
			hash = HashCodeUtil.hash(hash, steprule);
		}
		return hash;
	}
	


}
