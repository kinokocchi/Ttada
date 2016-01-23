package info.pinlab.ttada.core.model.task;

import info.pinlab.ttada.core.cache.CachedValue;
import info.pinlab.ttada.core.model.HasAudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
import info.pinlab.utils.HashCodeUtil;

public class RecordTask extends AbstractTask implements HasAudioRule{
		private CachedValue<AudioRule> recRule = null;
		
		public RecordTask(){
			super.isResponsible(true);
			recRule = CachedValue.getCacheWrapperForValue(new AudioRuleBuilder().build(), AudioRule.class);
		}
		
		public void setRecRule(AudioRule rule){
			recRule = CachedValue.getCacheWrapperForValue(rule, AudioRule.class);
		}
		
		public AudioRule getRecRule(){
			return recRule.getValue();
		}
		
		@Override
		public boolean equals(Object obj){
			if(!(obj instanceof RecordTask))
				return false;
			RecordTask other = (RecordTask)obj;
			
			if(!super.equals(other))
				return false;

			if(!this.recRule.getValue().equals(other.recRule.getValue()))
				return false;
//			if (recRule!=null){
//				if( ! this.recRule.equals(other.recRule))
//						return false;
//			}else{
//				if(other.recRule != null)
//					return false;
//			}
			return true;
		}
		
		@Override
		public int hashCode(){
			int hash = 787;
			hash = HashCodeUtil.hash(hash, super.hashCode());
			if(recRule!=null){
				hash = HashCodeUtil.hash(hash, recRule);
			}
			return hash;
		}


		@Override
		public AudioRule getAudioRule() {
			return recRule.getValue();
		}
}
