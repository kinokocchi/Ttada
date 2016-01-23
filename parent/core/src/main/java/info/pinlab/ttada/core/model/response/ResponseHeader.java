package info.pinlab.ttada.core.model.response;


import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.utils.AbsoluteTime;
import info.pinlab.utils.HashCodeUtil;


/**
 * This class provides info to identify a response resource.
 * The following info can identify a response : 
 *  <ul> 
 *   <li> user
 *   <li> session id
 *   <li> task set id
 *   <li> attempt
 *  </ul>
 * For identification timestamp is also created.
 * 
 * Use {@link #toString()} function to create file names for example.
 * 
 * @author Gabor Pinter
 *
 */
public class ResponseHeader {
	public final int taskSetId ;
	public final String taskSetBrief ;
	public final int taskId ;
	public final int taskIx ;
	public final int taskInstId ;
//	public final Class<? extends Task> taskType;
	public final String taskType;
	public final String taskBrief;
	
	public final String sessionId ;
	public final String usrId ;
	public final int attemptN;
	
	
	public final long timestamp;
	private String timestampLabel;
	
	public ResponseHeader( 
			int taskSetId, 
			String taskSetBrief,
			int taskId, 
			int taskInstId,
			int serial, 
//			Class<? extends Task> taskType, 
			String taskType,
			String taskBrief,
			
			String sessionId, 
			String usrId, 
			int attempt, 
			long timestamp 
			){
				this.taskSetId = taskSetId;
				this.taskSetBrief = taskSetBrief;
				this.taskId = taskId;
				this.taskInstId = taskInstId;
				this.taskIx = serial;
				this.taskType = taskType;
				this.taskBrief = taskBrief;

				this.sessionId = sessionId; 
				this.usrId = usrId;
				this.attemptN = attempt;
				
				this.timestamp = timestamp; 
				this.timestampLabel = AbsoluteTime.toTimeStamp(timestamp);
	}
	
	public ResponseHeader getNextAttempt(){
		return new ResponseHeaderBuilder()
		.setAttemptN(this.attemptN+1)
		.setSessionId(this.sessionId)
		.setTaskId(this.taskId)
		.setTaskInstId(this.taskInstId)
		.setTaskIx(this.taskIx)
		.setTaskSetId(this.taskSetId)
		.setTaskType(this.taskType)
		.setTimeStamp(this.timestamp)
		.build();
	}
	
	
	public static class ResponseHeaderBuilder{
		private int taskSetId = -1;
		private String taskSetBrief = "";
		private int taskId = -1;
		private int taskInstId = -1;
		private int serial = -1;
//		private Class<? extends Task> taskType = null;
		private String taskType = null;
		private String taskBrief = "";
		
		private String sessionId = null;
		private String usrId = null;
		private int attemptN = -1;
		private long timestamp;
		
		
		public ResponseHeaderBuilder setTaskBrief(String brief){
			taskBrief = brief;
			return this;
		}
		public ResponseHeaderBuilder setTaskSetId(int id){
			taskSetId = id;
			return this;
		}
		public ResponseHeaderBuilder setTaskSetBrief(String brief){
			this.taskSetBrief = brief;
			return this;
		}
		public ResponseHeaderBuilder setTaskIx(int i){
			serial = i;
			return this;
		}
		public ResponseHeaderBuilder setAttemptN(int i){
			attemptN = i;
			return this;
		}
		public ResponseHeaderBuilder setSessionId(String id){
			sessionId = id;
			return this;
		}
		public ResponseHeaderBuilder setTaskId(int id){
			taskId = id;
			return this;
		}
		public ResponseHeaderBuilder setTaskInstId(int id){
			taskInstId = id;
			return this;
		}
		public ResponseHeaderBuilder setUsrId(String id){
			usrId = id;
			return this;
		}
		public ResponseHeaderBuilder setTaskType(Class<? extends Task> t){
			taskType = t.getSimpleName();
			return this;
		}
		public ResponseHeaderBuilder setTaskType(String t){
			taskType = t;
			return this;
		}
		public ResponseHeaderBuilder setTimeStamp(long t){
			timestamp = t;
			return this;
		}
		public ResponseHeader build(){
			return new ResponseHeader(
					taskSetId,
					taskSetBrief,
					taskId,
					taskInstId,
					serial, 
					taskType, 
					taskBrief,
					
					sessionId,
					usrId,
					attemptN, 
					timestamp
					); 
		}
	}
	
	
	transient int hash = 0;
	
	@Override
	public int hashCode(){
		if(hash!=0) //-- lazy initialization;
			return hash;
		hash = 3449;
		hash = HashCodeUtil.hash(hash, taskSetId);
		hash = HashCodeUtil.hash(hash, taskSetBrief);
		hash = HashCodeUtil.hash(hash, taskId);
		hash = HashCodeUtil.hash(hash, taskInstId);
		hash = HashCodeUtil.hash(hash, taskType);
		hash = HashCodeUtil.hash(hash, sessionId);
		hash = HashCodeUtil.hash(hash, usrId);
		hash = HashCodeUtil.hash(hash, taskIx);
		hash = HashCodeUtil.hash(hash, attemptN);
		hash = HashCodeUtil.hash(hash, timestamp);
		return hash;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ResponseHeader))
			return false;

		ResponseHeader other = (ResponseHeader) obj;
		
		if(this.hashCode()!=other.hashCode())
			return false;
		//-- possible cannot go below if non-equal
		if(this.taskInstId != other.taskInstId)
			return false;
		if(this.timestamp != other.timestamp)
			return false;
		if(this.taskIx != other.taskIx)
			return false;
		if(this.taskId != other.taskId)
			return false;
		if(this.taskSetId != other.taskSetId)
			return false;
		if(!this.sessionId.equals(other.sessionId))
			return false;
		if(!this.taskSetBrief.equals(other.taskSetBrief))
			return false;
		if(!this.usrId.equals(other.usrId))
			return false;
		return true;
	}
	
	private static final String FIELD_SEP = "_";
	@Override
	public String toString(){
		return 
				taskSetId 
		+ FIELD_SEP + sessionId 
		+ FIELD_SEP + taskSetBrief 
		+ FIELD_SEP +  (taskIx< 0 ? "XX" : String.format("%02d", taskIx))
		+ "-" + taskType
		+ FIELD_SEP + "U" + (usrId == null ? "" : usrId)
		+ FIELD_SEP + "A" + (attemptN < 0 ? "XX" : String.format("%02d",attemptN))  
		+ FIELD_SEP + timestampLabel 
		;
	}
	
	
	
	
	
}