package info.pinlab.ttada.session.manual;

import info.pinlab.ttada.session.Registry.Key;
import info.pinlab.ttada.session.app.CLI;

public class ManualConnectionTest {


	public static void main(String[] args) {

		CLI cli = new CLI();
		
		
		cli.addRegItem(Key.USER, "connection_test");
		cli.addRegItem(Key.LOCAL_AVAILABLE, false);
		cli.addRegItem(Key.REMOTE_AVAILABLE, true);
		
		cli.addRegItem(Key.REMOTE_IP, "133.30.246.210");
		cli.addRegItem(Key.REMOTE_HOST, "133.30.246.210");
		cli.addRegItem(Key.REMOTE_PORT, "80");
		cli.addRegItem(Key.REMOTE_PING_PATH, "/django/pretest/app-ping/");
		cli.addRegItem(Key.REMOTE_LOGIN_PATH, "/django/pretest/app-login/");
		cli.addRegItem(Key.REMOTE_LOGIN_ID,  "pinplayer-app");
		cli.addRegItem(Key.REMOTE_LOGIN_PWD, "scret passwrd");
		
		cli.run(args);

	}

}
