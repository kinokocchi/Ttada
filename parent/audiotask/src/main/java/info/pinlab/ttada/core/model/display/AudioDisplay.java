package info.pinlab.ttada.core.model.display;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.model.HasAudio;
import info.pinlab.ttada.core.model.HasAudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.pinsound.WavClip;
import info.pinlab.utils.HashCodeUtil;


public class AudioDisplay extends AbstractDisplay implements Display, HasAudio, HasAudioRule{
	private CachedValue<WavClip> wav = null;
	private CachedValue<AudioRule> audiorule = null;

	
	public AudioDisplay(WavClip wav){
		this.wav = CachedValue.getCacheWrapperForValue(wav, WavClip.class);
		this.audiorule = CachedValue.getCacheWrapperForValue(new AudioRule.AudioRuleBuilder().build(), AudioRule.class);
	}
	public void setAudioRule(AudioRule r){
		this.audiorule = CachedValue.getCacheWrapperForValue(r, AudioRule.class);
		hash = 0;
	}
	
	@Override
	public AudioRule getAudioRule(){
		return audiorule.getValue();
	}

	@Override
	public WavClip getAudio(){
		return wav.getValue();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof AudioDisplay))
			return false;

		AudioDisplay other = (AudioDisplay)obj;
		if(!this.audiorule.getValue().equals(other.audiorule.getValue()))
			return false;
		if(!this.wav.getValue().equals(other.wav.getValue()))
			return false;
//		if(audiorule==null){
//			if(other.audiorule!=null)
//				return false;
//		}else{
//			if(other.audiorule==null)
//				return false;
//			//-- if none of them is null --//
//			if(!this.audiorule.equals(other.audiorule))
//				return false;
//		}
		return true; 
	}
	
	@Override
	public int hashCode(){
		if(hash!=0) //-- lazy initialization;
			return hash;
		hash = 936;
		if(audiorule!=null){
			hash = HashCodeUtil.hash(hash, audiorule);
		}else{
			hash = HashCodeUtil.hash(hash, null);
		}
		hash = HashCodeUtil.hash(hash, wav);
		return hash;
	}

	@Override
	public Class<? super Display> getInterfaceClass() {
		return Display.class;
	}
}
