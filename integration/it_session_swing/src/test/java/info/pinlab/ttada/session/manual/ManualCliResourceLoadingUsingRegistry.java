package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualCliResourceLoadingUsingRegistry {

	public static void main(String[] args) throws Exception{
		CLI cli = new CLI();
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		cli.addRegItem(Key.TASK_SET_FILE_TYPE, "csv");
		cli.addRegItem(Key.TASK_SET_CSV, "resource-loading-test.csv");
		cli.addRegItem(Key.TASK_SET_RESOURCELOADER_CLASS, "info.pinlab.ttada.session.manual.ManualCliResourceLoadingUsingRegistry");
		cli.run(args);
	}
}
