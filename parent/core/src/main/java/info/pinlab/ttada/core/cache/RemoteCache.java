package info.pinlab.ttada.core.cache;

import info.pinlab.ttada.core.cache.Cache;


/** Cache with remote connections.
 * 
 * @author Gabor Pinter
 *
 */
public interface RemoteCache extends Cache, Remote{
	
	public void setResourceLink(String url);
	public String getResourceLink();
	
	public int getPendingTransactionN();
	
	public String getRemoteUri();
	
}
