package info.pinlab.ttada.restcache;

import static org.junit.Assert.*;
import info.pinlab.ttada.restcache.HttpClient43Cache.HttpClient43CacheBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpClient43CacheTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void uriTest() throws Exception {
		String target = null;
		HttpClient43Cache client =null;
		
		
		target  = "http://pinlab.info:8000/django/app-login/";
		client = new HttpClient43CacheBuilder()
		.setScheme("http")
		.setHost("pinlab.info")
		.setLoginPath("django/app-login")
		.setPort(8000)
		.build();
		assertTrue(target.equals(client.getLoginUri()));

		target  = "http://pinlab.info:80/";
		client = new HttpClient43CacheBuilder()
		.setScheme("http")
		.setHost("pinlab.info")
		.build();
		assertTrue(target.equals(client.getLoginUri()));


		
		
		System.out.println(client.getLoginUri());
	}

}
