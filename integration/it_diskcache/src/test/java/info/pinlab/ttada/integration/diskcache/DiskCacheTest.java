package info.pinlab.ttada.integration.diskcache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.cache.disk.DiskCache;
import info.pinlab.ttada.cache.disk.DiskCache.DiskCacheBuilder;
import info.pinlab.ttada.core.cache.LocalCache;
import info.pinlab.ttada.core.cache.Pointer;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseContentTextTestResources;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.ttada.core.model.response.ResponseHeaderTestResource;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.InfoTaskTestResource;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.core.ser.SimpleJsonSerializerFactory;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
import info.pinlab.utils.FileStringTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DiskCacheTest {
	public static Logger logger = Logger.getLogger(DiskCacheTest.class);
	static SimpleJsonSerializer gson;
	static DiskCache localCache;
	static boolean isTmpDirCreated = false;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		//-- setup json Adapter --//
		
		SimpleJsonSerializerFactory factory = new SimpleGsonSerializerFactory();
//		facotry.addClassTag(aTag, forThisClass);
		gson = factory.build();
		
		
		//-- setup json Cache --//
		localCache = new DiskCacheBuilder ()
		.setJsonAdapter(gson)
//		.isSaveRespTextOnTheFly(false)
		.build();
		isTmpDirCreated = true;		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//-- clean up folder --//

		final File dir = localCache.getRootDir();
		if(isTmpDirCreated && dir.exists() && dir.isDirectory()){
			logger.info("Deleting cache root '" + dir.getAbsolutePath());
			try{
				FileStringTools.removeDir(dir);
				isTmpDirCreated = false;
			}catch(IOException e){
				logger.error("Cannot delete cache dir '" + dir.getAbsolutePath() + "'");
				isTmpDirCreated = true;
			}
		}
		
		//-- make sure Cache is deleted
		assertFalse("Couldn't remove '" + dir.exists() + "'", isTmpDirCreated);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void cacheDataWriteTest(){
		Class<ResponseContent> clazz = ResponseContent.class;

		List<File> filesToDelete = new ArrayList<File>();
		for(ResponseContentText orig : new ResponseContentTextTestResources().getResources()){
			Pointer ptr = localCache.put(orig, clazz);
			String fileName = localCache.getRootDir() + FileStringTools.SEP + clazz.getSimpleName() + FileStringTools.SEP  + ptr.getInt() + ".gz";
			File file = new File(fileName);
			assertTrue("Disk data was not saved! '" + file.getAbsolutePath() +"'", file.exists());
			filesToDelete.add(file);
		}
		
		for (File file : filesToDelete){
			if(file.delete()){
				logger.info("Remove test resource '" + file.getAbsolutePath() +"'");
			}else{
				logger.warn("Couldn't remove test resource '" + file.getAbsolutePath() +"'");
			}
		}
	}

	
	
	@Test
	public void createRootDirTest() throws IOException{
		File desiredDir = new File("/var/tmp/pintest/");
		
		LocalCache cache =  new DiskCacheBuilder ()
		.setJsonAdapter(gson)
		.setDiskCacheRootPath(desiredDir)
		.build();
		
		File createdDir = cache.getRootDir();
		assertTrue("The cache root dir is null! Was not set correctly!" , createdDir != null);
		assertTrue(desiredDir.getAbsolutePath().equals(createdDir.getAbsolutePath()));
		assertTrue("Cache dir was not created '" + desiredDir +"'", createdDir.exists());
		assertTrue("Cache dir is not a directory  '" + createdDir +"'", createdDir.isDirectory());
		FileStringTools.removeDir(createdDir);
	}
	

	
	@Test
	public void readWriteLocalCacheTest(){
		List<InfoTask> tasks = new ArrayList<InfoTask>();
		List<Pointer> ptrs = new ArrayList<Pointer>();

		for(InfoTask task : new InfoTaskTestResource().getResources()){
			Pointer ptr = localCache.put(task, Task.class);
			tasks.add(task);
			ptrs.add(ptr);
		}
		assertTrue(tasks.size()==ptrs.size());
		for(int i = 0 ; i < tasks.size() ; i++){
			Task orig = tasks.get(i);
			Task copy = localCache.get(ptrs.get(i), Task.class);
			
			assertTrue("Two tasks are not identical! '" + orig + "'", orig.equals(copy));
		}
	}
	
	
	@Test 
	public void testOnTheFlyRespWriting() throws Exception{
		File desiredDir = new File("/var/tmp/pintest-ontheflytext/");
		LocalCache cache =  new DiskCacheBuilder ()
							.setJsonAdapter(gson)
							.setDiskCacheRootPath(desiredDir)
//							.isSaveRespTextOnTheFly(true)
							.build();
		
		List<ResponseHeader> headers = new ResponseHeaderTestResource().getResources();
		int i = 0;
		for(ResponseContent resp : new ResponseContentTextTestResources().getResources()){
			cache.put(new Response(headers.get(i), resp), Response.class);
			i++; if (i == headers.size()) i= 0;
		}
//		File respFile = cache.getTextResponseFile();
//		assertTrue(respFile.exists());
		
//		((DiskCache)cache).dispose();
		
		FileStringTools.removeDir(desiredDir);
	}
	
}

