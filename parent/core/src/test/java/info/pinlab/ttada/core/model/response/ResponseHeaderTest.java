package info.pinlab.ttada.core.model.response;

import  static org.junit.Assert.assertTrue; 

import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;

import org.junit.Before;
import org.junit.Test;


public class ResponseHeaderTest{
	
	
//	@BeforeClass
//	public static void setUpClass(){
//
//	}
//	
	@Before
	public void setUp(){

	}
	

	@Test
	public void testFileName(){
		ResponseHeader hdr = new ResponseHeaderBuilder()
		.setSessionId("sess")
		.setTaskIx(2)
		.setTaskId(8)
		.setAttemptN(1)
		.setTaskSetId(22332)
		.setTaskType("MultichoiceTask")
		.setTimeStamp(System.currentTimeMillis())
		.setUsrId("anon")
		.build();
		
		assertTrue(!hdr.toString().isEmpty());
		System.out.println(hdr);
		
//		ResponseHeader hdr = TestResources.respHeaders.get(0);
//		String fname = hdr.toString();
//		System.out.println(fname);
//		assertTrue(fname!=null);
//		assertFalse(fname.isEmpty());
	}
	
	
	
	

}
