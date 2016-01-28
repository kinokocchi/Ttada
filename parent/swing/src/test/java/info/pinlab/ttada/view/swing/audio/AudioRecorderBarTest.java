package info.pinlab.ttada.view.swing.audio;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.pinsound.PlayerDeviceController;
import info.pinlab.pinsound.RecorderDeviceController;
import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioRecorder;
import info.pinlab.snd.oal.OpenAlPlayer;
import info.pinlab.snd.oal.OpenAlRecorder;
import info.pinlab.ttada.view.swing.PlayerTopPanel;

public class AudioRecorderBarTest {
	
	static PlayerDeviceController playerDev = null;
	static RecorderDeviceController recorderDev = null;
	
	AudioRecorder recorder ;
	AudioPlayer player;
	
	AudioRecorderBar bar;
	
	
	static boolean IS_LWJGL_WORKING = false;
	
	static void IF_LWJGL_IS_WORKING(){
		playerDev = new OpenAlPlayer();
		recorderDev = OpenAlRecorder.getInstance();
		recorderDev.setRecFormat(1, 8000, 16);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTopPanel.setNimbusLF();
		if(IS_LWJGL_WORKING){
			IF_LWJGL_IS_WORKING();
		}
	}

	
	@Before
	public void setUp() throws Exception{
		recorder = new AudioRecorder();
		player = new AudioPlayer();
		
		bar = new AudioRecorderBar();
		recorder.setRecorderDevice(recorderDev);
		recorder.setAudioRecorderView(bar);
		
		player.setPlayerDevice(playerDev);
		player.setAudioPlayerView(bar);

		bar.setPlayActionListener(player);
		bar.setRecActionListener(recorder);
		
		
		recorder.setAfterRecHook(new Runnable() {
			@Override
			public void run() {
				WavClip wav = recorder.getWavClip();
				player.setAudio(wav);
			}
		});
		player.setAfterPlayHook(new Runnable() {
			@Override
			public void run(){
				bar.setReadyToRecState();
			}
		});
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(!IS_LWJGL_WORKING){
			return;
		}

		if(recorderDev==null){
			OpenAlPlayer.disposeAll();
		}else{
			OpenAlRecorder.disposeAll();
			OpenAlPlayer.disposeAll();
		}
	}

	@Test
	public void dummy(){
		//-- NOTE --//
		//-- comment out @Test on "testRecordBarGui()" when not function testing!! 
		//-- 
	}
	
//	@Test
	public void testRecordBarGui() throws Exception{
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setSize(300, 80);
				
				frame.getContentPane().add(bar);
				frame.setVisible(true);
			}
		});
		
		Thread.sleep(5000);
	}

}
