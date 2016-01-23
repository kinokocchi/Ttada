package info.pinlab.ttada.cache.disk;

import info.pinlab.utils.FileStringTools;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TestFolderManager {
	private File path; //-- saves files here - 
	
	
	public TestFolderManager (){
		String tmpDir = System.getProperty("java.io.tmpdir");
//		System.out.println();
		String randomString = UUID.randomUUID().toString().substring(0, 8);
		path = new File (tmpDir + FileStringTools.SEP + "pintest-" + randomString);
		path.mkdirs();
		System.out.println(path);
	}
	
	/**
	 * removes dir 
	 */
	public void dispose(){
		if(path.exists() && path.isDirectory()){
			try {
				FileStringTools.removeDir(path);
			} catch (IOException ignore){			}
		}
	}

	public File getPath(){
		return path;
	}
	
	
	public static void main(String[] args) {
		new TestFolderManager();
	}

}
