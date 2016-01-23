package info.pinlab.ttada.core.model.response;

import info.pinlab.utils.HashCodeUtil;


public class ResponseContentText extends AbstractResponseContent{
	private final String text;
	
	/**
	 * @param timeStamp  in ms
	 * @param respT   response time - in millisecond
	 * @param txt  response text
	 */
	public  ResponseContentText(long timeStamp, long respT, String txt){
		super(timeStamp, respT);
		text = txt;
	}

	public  ResponseContentText(long timeStamp, long respT, String txt, String brief){
		super(timeStamp, respT, brief);
		text = txt;
	}
	
	public String getText(){
		return text;
	}
	
	@Override
	public int hashCode(){
		int hash = super.hashCode();
		hash = HashCodeUtil.hash(hash, text);
		return hash;
	}
}
