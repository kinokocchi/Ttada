package info.pinlab.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WindowsUtils {

	private static final String REGQUERY_UTIL = "reg query ";
	private static final String REGSTR_TOKEN = "REG_SZ";
	private static final String DESKTOP_FOLDER_CMD = REGQUERY_UTIL 
			+ "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\" 
			+ "Explorer\\Shell Folders\" /v DESKTOP";

	private WindowsUtils() {}


	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw;

		StreamReader(InputStream is) {
			this.is = is;
			sw = new StringWriter();
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			}
			catch (IOException e) { ; }
		}
		String getResult() {
			return sw.toString();
		}
	}


	public static String getCurrentUserDesktopPath() {
		try {
			Process process = Runtime.getRuntime().exec(DESKTOP_FOLDER_CMD);
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();
			String result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);

			if (p == -1) return null;
			String path = result.substring(p + REGSTR_TOKEN.length()).trim();
//			path = new String(path.getBytes("SJIS"),  Charset.forName("MS932"));
			return path;
		}
		catch (Exception e) {
			return null;
		}
	}

//	public static void main(String[] args) {
//		System.out.println(System.getProperty("os.name"));
//	}

}
