package info.pinlab.ttada.core.model.task;


import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.utils.HashCodeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TaskSet implements Iterable<Task>, Iterator<Task>{
	public static Logger LOG = LoggerFactory.getLogger(TaskSet.class);
	private String brief = ""; //-- handy data holder (e.g., for IDs)
	
//	private int hash = 0;
	private final List<CachedValue<Task>> tasks  = new ArrayList<CachedValue<Task>>();
	
	transient private int currentTaskIx = 0;
	
	public TaskSet add(Collection<Task> tasks ){
		for(Task t : tasks)
			add(t);
		return this;
	}
	
	public TaskSet add(Task t){
		CachedValue<Task> cv = CachedValue.getCacheWrapperForValue(t, Task.class);
		tasks.add(cv);
		return this;
	}
	
	public TaskSet add(int ix, Task t){
		CachedValue<Task> cv = CachedValue.getCacheWrapperForValue(t, Task.class);
		tasks.add(ix, cv);
		return this;
	}

	/**
	 * 
	 * @param afterN  insert after every N
	 * @param t  the task to be inserted
	 * @param isIgnoreNonResponsibleTasks   when counting shall we ignore non-responsible tasks?
	 * @return
	 */
	public TaskSet insertAfterEvery(int afterIx, Task t, boolean isIgnoreNonResponsibleTasks){
		if(afterIx < 0){ //-- do nothing
			return this;
		}
		int taskN = 0;
		List<CachedValue<Task>> tmpTasks = new ArrayList<CachedValue<Task>>();
		CachedValue<Task> cachedTask = CachedValue.getCacheWrapperForValue(t, Task.class);

		for (int i = 0 ; i < tasks.size() ; i++ ){
			CachedValue<Task> task = tasks.get(i);
			tmpTasks.add(task);
			
			if(!isIgnoreNonResponsibleTasks || task.getValue().isResponsible()){
				taskN++;
				if (taskN == afterIx){
					taskN = 0;
					//-- check if next is responsible -> if not, don't add break!
					if (  tasks.size() == i+1 ){ //-- final task
						//-- don't add to after final
					}else{ //-- not final 
						Task nextTask = tasks.get(i+1).getValue();
						if(nextTask.isResponsible()  || !isIgnoreNonResponsibleTasks){ //-- next one is a real task
							tmpTasks.add(cachedTask);
						}
					}
				}
			} //-- not responsible
		}
		
		tasks.clear();
		for(CachedValue<Task> c : tmpTasks){
			tasks.add(c);
		}
		tmpTasks.clear();
		return this;
	}
	
	
	public TaskSet add(TaskSet ts){
		for(CachedValue<Task> cv : ts.tasks){
			tasks.add(cv);
		}
		return this;
	}
	
	public int size(){
		return tasks.size();
	}
	

	public void removeAll(){
//		Iterator<CachedValue<Task>> it = tasks.iterator();
		tasks.clear();
//		while(it.hasNext()){
//			it.remove();
//		}
	}
	
	
	public void remove(int ix){
		tasks.remove(ix);
	}
	
	public Task get(int ix){
		return tasks.get(ix).getValue();
	}
	
	public Task getTaskByHash(int hash){
		for(Task t : this){
			if(t.hashCode() == hash)
				return t;
		}
		return null;
	}
	
	@Override
	public Iterator<Task> iterator() {
		return this;
	}
	
	@Override
	public int hashCode(){
		int hash = 181;
		for(CachedValue<Task> cv : tasks){
			hash = HashCodeUtil.hash(hash, cv);
		}
		hash = HashCodeUtil.hash(hash, brief);
		return hash;
	}

	
	/*
	 * Shuffles task order
	 */
	public void shuffle(){
		LOG.info("Shuffling Task order");
		int taskN = tasks.size();
		List<Task> tasksToShuffle = new ArrayList<Task>();
		Map<Integer, Task> noShuffle = new HashMap<Integer, Task>();
		for(int i = 0; i < taskN ; i++){
			Task t = this.get(i);
			if(t.isResponsible()){
				tasksToShuffle.add(t);
			}else{
				noShuffle.put(i, t);
			}
		}
		tasks.clear();
		Collections.shuffle(tasksToShuffle);
		Iterator<Task> it = tasksToShuffle.iterator();
		for(int i = 0 ; i < taskN ; i++){
			Task t  = null;
			if(noShuffle.containsKey(i)){
				t = noShuffle.get(i);
			}else{
				t = it.next();
			}
			this.add(t);
		}
	}
	
	public TaskSet copy(){
		TaskSet copy = new TaskSet();
		for(Task t : this){
			copy.add(t);
		}
		return copy;
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TaskSet)){
			return false;
		}
		TaskSet other = (TaskSet)obj;
		int sz = this.size();
		if(sz!=other.size())
			return false;
		if(!this.brief.equals(other.brief))
			return false;
		for(int i = 0; i < sz ; i++){
			CachedValue<Task> thisTask = this.tasks.get(i);
			CachedValue<Task> otherTask = other.tasks.get(i);
			if(!thisTask.equals(otherTask)){
//				System.out.println(thisTask);
//				System.out.println(otherTask);
//				System.out.println("  FAILED TASK : equality");
				return false;
			}
		}
		return true;
	}

	/**
	 * Trims white space!
	 * 
	 * @param brief
	 */
	public void setBrief(String brief){
		this.brief = brief.trim();
	}
	public String getBrief(){
		return this.brief;
	}

	
	@Override
	public boolean hasNext(){
		return currentTaskIx < tasks.size();
	}

	@Override
	public Task next() {
		Task task = tasks.get(currentTaskIx).getValue();
		currentTaskIx++;
		return task;
	}

	@Override
	public void remove(){
		tasks.remove(currentTaskIx);
	}
}
