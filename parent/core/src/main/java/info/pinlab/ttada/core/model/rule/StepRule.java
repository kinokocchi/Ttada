package info.pinlab.ttada.core.model.rule;

import info.pinlab.utils.HashCodeUtil;




public class StepRule implements Rule {
	public static final int ver = 1; //-- increment this every time you change it!
	
	private final Boolean nextByUsr;
	private final Boolean nextByResp;
	private final Boolean nextByLastResp;
	private final Boolean nextByTimeout ;
	private final Boolean prevByUsr ;
	private final Integer maxAttempt;
	private final Integer timeout;
	private final int hash;
	
	//-- default values;
	transient public static final boolean NEXT_BY_USER = true;
	transient public static final boolean NEXT_BY_RESP = false;
	transient public static final boolean NEXT_BY_LAST_RESP = false; // if there is no more response
	transient public static final boolean NEXT_BY_TIMEOUT = false;
	transient public static final boolean PREV_BY_USR = true ;
	transient public static final int MAX_RESP_N = -1;
	transient public static final int TIMEOUT = -1;
	
	private StepRule(Boolean nextByUsr,
			Boolean nextByResp, 
			Boolean nextByLastResp, 
			Boolean nextByTimeout, 
			Boolean prevByUsr,
			Integer maxAttempt,
			Integer timeout){
		this.nextByUsr = nextByUsr;
		this.nextByResp = nextByResp ;
		this.nextByLastResp = nextByLastResp ;
		this.nextByTimeout = nextByTimeout;
		this.prevByUsr = prevByUsr;
		this.maxAttempt = maxAttempt != null ? (maxAttempt < 0 ? -1 : maxAttempt) : null;
		this.timeout = timeout != null ? (timeout < 0 ? -1 : timeout) : null;
		
		int _hash = 577;
		 for(Object obj : getSignificantFields()){
			 if(obj!=null){
				 _hash = HashCodeUtil.hash(_hash, obj);
			 }
		 }
		 hash = _hash;
	}
	
	public boolean isNextByUsr() {
		return nextByUsr == null ? true : nextByUsr;
	}

	public boolean isNextByResp() {
		return nextByResp == null ? true : nextByResp;
	}

	public boolean isNextByLastResp() {
		return nextByLastResp == null ? true : nextByLastResp;
	}

	public boolean isNextByTimeout() {
		return nextByTimeout == null ? true : nextByTimeout;
	}

	public boolean isPrevByUsr() {
		return prevByUsr == null ? true : prevByUsr;
	}

	public int getMaxAttempt() {
		return maxAttempt == null ? -1 : maxAttempt;
	}

	public int getTimeout() {
		return timeout == null ? -1 : timeout;
	}

	/*
	 * Defaults are very liberal : user can move around freely!
	 */
	public static class StepRuleBuilder{
		private Boolean nextByUsr = NEXT_BY_USER;
		private Boolean nextByResp = NEXT_BY_RESP;
		private Boolean nextByLastResp = NEXT_BY_LAST_RESP;
		private Boolean nextByTimeout = NEXT_BY_TIMEOUT;
		private Boolean prevByUsr = PREV_BY_USR;
		private Integer attemptN = MAX_RESP_N;
		private Integer timeout = TIMEOUT;
		
		public StepRuleBuilder(){}
		
		public StepRuleBuilder setNextByUsr(boolean nextByUsr) {
			this.nextByUsr = nextByUsr;
			return this;
		}
		public StepRuleBuilder setNextByResp(boolean nextByResp) {
			this.nextByResp = nextByResp;
			return this;
		}
		public StepRuleBuilder setNextByLastResp(boolean nextByLastResp) {
			this.nextByLastResp = nextByLastResp;
			return this;
		}
		public StepRuleBuilder setNextByTimeout(boolean nextByTimeout) {
			this.nextByTimeout = nextByTimeout;
			return this;
		}
		public StepRuleBuilder setPrevByUsr(boolean prevByUsr) {
			this.prevByUsr = prevByUsr;
			return this;
		}
		public StepRuleBuilder setTimeout(int timeoutInMs) {
			this.timeout = timeoutInMs;
			this.nextByTimeout = true;
			return this;
		}
		public StepRuleBuilder setMaxAttempt(int max) {
			this.attemptN = max;
			return this;
		}
		public StepRule build(){
			return new StepRule(nextByUsr, nextByResp, nextByLastResp, 
					nextByTimeout, prevByUsr, attemptN, timeout);
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof StepRule)) 
			return false;
		StepRule other = (StepRule)obj;
		if (other.hashCode() != this.hashCode())
			return false;
		if(	this.nextByUsr == other.nextByUsr
		&& this.nextByResp == other.nextByResp 
		&& this.nextByResp == other.nextByResp 
		&& this.nextByTimeout == other.nextByTimeout
		&& this.prevByUsr == other.prevByUsr	
		&& this.maxAttempt == other.maxAttempt
		&& this.timeout == other.timeout){
			return true;
		}
		return false;
	}
	
	 @Override 
	 public int hashCode(){
		 return hash;
	 }
	 private Object[] getSignificantFields(){
		 return new Object[] {nextByUsr, 
				 nextByResp, nextByLastResp,
				 nextByTimeout, prevByUsr, 
				 maxAttempt, timeout};
	 }
	 
	 
	 transient private String toString = null;

	 @Override
	 public String toString(){
		 if (toString != null)
			 return toString;
		 StringBuffer sb = new StringBuffer();
		 sb.append("RULES " ).append("\n")
		 	.append("  maxAttempt:      ").append(maxAttempt).append("\n")
		 	.append("  nextByTimeout:   ").append(nextByTimeout).append("\n")
		 	.append("  timeout:         ").append(timeout).append("\n")
		 	.append("  nextByUsr:       ").append(nextByUsr).append("\n")
		 	.append("  nextByResp:      ").append(nextByResp).append("\n")
		 	.append("  nextByLastResp:  ").append(nextByLastResp).append("\n")
		 	.append("  prevByUsr:       ").append(prevByUsr).append("\n")
		 	;
		 toString = sb.toString();
		 return toString;
	 }
}
