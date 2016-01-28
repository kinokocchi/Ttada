package info.pinlab.ttada.restcache;

import static org.junit.Assert.*;

import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.ttada.core.ser.SimpleJsonSerializer;
import info.pinlab.ttada.gson.SimpleGsonSerializerFactory;
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

	
	
	
//	@Test
	public void loginAppTest() throws Exception {
		SimpleJsonSerializer gson = new SimpleGsonSerializerFactory().build();
		
		HttpClient43Cache client = new HttpClient43CacheBuilder()
				.setScheme("http")
				.setHost("pinlab.info")
				.setLoginPath("django/app-login")
				.setLoginId("pinplayer-app")
				.setLoginPwd("wrong pwd")
				
				.setRestRoot("django/rest/")
//				.setLoginPwd("app pwd hold somewhere else")

				.setSerializer(gson)
				.build();
		
		Exception err = client.loginApp();
		if(err ==null){
			System.out.println("Login OK!");
		}else{
			System.out.println("Login ERROR!");
			System.out.println(err);
			return;
		}
		ResponseHeader hdr = new ResponseHeaderBuilder().build();
		ResponseContent content = new ResponseContentText(System.currentTimeMillis(), 1234, "HttpClient 4.3.x testing");
		Response resp = new Response(hdr, content);
		
		client.put(resp, Response.class);
		
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
