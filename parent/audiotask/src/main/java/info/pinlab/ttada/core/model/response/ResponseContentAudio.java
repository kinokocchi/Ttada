package info.pinlab.ttada.core.model.response;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.pinsound.WavClip;
import info.pinlab.utils.HashCodeUtil;


public class ResponseContentAudio extends AbstractResponseContent {
	private final CachedValue<WavClip> wav;
	
	public ResponseContentAudio(long timeStamp, int responseTime, WavClip wav){
		super(timeStamp, responseTime);
		assert(wav!=null);
//		assert("argument wav can't be null!", wav!=null);
		this.wav = CachedValue.getCacheWrapperForValue(wav, WavClip.class);
	}
	public WavClip getWav(){
		return wav.getValue();
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ResponseContentAudio))
			return false;
		ResponseContentAudio other = (ResponseContentAudio)obj;
		if(!super.equals(other))
			return false;
		if(!this.wav.equals(other.wav))
			return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = super.hashCode();
		hash = HashCodeUtil.hash(hash, wav);
		return hash;
	}
	@Override
	public String toString(){
		return "[t=" + this.getTimeStamp() + " rt="+this.getResponseTime() + " " +   (wav==null ? "no_wav " : wav.toString())+ " ]";
				
	}
	
	
}
