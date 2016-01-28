package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;


public class ManualExternalResourceLoaderTest {

	public static void main(String[] args) throws Exception{
		CLI cli = new CLI();
		//-- these defaults can be overwritten by runtime arguments
		cli.addRegItem(Key.USER, "Experimenter");
		cli.addRegItem(Key.LOCAL_AVAILABLE, true);
		cli.addRegItem(Key.TASK_SET_FILE_TYPE, "csv");
//	info.pinlab.player.taskset.file.csv=main_v02.csv
		
		//IMPORTANT: uncomment   : for developing / testing!
		//           comment out : for deploy
		//           for real runs the task sets are defined by command line arg 
		cli.addRegItem(Key.TASK_SET_CSV, "practice.csv");
		cli.addRegItem(Key.TASK_SET_CSV, "/home/kinoko/tmp/pinplayer-localtest/test.csv");
//		cli.addRegItem(Key.TASK_SET_CSV, "main_v01.csv");
		
		
//		cli.addRegItem(Key.TASK_SET_RESOURCELOADER_CLASS, "info.pinlab.ttada.release.exp_sibilants.Experiment");
		
		cli.run(args);
	}

}
