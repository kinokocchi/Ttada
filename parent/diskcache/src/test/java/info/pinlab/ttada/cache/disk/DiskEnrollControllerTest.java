package info.pinlab.ttada.cache.disk;

import static org.junit.Assert.*;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DiskEnrollControllerTest {

	static TestFolderManager folder;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		folder = new TestFolderManager(); 

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		folder.dispose();
	}

	
	@Before
	public void setUp() throws Exception {
		
	}
	@After
	public void tearDown() throws Exception {
	}

	
	
	
	@Test
	public void testConstructor() throws InterruptedException{
		DiskEnrollController controller = new DiskEnrollController();
		
		LocalSaveHook hookForTxt = new LocalSaveHookForTxtResponse(folder.getPath());
		LocalSaveHook hookForWav = new LocalSaveHookForWavResponse(folder.getPath());
		controller.addSaveHook(hookForTxt);
		controller.addSaveHook(hookForWav);
		
		assertTrue(controller.getSaveHooks().size() == 2);
		
		
		ResponseContentText txt1 = new ResponseContentText(100, 0, "first answer", "#1");
		Response resp1 = new Response(new ResponseHeaderBuilder().build(), txt1);
		ResponseContentText txt2 = new ResponseContentText(200, 0, "second answer", "#2");
		Response resp2 = new Response(new ResponseHeaderBuilder().build(), txt2);
		
		controller.getResponseSet().add(resp1);
		controller.getResponseSet().add(resp2);
		controller.start();

		
		Thread.sleep(2000);

		assertTrue(hookForTxt.getRootPath() != null);
		assertTrue(hookForTxt.getRootPath().isFile());
		assertTrue(hookForTxt.getRootPath().length() > 0);
		
		
		
		
	}
	
}
