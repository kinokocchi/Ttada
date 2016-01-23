package  info.pinlab.ttada.core.model.response;

import info.pinlab.utils.HashCodeUtil;





public abstract class AbstractResponseContent implements ResponseContent{
	private final long stamp;
	private final long rt;
	private final String brief;
	
	
	
	public AbstractResponseContent(ResponseContent content){
		AbstractResponseContent other = ((AbstractResponseContent)content);
		this.stamp = other.stamp;
		this.rt = other.rt;
		this.brief = "";
	}

	public AbstractResponseContent(long timeStamp, long responseTime){
		stamp = timeStamp;
		rt = responseTime;
		this.brief = "";
	}
	public AbstractResponseContent(long timeStamp, long responseTime, String brief){
		stamp = timeStamp;
		rt = responseTime;
		this.brief = brief;
	}
	
	@Override
	public long getTimeStamp(){
		return stamp;
	}
	@Override
	public long getResponseTime(){
		return rt;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj==null)
			return false;
		if(!AbstractResponseContent.class.isAssignableFrom(obj.getClass()))
			return false;
		AbstractResponseContent other = (AbstractResponseContent) obj;
		if(this.stamp == other.stamp && this.rt==other.rt){
			return true;
		}
		return false;
	}
	
	
	public String getBrief(){
		return brief;
	}
	
	@Override
	public int hashCode(){
		int hash = 3003;
		hash = HashCodeUtil.hash(hash, stamp);
		hash = HashCodeUtil.hash(hash, rt);
		hash = HashCodeUtil.hash(hash, brief);
		return hash;
	}
	
	@Override
	public Class<? super ResponseContent> getInterfaceClass() {
		return ResponseContent.class;
	}
}
