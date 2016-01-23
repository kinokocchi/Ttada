package info.pinlab.ttada.view.swing.audio;

import static org.junit.Assert.assertTrue;
import info.pinlab.pinsound.PlayerDeviceController;
import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.snd.oal.OpenAlPlayer;

import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AudioPlayerButtonTest {
	static WavClip wav;
	static PlayerDeviceController playerDev = null;
	static AudioPlayer player;
	static AudioPlayerButton view;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				//					logger.info("Look and feel " + info.getName());
				UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		}
		String path = "sample.wav";
		InputStream is = AudioPlayerButtonTest.class.getResourceAsStream(path);
		assertTrue( "Can't read wav file!", is!=null);
		wav = new WavClip(is);
		
	}

	@Before
	public void setUp() throws Exception{
		player = new AudioPlayer();
		
		view = new AudioPlayerButton();
		view.setPlayActionListener(player);
		
		player.setAudioPlayerView(view);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(playerDev!=null){
			OpenAlPlayer.disposeAll();
		}
	}

//	@Test
	public void dummy() throws InterruptedException{
		//-- NOTE --//
		//-- comment out @Test on "testAudioButton()" when not function testing!! 
		//-- 
	}
	
	
	
//	public static void main(String [] ignore) throws Exception{
//	@Test
	public void manualTestAudioButton() throws Exception{
		if(playerDev==null){
			playerDev = new OpenAlPlayer();
		}
		player.setPlayerDevice(playerDev);
		player.setAudio(wav);
		
		SwingUtilities.invokeAndWait(new Runnable(){
			@Override
			public void run() {
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				panel.add(view);
				frame.getContentPane().add(panel);
				frame.setSize(500,150);
				frame.setVisible(true);
			}
		});
		Thread.sleep(3000);
	}
}
