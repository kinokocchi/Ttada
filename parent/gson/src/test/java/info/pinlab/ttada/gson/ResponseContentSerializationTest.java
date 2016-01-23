package info.pinlab.ttada.gson;

import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.response.ResponseContentEmptyTestResource;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseContentTextTestResources;

import org.junit.Test;

public class ResponseContentSerializationTest {

	@Test
	public void ResponseContentTextTest(){
		for(ResponseContentText orig : new ResponseContentTextTestResources().getResources()){
			SerializerUtil.serializeAndCompare(orig, ResponseContentText.class);
		}
	}

	@Test
	public void ResponseContentEmtpyTest(){
		for(ResponseContentEmpty orig : new ResponseContentEmptyTestResource().getResources()){
			SerializerUtil.serializeAndCompare(orig, ResponseContentEmpty.class);
		}
	}
}


