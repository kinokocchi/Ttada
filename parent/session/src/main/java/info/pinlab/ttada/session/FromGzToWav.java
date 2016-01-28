package info.pinlab.ttada.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
import info.pinlab.utils.FileStringTools;



/**
 * This should go into CLI as a function 
 * 
 * 
 * @author Gabor Pinter
 *
 */
public class FromGzToWav {
	public static Logger LOG = LoggerFactory.getLogger(FromGzToWav.class);

	static SimpleJsonSerializer jsonSerializer ;

	public static WavClip readWavFromGz(String path, String wavPath){
		
		jsonSerializer = new SimpleGsonSerializerFactory().build();
		String absPath = new File(path).getAbsolutePath();
		LOG.info("Reading wav from '" + absPath + "'");
		try {
			byte[] inBytes= FileStringTools.getFileAsByteArray(absPath);
			String json = FileStringTools.unzip(inBytes);
			WavClip wav = jsonSerializer.fromJson(json, WavClip.class);

			//-- now write it out! --//
			byte [] outBytes = wav.toWavFile();
			FileOutputStream fos = new FileOutputStream(wavPath);
			fos.write(outBytes);
			fos.close();
			return wav;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) {
//		BasicConfigurator.configure();
//		String gzPath = "/home/kinoko/Desktop/.pinlab/WavClip/-217683088.gz";
//		String wavPath = "/home/kinoko/Desktop/.pinlab/WavClip/-217683088.wav";
//		WavClip wav = readWavFromGz(gzPath, wavPath);
//		System.out.println(wav);
//	}

}
