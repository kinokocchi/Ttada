package info.pinlab.ttada.core.model.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * A set of ResponseContents. 
 * 
 * @author Gabor Pinter
 *
 */
public class ResponseContentMulti  extends AbstractResponseContent implements Iterable<ResponseContent>{
	final List<ResponseContent> responseContents;
	
	public ResponseContentMulti(long timeStamp, long responseTime, List<ResponseContent> responses) {
		super(timeStamp, responseTime);
		responseContents = new ArrayList<ResponseContent>();
		for(ResponseContent cont : responses){
			responseContents.add(cont);
		}
	}

	@Override
	public Iterator<ResponseContent> iterator() {
		return responseContents.iterator();
	}
	

}
