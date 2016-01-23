package info.pinlab.ttada.core.ser;

import info.pinlab.pinsound.WavClip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WavAdapter implements 
JsonSerializer<WavClip>,
JsonDeserializer<WavClip>{

	@Override
	public JsonElement serialize(WavClip wav, Type t,
			JsonSerializationContext context){
		String wavAsString = new String(Base64.encodeBase64(wav.toWavFile()));
		JsonObject obj = new JsonObject();
		obj.addProperty("file", wavAsString);
		obj.addProperty("frames", wav.getDurInFrames());
		obj.addProperty("hz", wav.getAudioFormat().getFrameRate());
		obj.addProperty("hash", wav.hashCode());
		return obj;
	}

	@Override
	public WavClip deserialize(JsonElement json, Type t,
			JsonDeserializationContext context) throws JsonParseException{
		JsonObject jobj =  json .getAsJsonObject();
		String wavfile = jobj.get("file").getAsString();
		byte [] stringBytes = wavfile.getBytes();

		//		byte [] stringBytes = json.getAsJsonPrimitive().getAsString().getBytes();
		ByteArrayInputStream baos = new ByteArrayInputStream(Base64.decodeBase64(stringBytes));
		try {
			return new WavClip(baos);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			return null;
		}
	}
}
