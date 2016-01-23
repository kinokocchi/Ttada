package info.pinlab.ttada.core.model.response;

import static org.junit.Assert.*;

import java.util.List;

import info.pinlab.ttada.core.model.TestResources;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResponseContentTest  {
	static List<ResponseContent> contents;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestResources.init();
		contents = TestResources.getResoursesFor(ResponseContent.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testEquality(){
		int size = contents.size();
		for(int i=0 ; i < size ; i++){
			for(int j=0 ; j < size ; j++){
				ResponseContent a = contents.get(i);
				ResponseContent b = contents.get(j);
				if(i!=j){
					assertFalse(a.equals(b));
					assertFalse(b.equals(a));
				}else{
					assertTrue(a.equals(b));
				}
			}
		}
		
	}
	
	

}
