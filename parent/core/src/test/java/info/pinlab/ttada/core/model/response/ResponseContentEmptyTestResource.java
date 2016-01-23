package info.pinlab.ttada.core.model.response;

import info.pinlab.ttada.core.AbstractTestResourceProvider;

import java.util.Random;


public class ResponseContentEmptyTestResource extends AbstractTestResourceProvider<ResponseContentEmpty>{

	
	public ResponseContentEmptyTestResource(){
		super(ResponseContentEmpty.class);
		Random rand = new Random();
		
		for(int i = 0 ; i < 10 ; i++){
			super.addResource(
					new ResponseContentEmpty(
							System.currentTimeMillis()+1+rand.nextInt(), 
							rand.nextInt() 
							)
					);
		}
	}
	
}

