package info.pinlab.ttada.core.model.task;

import info.pinlab.ttada.core.AbstractTestResourceProvider;
import info.pinlab.ttada.core.StringTestResource;

public class InfoTaskTestResource extends AbstractTestResourceProvider<InfoTask>{

	public InfoTaskTestResource() {
		super(InfoTask.class);
		
		for (String txt : new StringTestResource().getResources()){
			super.addResource(new InfoTask(txt));
		}
	}
}

