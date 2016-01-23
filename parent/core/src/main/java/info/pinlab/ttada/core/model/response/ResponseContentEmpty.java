package info.pinlab.ttada.core.model.response;

/**
 * Empty response content;
 * 
 * @author Gabor PINTER
 *
 */
public class ResponseContentEmpty extends AbstractResponseContent {
	
	public ResponseContentEmpty(long timeStamp, long respT){
		super(timeStamp, respT);
	}

	public ResponseContentEmpty(){
		super(System.currentTimeMillis(), 0);
	}

}
