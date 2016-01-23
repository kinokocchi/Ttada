package info.pinlab.ttada.core.model.response;

import java.util.Random;

import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;

public class ResponseHeaderTestResource extends AbstractTestResourceProvider<ResponseHeader>{

	private static Random rand = new Random(System.currentTimeMillis());
	
	public ResponseHeaderTestResource(){
		super(ResponseHeader.class);

		String[] taskTypes = new String []{
				"MultichoiceTask", "InfoTask", "EssayTaks", "EssayTask", "RecordingTask"
		};
		
		for (int i = 0 ; i < 20 ; i++){
			int sessIx = i % 4;  
			ResponseHeader hdr = new ResponseHeaderBuilder()
			.setSessionId("sess_"+sessIx)
			.setTaskIx(rand.nextInt(1000)) 
			.setTaskId(rand.nextInt(1000))
			.setAttemptN(1)
			.setTaskSetId(22332)
			.setTaskType(taskTypes[rand.nextInt(taskTypes.length)])
			.setTimeStamp(System.currentTimeMillis()-20*1000 + rand.nextInt(40*1000))
			.setUsrId("anon "+i)
			.build();
	
			addResource(hdr);
		}
	}
	

}
