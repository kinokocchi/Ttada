package info.pinlab.ttada.core.model.response;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.model.response.ResponseContentAudio;
import info.pinlab.ttada.core.ser.WavClipTestResource;

import java.util.Random;

public class ResponseContentAudioTestResource extends AbstractTestResourceProvider<ResponseContentAudio>{
	
	public ResponseContentAudioTestResource(){
		super(ResponseContentAudio.class);
		Random rand = new Random();
		
		for(WavClip wav : new WavClipTestResource().getResources()){
			addResource(
					new ResponseContentAudio(
						System.currentTimeMillis()+rand.nextInt(10), 
						500 + rand.nextInt(1000), 
						wav)
					);
		}
	}
	
}
