package info.pinlab.ttada.core.model.task;

public class InfoTask extends AbstractTask{

	public InfoTask(){
		super.isResponsible = false;
	}

	public InfoTask(String text){
		super.isResponsible = false;
		super.addDisplay(text);
	}

}
