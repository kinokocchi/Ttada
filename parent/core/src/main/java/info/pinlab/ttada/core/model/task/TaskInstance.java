package info.pinlab.ttada.core.model.task;

import info.pinlab.utils.HashCodeUtil;




/**
 *
 * Hieararchy
 * <PRE>
 * Task         : repeatedly usable task descriptions 
 *  |
 * TaskInstance : concrete instance of the task in a taskSet
 *  | 
 * TaskToken    : concrete instance in a session
 * </PRE>
 * 
 * 
 * @author Gabor PINTER
 *
 */
public class TaskInstance {
	private final Task task;
	private final int ix;
	private final int tasksethash;
	private final int hash;
	
	public TaskInstance(Task task, int taskSetHash, int taskIxInTaskSet){
		this.task = task;
		ix = taskIxInTaskSet;
		tasksethash = taskSetHash;
		int hash_ = 434;
		hash_ = HashCodeUtil.hash(hash_, task);
		hash_ = HashCodeUtil.hash(hash_, taskSetHash);
		hash_ = HashCodeUtil.hash(hash_, taskIxInTaskSet);
		this.hash = hash_;
	}
	
	
	public Task getTask(){
		return task;
	}
	public int getTaskIx(){
		return ix;
	}
	public int getTaskSetHash(){
		return tasksethash;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TaskInstance))
			return false;
		TaskInstance other = (TaskInstance)obj;
		if(other.ix != this.ix)
			return false;
		if(other.tasksethash!=this.tasksethash)
			return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		return hash;
	}
}
