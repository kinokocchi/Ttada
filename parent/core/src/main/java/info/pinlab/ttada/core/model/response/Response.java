package info.pinlab.ttada.core.model.response;

import info.pinlab.utils.HashCodeUtil;

/**
 * Response consists of (1) header (2) content
 * <ol>
 *   <li> header part
 *   <ul>
 *     <li> implemented by {@link ResponseHeader}
 *     <li> immutable fields 
 *     <li> includes info about the response: when, who, what task... etc.
 *   </ul> 
 *   <li> content part 
 *   <ul>
 *     <li> by classes implementing the {@link ResponseContent} interface
 *     <li> immutable fields 
 *     <li> includes user input data
 *   </ul> 
 * </ol>
 * 
 * @author Gabor Pinter
 *
 */
public class Response {
	private final ResponseHeader header;
	private final ResponseContent content;

	public Response(ResponseHeader hdr, ResponseContent content){
		this.header = hdr;
		this.content = content;
	}
	
	public ResponseHeader getHeader(){
		return header;
	}
	public ResponseContent getContent(){
		return content;
	}
	
	
	@Override
	public int hashCode(){
		int hash = 2837;
		hash = HashCodeUtil.hash(hash, header);
		hash = HashCodeUtil.hash(hash, content);
		return hash;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Response))
			return false;
		
		Response other = (Response)obj;
		
		if (this.hashCode()!= other.hashCode())
			return false;
		
		if(!this.header.equals(other.header))
			return false;

		if(!this.content.equals(other.content))
			return false;
		
		return true;

	}
	
	
}
