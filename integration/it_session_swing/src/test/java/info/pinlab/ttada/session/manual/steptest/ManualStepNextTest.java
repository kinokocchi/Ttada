package info.pinlab.ttada.session.manual.steptest;

import org.apache.log4j.BasicConfigurator;

import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.task.TaskSet;
import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;


/**
 * Should not step with NO response.
 * Should not step by response.
 * 
 */
public class ManualStepNextTest {

	static TaskSet getTaskSet(){
		TaskSet tset = new TaskSet();
		
		for(int i = 0 ; i < 4 ; i++){
			MultichoiceTask task = new MultichoiceTask()
					.addChoice(i + " A")
					.addChoice(i + " B");
			tset.add(task);
		}		
		return tset;
	}
	
	public static void main(String[] args) {
	
		BasicConfigurator.configure();
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, false);
		
		cli.getSessionFactory().setTaskSet(getTaskSet());
		cli.run(args);
	}

	
}
