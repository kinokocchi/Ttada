package info.pinlab.ttada.core.ser;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;

public class WavClipTestResource extends AbstractTestResourceProvider<WavClip>{
	
	static private String[] embeddedWavNames = new String[] {"sample1.wav", "sample2.wav"} ;
	static private List<WavClip> wavs = new ArrayList<WavClip>();

	static {
		InputStream is = null;
		for(String wavFileName : embeddedWavNames){
			is = WavClipTestResource.class.getResourceAsStream(wavFileName);
			WavClip wav ;
			try {
				wav = new WavClip(is);
				wavs.add(wav);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}
	}

	public WavClipTestResource(){
		super(WavClip.class);
		for (WavClip wav : wavs){
			addResource(wav);
		}
	}
	
	public static class TestClass extends AbstractTestResourceProvider<String>{
		public TestClass() {
			super(String.class);
		}
	}
	
	@Test
	public void testSerialization(){
		WavClip wav = wavs.get(0);
		byte [] bytes = wav.toWavFile();
		System.out.println(bytes);
		
		
		
		SimpleJsonSerializerFactory factory = new SimpleGsonSerializerFactory();
		factory.registerTypeAdapter(WavClip.class, new WavAdapter());
		SimpleJsonSerializer json = factory.build();
		String wavAsString = json.toJson(wav);
		System.out.println(wavAsString);
	}

}
