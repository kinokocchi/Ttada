package info.pinlab.ttada.core.model.task;

import info.pinlab.utils.HashCodeUtil;


public class LoginTask extends AbstractTask{
	private boolean hasPwd = false;
	public LoginTask(){
		super.isResponsible = false;
	}
	
	public LoginTask hasPwd(boolean hasPwd){
		this.hasPwd = hasPwd;
		return this;
	}
	public boolean hasPwd(){
		return hasPwd;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof LoginTask))
			return false;
		LoginTask other = (LoginTask)obj;
		if (this.hasPwd != other.hasPwd)
			return false;
		
		if(!super.equals(other))
			return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = HashCodeUtil.hash(293, super.hashCode());
		hash = HashCodeUtil.hash(hash, hasPwd);
		return hash;
	}
}
