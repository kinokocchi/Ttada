package info.pinlab.ttada.cache.disk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.utils.FileStringTools;

public class LocalSaveHookForTxtResponseTest {
	static TestFolderManager folder ;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		folder = new TestFolderManager(); 
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		folder.dispose();
	}
	
	
	@Test
	public void test() throws IOException{
		LocalSaveHookForTxtResponse hook = new LocalSaveHookForTxtResponse(folder.getPath());
		

		ResponseContentText txt1 = new ResponseContentText(100, 0, "first answer", "1::");
		Response resp1 = new Response(new ResponseHeaderBuilder().build(), txt1);
		hook.save(resp1);
		ResponseContentText txt2 = new ResponseContentText(200, 0, "second answer", "2::hah");
		Response resp2 = new Response(new ResponseHeaderBuilder().build(), txt2);
		hook.save(resp2);
		
		assertTrue(hook.getRootPath() != null);
		assertTrue(hook.getRootPath().exists());
		assertTrue(hook.getRootPath().isFile());
		
		
		String inFileTxt = FileStringTools.getFileAsString(hook.getRootPath());
		assertFalse(inFileTxt.isEmpty());
		
		String [] lines = inFileTxt.split("\\r?\\n");
		assertTrue(lines.length == 3);
		
	}
	

}
