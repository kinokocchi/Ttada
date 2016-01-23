package info.pinlab.ttada.core.cache;

import java.io.File;

public interface LocalCache extends Cache{
	
	
//	public File getTextResponseFile();
//	public boolean isSaveTextResponse();
//	public void isSaveTextResponse(boolean  b);
	
	
	
//	public void isSaveTextResponseAsText(boolean b);
//	public void isSaveAudioResponseAsWav(boolean b);
	
	/**
	 * No set method for cache dir, as it is given in {@link DiskCacheBuilder}
	 * @return
	 */
	public File getRootDir();
	
}
