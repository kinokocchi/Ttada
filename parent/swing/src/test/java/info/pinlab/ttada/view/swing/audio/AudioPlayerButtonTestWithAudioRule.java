package info.pinlab.ttada.view.swing.audio;

import static org.junit.Assert.assertTrue;
import info.pinlab.ttada.core.control.InfoTaskController;
import info.pinlab.ttada.core.control.PlayerController;
import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.control.TaskControllerWithAudio;
import info.pinlab.ttada.core.model.rule.AudioRule;
import info.pinlab.ttada.core.model.rule.AudioRule.AudioRuleBuilder;
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

public class AudioPlayerButtonTestWithAudioRule {
	static WavClip wav;
	static PlayerDeviceController playerDev = null;
	AudioPlayer player;
	AudioPlayerButton view;
	TaskController dummyController;
	TaskControllerWithAudio controller;
	AudioRule arule;
	
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

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception{
		arule = new AudioRuleBuilder()
		.setMaxPlayN(-1)
		.canPlay(true)
		.canPause(false)
		.build()
		;
		player = new AudioPlayer();
		view = new AudioPlayerButton();

		dummyController = new InfoTaskController();
		controller = new TaskControllerWithAudio(dummyController);
		
		PlayerController playerToken = new PlayerController(player);
		playerToken.setAudioPlayerView(view);
		playerToken.setAudioRule(arule);

		controller.addPlayerToken(playerToken);
	}

//	@After
//	public void tearDown() throws Exception {
//	}

	@Test
	public void dummy() throws InterruptedException{
		//-- NOTE --//
		//-- comment out @Test on "testAudioButton()" when not function testing!! 
		//-- 
	}
	
	
	@Test
	public void testAudioButton() throws Exception{
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
		Thread.sleep(15000);
	}
}
