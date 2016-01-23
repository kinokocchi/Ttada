package info.pinlab.ttada.session.manual;

import org.apache.log4j.BasicConfigurator;

import info.pinlab.ttada.session.app.CLI;

public class ManualMinimalCliTest {

	public static void main(String[] args) throws Exception{
		BasicConfigurator.configure();
		CLI.main();
	}
}

