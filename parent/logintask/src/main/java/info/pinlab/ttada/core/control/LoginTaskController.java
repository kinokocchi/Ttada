package info.pinlab.ttada.core.control;

import info.pinlab.ttada.core.model.response.ResponseContent;

public class LoginTaskController extends AbstractTaskController{

	
	
	
	
	@Override
	public void enrollResponse(final ResponseContent respContent){
		System.out.println("Enrolling!");

		
		
		
		if(super.sessionController != null){
			super.sessionController.setUserId("");
		}
	}
	
	public static void main(String[] args) {

	}
}
