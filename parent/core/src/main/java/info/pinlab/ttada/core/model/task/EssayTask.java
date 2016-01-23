package info.pinlab.ttada.core.model.task;

import info.pinlab.utils.HashCodeUtil;


public class EssayTask extends AbstractTask {
	private int prepIntervalInMs = -1; 
	private int workIntervalInMs = -1; 
	
	public EssayTask(){	}
	
	public EssayTask setPrepInterval(int sec){
		prepIntervalInMs = sec*1000;
		return this;
	}
	public EssayTask setWriteInterval(int sec){
		workIntervalInMs = sec*1000;
		return this;
	}

	public int getFirstInterval() {
		return prepIntervalInMs;
	}

	public int getSecondInterval() {
		return workIntervalInMs;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof EssayTask))
			return false;
		EssayTask other = (EssayTask)obj;
		if(!super.equals(other))
			return false;
		if(prepIntervalInMs != other.prepIntervalInMs)
			return false;
		if(workIntervalInMs != other.workIntervalInMs)
			return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = HashCodeUtil.hash(701, super.hashCode());
		hash = HashCodeUtil.hash(hash, prepIntervalInMs);
		hash = HashCodeUtil.hash(hash, workIntervalInMs);
		return hash;
	}
}
