package info.pinlab.ttada.cache.disk;

import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.utils.AbsoluteTime;
import info.pinlab.utils.FileStringTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Saves responses into a single file while running a session (eg. an experiment)
 * Saves only {@link ResponseContentText }, such as multichoice answers, text input, button pushes.
 * 
 * @author Gabor Pinter
 *
 */
public class LocalSaveHookForTxtResponse extends AbstractSaveHook{
	public static Logger logger = Logger.getLogger(LocalSaveHookForTxtResponse.class); 
	private final File responseFile ; 
	private PrintWriter writer;
	
	/**
	 * 
	 * 
	 * @param absPath  absolute path to response file to save, or a dir where reponse file is created
	 */
	public LocalSaveHookForTxtResponse(File absPath){
		super(absPath);
		
		if(absPath.isDirectory()){
			responseFile = new File(absPath.getAbsolutePath() + FileStringTools.SEP + "responses_" + AbsoluteTime.toTimeStamp(System.currentTimeMillis()) + ".csv");
		}else{
			responseFile = absPath;
		}
		
		try {
			logger.info("Creating text response file '" + responseFile.getAbsolutePath() + "'");
			writer = new PrintWriter(new BufferedWriter(new FileWriter(responseFile, true)));
			writeFileHdr();
		} catch (IOException e) {
		    //oh noes!
		}
		//-- create save file --//
	}
	
	
	@Override
	public LocalSaveHookForTxtResponse relocate(File newAbsPath){
		return new LocalSaveHookForTxtResponse(newAbsPath);
	}
	
	
	
	
	private void writeFileHdr(){
		StringBuffer sb = new StringBuffer();
		sb	.append("#sessionId")	.append('\t')
			.append("taskSetId")	.append('\t')
			.append("taskSetBrief")	.append('\t')
			.append("taskIx")		.append('\t')
			.append("taskId")		.append('\t')
			.append("taskBrief")	.append('\t')
			.append("attemptN")		.append('\t')
			.append("timestamp")	.append('\t')
			.append("RT")			.append('\t')
			.append("text")			.append('\t')
			.append("brief")	
			;
		if(writer != null){
			writer.println(sb.toString());
			writer.flush();
		}

	}
	
	
	@Override
	public void save(Response resp) {
		ResponseHeader hdr = resp.getHeader();
		ResponseContent cont = resp.getContent();
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:SSS");
		String timeStamp = sdf.format(new Date(hdr.timestamp));
		
		
		if(cont instanceof ResponseContentText){
			StringBuffer sb = new StringBuffer();
			sb	.append(hdr.sessionId)	.append('\t')
				.append(hdr.taskSetId)	.append('\t')
				.append(hdr.taskSetBrief==null ? "" :hdr.taskSetBrief).append('\t')
				.append(hdr.taskIx)		.append('\t')
				.append(hdr.taskId)		.append('\t')
				.append(hdr.taskBrief==null ? "" :hdr.taskBrief).append('\t')
				.append(hdr.attemptN)	.append('\t')
				.append(timeStamp)	.append('\t')
				.append(cont.getResponseTime())	.append('\t')
				;
			
			String txt = ((ResponseContentText)cont).getText();
			String choiceVal = cont.getBrief();
			
			
//			System.out.println(choiceVal);
//			if(txt !=null && (choiceVal == null || choiceVal.isEmpty())){
//				int sepIx = txt.indexOf("::");
//				System.out.println(sepIx);
//				if(sepIx  > 0){
//					txt = txt.substring(0,sepIx);
//					choiceVal = txt.substring(sepIx+2);
//				}else{
//					sb.append(txt);
//				}
//			}else{ //-- txt is nULL
//				sb.append("");
//			}
			
			//-- escape TABs as its the delimiterTa
			txt = txt.replace("\t", "\\t");
			choiceVal = choiceVal.replace("\t", "\\t");
			
			sb	.append(txt)
				.append('\t')
				.append(choiceVal);

			if(writer != null){
				writer.println(sb.toString());
				writer.flush();
			}
		}
	}
	
	
	
	@Override
	public File getRootPath(){
		return new File(this.responseFile.getAbsolutePath());
	}
	
	
	public void dispose(){
		writer.flush();
		writer.close();
	}
	
}
