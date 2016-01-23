package info.pinlab.ttada.cache.disk;

import java.io.File;

public abstract class AbstractSaveHook implements LocalSaveHook{
	final File rootPath; 
	
	public AbstractSaveHook(File absPath){
		if(!absPath.isAbsolute()){
			throw new IllegalArgumentException("Argument must be absolute '" + absPath.getPath()  +"'");
		}
		rootPath = absPath;
		
		if (rootPath.isDirectory() && !rootPath.exists()){
			//-- created dir if doesn't exists! --//
			rootPath.mkdirs();
		}
	}
	
	public File getRootPath(){
		return rootPath;
	}

}
