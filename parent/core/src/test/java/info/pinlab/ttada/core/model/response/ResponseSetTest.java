package info.pinlab.ttada.core.model.response;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.ttada.core.model.TestResources;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;

public class ResponseSetTest {

	static List<Response> resps;
	static Random rand ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		BasicConfigurator.resetConfiguration();
//		BasicConfigurator.configure();
		
		TestResources.RESP_SET_SIZE = 80;
		TestResources.init();
		resps = TestResources.getResoursesFor(Response.class);
		
		rand = new Random(System.currentTimeMillis());
	}

	
	@Test 
	public void testSize(){
		ResponseSet rset = new ResponseSet();
		for(int i = 0 ; i < TestResources.RESP_SET_SIZE; i++){
//			System.out.println(i);
			assertTrue(rset.size()==i);
			assertTrue(rset.getUnrpocessedN()==i);
			Response resp = resps.get(i);
//			System.out.println(resp.getHeader().timestamp + "\t" + resp.getHeader().taskId + "\t" + resp.equals(prev)); 
			rset.add(resp);
		}
	}
	
	
	

	@Test
	public void testMultiAdd(){
		ResponseSet rset = new ResponseSet();
		
		//-- before doing anything --//
		assertTrue(rset.next()==null);
		assertTrue(rset.size()==0);
		assertTrue(rset.getUnrpocessedN()==0);
		
		final int randIx1 = rand.nextInt(resps.size());
		final Response resp1 = resps.get(randIx1);
		rset.add(resp1);
		final int sz1 = rset.size();
		assertTrue(sz1==1);
		assertTrue(rset.getUnrpocessedN()==1);

		//-- adding the same thing again --//
		rset.add(resp1);
		assertTrue(sz1==1);
		assertTrue(rset.getUnrpocessedN()==1);

		
		//-- ADD ONE MORE RESPONSE
		int randIx2 = rand.nextInt(resps.size());
		while(randIx2 == randIx1){
			randIx2 = rand.nextInt(resps.size());
		}
		
		final Response resp2 = resps.get(randIx2);
		rset.add(resp2);
		final int sz2 = rset.size();
		assertTrue(sz2==2);
		assertTrue(rset.getUnrpocessedN()==2);
		
	}
	
	
	@Test
	public void testNext(){
		ResponseSet rset = new ResponseSet();
		//-- before doing anything --//
		assertTrue(rset.next()==null);
		assertTrue(rset.size()==0);
		assertTrue(rset.getUnrpocessedN()==0);
		for(Response resp : resps){ //-- fill in
			rset.add(resp);
		}
		assertTrue(rset.size()==TestResources.RESP_SET_SIZE);
		assertTrue(rset.getUnrpocessedN()==TestResources.RESP_SET_SIZE);
		
		for(int i=1; i <= TestResources.RESP_SET_SIZE; i++){
			final Response resp = rset.next();
			assertTrue(rset.getUnrpocessedN()==TestResources.RESP_SET_SIZE-i);
			assertTrue(rset.size()==TestResources.RESP_SET_SIZE); //-- size should not change!
			assertTrue(resp!=null);
		}
		assertTrue(rset.next()==null); //-- this should be null : already finished iterating
		assertTrue(rset.next()==null); //-- this should be null : already finished iterating
		
		rset.add(resps.get(rand.nextInt(resps.size())));
		assertTrue(rset.size()==TestResources.RESP_SET_SIZE);//-- shold be the same as this response was already added!
		assertTrue(rset.getUnrpocessedN()==0);//-- shold be the same as this response was already added!
	}
	
	
	
	@Test
	public void multiAddTest(){
		ResponseSet rset = new ResponseSet();
		
		ResponseHeader hdr = new ResponseHeader(0, "fake brief",  0, 0, 0, "fakse task type", "fake task brief", "fake session id", "fake user id", 0, 0);
		Response resp = new Response(hdr, new ResponseContentEmpty(System.currentTimeMillis(), 10));
		
		rset.add(resp);
		int sz1 = rset.size();
		assertTrue(sz1==1);
		
		rset.add(resp);  //-- can't add exactly the same thing twice!
		int sz2 = rset.size();
		
		assertTrue(sz1==sz2);
	}
	
	
	@Test
	public void testAttemptCnt(){
		ResponseSet rset = new ResponseSet();
		List<Task> tasks = TestResources.getResoursesFor(Task.class);
		Task task0 = tasks.get(0);
		Task task1 = tasks.get(1);
		
		TaskInstance taski0 = new TaskInstance(task0, 0, 0);
		TaskInstance taski1 = new TaskInstance(task1, 0, 1);
		
		
		ResponseContent cont1 = resps.get(0).getContent();
		ResponseContent cont2 = resps.get(1).getContent();
		ResponseContent cont3 = resps.get(2).getContent();

		ResponseHeader hdr0 = new ResponseHeaderBuilder()
				.setTaskInstId(taski0.hashCode()).build();
		ResponseHeader hdr1 = new ResponseHeaderBuilder()
				.setTaskInstId(taski1.hashCode()).build();
		
		rset.add(new Response(hdr0, cont1));
		rset.add(new Response(hdr0, cont2));
		rset.add(new Response(hdr0, cont3));
		rset.add(new Response(hdr1, cont1));
		//-- this should be ignored
		rset.add(new Response(hdr1, cont1));
		assertTrue(rset.size() == 4);
		
		assertTrue(rset.getAttemptCntForTaskInstance(taski0) == 3);
		assertTrue(rset.getAttemptCntForTaskInstance(taski1) == 1);
		
//		Response resp_a_3 = new Response(hdr1, cont3);
//		Response resp2 = new Response(hdr2, cont1);
//		rset.add(resp2);
//		System.out.println(tasks.size());
	}
	
}
