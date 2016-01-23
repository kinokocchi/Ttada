package info.pinlab.ttada.core.ser;

import static org.junit.Assert.assertTrue;
import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentAudio;
import info.pinlab.ttada.core.model.response.ResponseContentAudioTestResource;
import info.pinlab.ttada.gson.SerializerUtil;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Using default gson adapter. 
 * 
 * @author Gabor Pinter
 *
 */
public class WavSerializatonTest {

	@BeforeClass
	public static void registerClassTag(){
		SerializerUtil.addClassTag("ResponseContentAudio", ResponseContentAudio.class);
	}
	
	@Test
	public void wavClipSerializationTest(){
		List<WavClip> wavs = new WavClipTestResource().getResources();
		assertTrue("No WavClips to test! (null array)",  wavs != null);
		assertTrue("No WavClips to test! (empty array)", wavs.size() > 0 );
		
		for (WavClip wav : wavs){
			assertTrue(wav!=null);
			SerializerUtil.serializeAndCompare(wav, WavClip.class);
		}
	}

	@Test
	public void responseContentAudioClipSerializationTest(){
		for(ResponseContentAudio audio : new ResponseContentAudioTestResource().getResources()){
			assertTrue(audio!=null);
			SerializerUtil.serializeAndCompare(audio, ResponseContentAudio.class);

			//-- try interface class --//
			SerializerUtil.serializeAndCompare(audio, ResponseContent.class);
		}

	}

}
