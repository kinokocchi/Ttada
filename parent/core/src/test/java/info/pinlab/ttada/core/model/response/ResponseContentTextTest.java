package info.pinlab.ttada.core.model.response;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResponseContentTextTest {

	
	@Test
	public void test()throws Exception {
		for(ResponseContentText orig : new ResponseContentTextTestResources().getResources()){
			assertTrue(orig != null);
			// self equality
			assertTrue(orig.equals(orig));
			
			ResponseContentText copy = new ResponseContentText(
					orig.getTimeStamp(), 
					orig.getResponseTime(),
					orig.getText()
					);
			assertTrue(	copy.equals(orig));
			assertTrue(	orig.equals(copy));
			
			System.out.println(orig);
			System.out.println(copy);
			System.out.println("");
		}
	}
}

