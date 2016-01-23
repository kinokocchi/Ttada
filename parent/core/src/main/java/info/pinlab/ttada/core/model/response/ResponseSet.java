package info.pinlab.ttada.core.model.response;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;


/**
 * Special data holder for responses.
 * 
 * * Index does not move when responses are added
 * x-x
 * ^
 * x-x-x-x-x-x
 * ^
 * 
 * Index can be moved to iterate over the growing list.
 * 
 * hasNext()
 * x-x-x-x-x-x
 * ^
 * next()  : gets the next and moves index  
 * x-x-x-x-x-x
 *   ^
 * 
 * status of data:
 * - absent
 * - queued
 * - processed
 * 
 * @author Gabor Pinter
 *
 */
public class ResponseSet {
	public static final Logger LOG = LoggerFactory.getLogger(ResponseSet.class);
//	private final Deque<Response> responses ;

	private int index = -1;
	private int lastIx = -1;
	private boolean isSealed = false;
	private final Map<Integer, Response> responses ;
	private final Set<Integer> responseSet;
	
	private final Map<Integer, Integer> taskRespCounter;
	private final Map<Integer, Integer> taskInstRespCounter;
	
	
	
	public ResponseSet(){
		responses = new ConcurrentHashMap<Integer, Response>();
		responseSet = new ConcurrentSkipListSet<Integer>();
		taskRespCounter = new ConcurrentHashMap<Integer, Integer>();
		taskInstRespCounter = new ConcurrentHashMap<Integer, Integer>();
	}
	
	/**
	 * Creates a new ResponseSet with identical responses.
	 * 
	 * @param rset
	 */
	public ResponseSet(ResponseSet original){
		this();
		//-- copy responses
		for(Integer ix : original.responses.keySet()){
			responses.put(ix, original.responses.get(ix));
		}
		this.lastIx = original.lastIx;
	}
	
	
	synchronized public void add(Response response){
		if(isSealed){
			LOG.error("The responset is SEALED already!");
			throw new IllegalStateException("Already sealed!");
		}
		int responseHash  = response.hashCode();
		if(responseSet.contains(responseHash)){
			LOG.warn("This response is already in the ResponseSet queue! "
					+ response.getContent().getClass().getSimpleName() 
					+ " " + response.getContent().hashCode());
			return;
		}else{
			responseSet.add(responseHash);
		}
		
		lastIx++;
		responses.put(lastIx, response);

		int taskInstHash = response.getHeader().taskInstId;
		if(taskInstRespCounter.containsKey(taskInstHash)){
			taskInstRespCounter.put(taskInstHash, taskInstRespCounter.get(taskInstHash)+1);
		}else{
			taskInstRespCounter.put(taskInstHash, 1);
		}
		int taskHash     = response.getHeader().taskInstId;
		if(taskRespCounter.containsKey(taskHash)){
			taskRespCounter.put(taskHash, taskRespCounter.get(taskHash)+1);
		}else{
			taskRespCounter.put(taskHash, 1);
		}
	}
	
	
	synchronized public int size(){
//		System.out.println("Last ix " + lastIx);
		return lastIx+1;
	}

	synchronized public Response next(){
		if(index==lastIx)
			return null;
		index++;
		Response next = responses.get(index);
		return next;
	}
	
	synchronized public void undoNext(){
		if(index>0)	index--;
	}
	
	
	synchronized public int getUnrpocessedN(){
		return (lastIx-index);
	}
	/**
	 * Clears processed responses: up till the index; 
	 * 
	 * @return number of responses removed;
	 */
	synchronized public int clear(){
		int removedN = 0;
		for(int i = 0; i < index; i++){
			responses.remove(i);
			removedN++;
		}
		return removedN;
	}
	
	
	public int getAttemptCntForTask(Task task){
		Integer attemptN = this.taskRespCounter.get(task.hashCode());
		if (attemptN==null){
			return 0;
		}
		return attemptN;
	}
	
	
	public int getAttemptCntForTaskInstance(TaskInstance taski){
		Integer attemptN = this.taskInstRespCounter.get(taski.hashCode());
		if (attemptN==null){
			return 0;
		}
		return attemptN;
	}
	
	
	/**
	 * Sealing means that no further responses are taken.
	 * 
	 * @param b
	 */
	synchronized public void seal(){
		isSealed = true;
	}
	
	/**
	 * TaskSet is finished, no more data can be added. 
	 * 
	 * @return
	 */
	synchronized public boolean isSealed(){
		return isSealed; 
	}
	
	
	synchronized public boolean hasNext(){
		return index < lastIx;
	}
}


