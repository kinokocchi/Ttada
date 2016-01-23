package info.pinlab.ttada.core.model.response;

import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.StringTestResource;

import java.util.Random;

public class ResponseContentTextTestResources extends AbstractTestResourceProvider<ResponseContentText>{
	
	
	public ResponseContentTextTestResources(){
		super(ResponseContentText.class);
		
		Random rand = new Random();
		
		int i = 0;
		for(String label : new StringTestResource().getResources()){
			ResponseContentText resp = new ResponseContentText(
					System.currentTimeMillis()+10+rand.nextInt(), 
					rand.nextInt(), 
					label,
					"hidden_txt_" + i++
					);
			super.addResource(resp);
		}
		
	}
	
	
	
}



