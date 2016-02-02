package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualConnectionTest {

	
	
	
	
	public static void main(String[] args) {
		
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		cli.run(args);
		
	}

}
