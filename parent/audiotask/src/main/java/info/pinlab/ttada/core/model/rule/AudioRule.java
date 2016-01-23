package info.pinlab.ttada.core.model.rule;

import info.pinlab.utils.HashCodeUtil;




public class AudioRule implements Rule{
	public static final int ver = 1; //-- increment this every time you change it!

	transient public static final int MAX_PLAY_N = -1;
	transient public static final int MAX_REC_N = -1;
	transient public static final int MAX_REC_LEN = 4*1000; /* 4 sec */
	transient public static final int DELAY = -1 ;
	transient public static final boolean CAN_PLAY =  true;
	transient public static final boolean CAN_PAUSE = true;
	transient public static final boolean CAN_STOP = true;
	transient public static final boolean CAN_REC = true;
	

	public final int maxRecN;
	public final long maxRecLen;
	public final int maxPlayN;
	public final int delay;

	public final boolean canPlay; 
	public final boolean canPause; 
	public final boolean canStop; 
	public final boolean canRec; 
	
	private AudioRule(int maxPlayN, int maxRecN, long maxRecLen,  int delay,
			boolean canPlay, boolean canPause,boolean  canStop,boolean  canRec){
		this.maxPlayN = maxPlayN;
		this.maxRecN = maxRecN;
		this.maxRecLen = maxRecLen;
		this.delay = delay;
		this.canPlay = canPlay; 
		this.canPause = canPause; 
		this.canStop = canStop; 
		this.canRec = canRec;
		
//		int _hash = 1129;
//		_hash = HashCodeUtil.hash(_hash, maxRecN);
//		_hash = HashCodeUtil.hash(_hash, maxRecLen );
//		_hash = HashCodeUtil.hash(_hash, maxPlayN);
//		_hash = HashCodeUtil.hash(_hash, delay);
//		hash = _hash;
	};
	
	public static class AudioRuleBuilder{
		private int maxRecN = MAX_REC_N;
		private int maxRecLen = MAX_REC_LEN;
		private int maxPlayN = MAX_PLAY_N;
		private int delay = DELAY;
		public boolean canPlay = CAN_PLAY; 
		public boolean canPause = CAN_PAUSE; 
		public boolean canStop = CAN_STOP; 
		public boolean canRec = CAN_REC; 
		
		public AudioRuleBuilder(){};
		
		public AudioRuleBuilder setMaxPlayN(int max){
			maxPlayN = max < 0 ? -1 : max;;
			return this;
		}
		public AudioRuleBuilder setMaxRecN(int max){
			maxRecN = max < 0 ? -1 : max;
			return this;
		}
		public AudioRuleBuilder setRecLen(int ms){
			maxRecLen = ms;
			return this;
		}
		public AudioRuleBuilder setDelay(int ms){
			delay = ms;
			return this;
		}
		public AudioRuleBuilder canPlay(boolean b){
			canPlay = b;
			return this;
		}
		public AudioRuleBuilder canPause(boolean b){
			canPause = b;
			return this;
		}
		public AudioRuleBuilder canStop(boolean b){
			canStop = b;
			return this;
		}
		public AudioRuleBuilder canRec(boolean b){
			canRec = b;
			return this;
		}
		
		public AudioRule build(){
			maxPlayN = maxPlayN < -1 ? -1 : maxPlayN;
			maxRecN = maxRecN < -1 ? -1 : maxRecN;
			maxRecLen =  maxRecLen < -1 ? -1 : maxRecLen;
			delay = delay < -1 ? -1 : delay;
			return new AudioRule(maxPlayN, maxRecN, maxRecLen, delay,
					canPlay, canPause,canStop,canRec);
		}
	}
	

	@Override
	public int hashCode(){
		int hash = 1129;
		hash = HashCodeUtil.hash(hash, maxRecN);
		hash = HashCodeUtil.hash(hash, maxRecLen );
		hash = HashCodeUtil.hash(hash, maxPlayN);
		hash = HashCodeUtil.hash(hash, delay);
		hash = HashCodeUtil.hash(hash, canPlay);
		hash = HashCodeUtil.hash(hash, canPause);
		hash = HashCodeUtil.hash(hash, canStop);
		hash = HashCodeUtil.hash(hash, canRec);
		return hash;
	}
	
	@Override
	public String toString(){
		 return "maxRecN = "+ maxRecN + "\n" 
		 + "maxRecLen = "+ maxRecLen + "\n" 
		 + "maxPlayN = "+ maxPlayN + "\n"
		 + "delay = "+ delay + "\n"
		 + "canPlay = "+ canPlay + "\n"
		 + "canPause = "+ canPause + "\n"
		 + "canStop = "+ canStop + "\n"
		 + "canRec = "+ canRec;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof AudioRule))
			return false;
		AudioRule other = (AudioRule) obj;
		if (this.maxRecN != other.maxRecN)
			return false;
		if (this.maxRecLen != other.maxRecLen)
			return false;
		if (this.maxPlayN != other.maxPlayN)
			return false;
		if (this.delay != other.delay)
			return false;
		return true;
	}
	
}
