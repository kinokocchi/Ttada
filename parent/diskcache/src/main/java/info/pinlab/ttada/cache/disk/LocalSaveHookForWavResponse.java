package info.pinlab.ttada.cache.disk;

import info.pinlab.pinsound.WavClip;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentAudio;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.utils.FileStringTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;


/**
 * 
 * Saves wav responses to local directory given at the cont'
 * 
 * @author Gabor Pinter
 *
 */
public class LocalSaveHookForWavResponse extends AbstractSaveHook {
	public static Logger logger = Logger.getLogger(LocalSaveHookForWavResponse.class);
	
	private final File wavDir ;
	
	public LocalSaveHookForWavResponse(File absPathToWavDir){
		super(absPathToWavDir);
		this.wavDir = new File(this.rootPath.getAbsolutePath() + FileStringTools.SEP + "wavs");
		logger.info("Wav dir was set to '" + wavDir.getAbsolutePath() + "'");
		
		
		if(this.wavDir.exists() || this.wavDir.isDirectory()){
			// -- good, existing dir!
		}else{
			logger.info("Creating text wav dir  '" + this.wavDir.getAbsolutePath() + "'");
			this.wavDir.mkdirs();
		}
	}
	

	@Override
	public File getRootPath(){
		return new File(this.wavDir.getAbsolutePath());
	}
	
	
	@Override
	public void save(Response resp){
		ResponseHeader hdr = resp.getHeader();
		ResponseContent content = resp.getContent();
		
		if(content instanceof ResponseContentAudio){
			ResponseContentAudio audio = (ResponseContentAudio) content;
			WavClip wav = audio.getWav();
			byte [] bytes = wav.toWavFile();
			
			String fileName = hdr.usrId + "_" + hdr.taskSetId + "_" + hdr.sessionId + "_" 
					+ hdr.taskIx + "_" + hdr.attemptN + "_" + hdr.timestamp +".wav";
			File wavPath = new File(wavDir +  DiskCache.SEP  + fileName);
			try{
				logger.info("Saving wav file file '" + wavPath.getAbsolutePath() + "'");
				FileOutputStream fos = new FileOutputStream(wavPath);
				fos.write(bytes);
				fos.close();
			}catch(IOException e){
				logger.info("Failed to write text transcript file '" + wavPath.getAbsolutePath() + "'");
			}
			
			String trsFileName = hdr.usrId + "_" + hdr.taskSetBrief + "_" + hdr.taskSetId + "_" + hdr.sessionId + "_" 
					+ hdr.taskIx + "_" + hdr.attemptN + "_" + hdr.timestamp +".txt"; 
			File trsPath = new File(wavDir +  DiskCache.SEP  + trsFileName);
			try {
				logger.info("Creating text transcript file '" + trsPath.getAbsolutePath() + "'");
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(trsFileName, true)));
				writer.println(hdr.taskBrief);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				logger.info("Failed to write text transcript file '" + trsPath.getAbsolutePath() + "'");
			}
		}
	}


	@Override
	public LocalSaveHook relocate(File newAbsPath) {
		return new LocalSaveHookForWavResponse(newAbsPath);
	}
	
}
