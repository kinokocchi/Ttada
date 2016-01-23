package info.pinlab.ttada.restcache;

import info.pinlab.ttada.cache.disk.DiskEnrollController;
import info.pinlab.ttada.core.cache.Cache;
import info.pinlab.ttada.core.cache.RemoteCache;
import info.pinlab.ttada.core.control.EnrollController;
import info.pinlab.ttada.core.model.response.ResponseSet;

public class RestEnrollController extends DiskEnrollController implements EnrollController{

	RemoteCache remoteCache = null; 
	
	public RestEnrollController(){
		super(new ResponseSet());
	}
	
	public RestEnrollController(ResponseSet rset){
		super(rset);
	}
	

	@Override
	public void setCache(Cache cache) {
		super.setCache(cache);
		
		if(cache instanceof RemoteCache){ //-- just to have a local copy;
			remoteCache = (RemoteCache)cache; 
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		remoteCache.disconnect();
	}

}
