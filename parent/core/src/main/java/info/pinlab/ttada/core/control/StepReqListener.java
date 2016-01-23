package info.pinlab.ttada.core.control;

public interface StepReqListener {
	//-- step requests --//
	public void reqNext(); 		//-- req from system (e.g. timed task)
	public void reqPrev();
	public void reqNextByUsr();	//-- req by user from GUI
	public void reqPrevByUsr();
	
}
