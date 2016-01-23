package info.pinlab.ttada.core.model;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.task.AbstractTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.utils.HashCodeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MultichoiceTask extends AbstractTask implements Iterable<Display>, Iterator<Display>{
	public static Logger LOG = LoggerFactory.getLogger(MultichoiceTask.class);
	private final List<CachedValue<Display>> choices ;
	private int rown = 1; //-- how many rows of buttoms
	
	private volatile int choiceIx = 0; //-- for iteration

	public static void shuffleChoices(TaskSet tset){
		for(Task task : tset){
			if(task instanceof MultichoiceTask){
				((MultichoiceTask)task).shuffleChoices();
			}
		}
	}
	
	public MultichoiceTask(){
		super.isResponsible(true);
		choices = new ArrayList<CachedValue<Display>>();
	}

	public MultichoiceTask addChoice(Display choice){
		CachedValue<Display> disp = CachedValue.getCacheWrapperForValue(choice, Display.class);
		choices.add(disp);
		return this;
	}

	/**
	 * 
	 * @param label  to show or save
	 * @param val  to have
	 * @return
	 */
	public MultichoiceTask addChoice(String label, String val){
		TextDisplay textDisp = new TextDisplay(label);
		textDisp.setBrief(val);
		CachedValue<Display> disp = CachedValue.getCacheWrapperForValue(textDisp, Display.class);
		choices.add(disp);
		return this;
	}
	
	public MultichoiceTask addChoice(String choice){
		return this.addChoice(choice, choice);
	}
	
	
	public void clearChoices(){
		choices.clear();
	}
	
	public int getChoiceN(){
		return choices.size();
	}
	
	public void setRowN(int n){
		this.rown = n;
	}
	public int getRowN(){
		return this.rown;
	}
	public void shuffleChoices(){
		Collections.shuffle(choices);
	}
	
	public Display getChoiceX(int ix){
		if (ix >= choices.size()){
			LOG.error("Choice ix out of bound " + ix + " / " + choices.size() + "!");
			return null;
		}
		return choices.get(ix).getValue();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof MultichoiceTask))
			return false;
		MultichoiceTask other = (MultichoiceTask)obj;
		if(!super.equals(other))
			return false;
		if (other.getRowN()!= this.getRowN())
			return false;
		int sz = choices.size();
		if(sz != other.choices.size())
			return false;
		for (int i = 0 ; i < sz ; i++){
			if(!(this.choices.get(i).getValue().equals(other.choices.get(i).getValue()))){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = HashCodeUtil.hash(373, super.hashCode());
		for(CachedValue<? extends Display> choice : choices){
			hash = HashCodeUtil.hash(hash, choice);
		}
		hash = HashCodeUtil.hash(hash, this.rown);
		return hash;
	}

	@Override
	public Iterator<Display> iterator() {
		choiceIx = 0;
		return this;
	}

	
	@Override
	public boolean hasNext() {
		return choiceIx != choices.size();
	}

	@Override
	public Display next(){
		final Display disp = this.getChoiceX(choiceIx); 
		choiceIx++;
		return disp;
	}

	@Override
	public void remove(){
		int ixToRemove = choiceIx-1;
		choices.remove(ixToRemove);
		choiceIx--;
	}
	
	
	
}
