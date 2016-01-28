package info.pinlab.ttada.view.swing.manual;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import info.pinlab.pinsound.WavClip;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.pinsound.app.AudioRecorder;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.pinsound.app.AudioRecorderView;
import info.pinlab.snd.oal.OpenAlPlayer;
import info.pinlab.snd.oal.OpenAlRecorder;
import info.pinlab.ttada.view.swing.PlayerTopPanel;
import info.pinlab.ttada.view.swing.audio.AudioRecorderBar;


public class ManualAudioRecBarTest {



	private static class DummyListener implements AudioRecorderListener{
		
		final AudioRecorderView view ;
		final AudioPlayerView pview ;
		
		 DummyListener(AudioRecorderView view1, AudioPlayerView view2){
			this.view = view1;
			this.pview = view2;
		}
		
		
		@Override
		public void reqRecStart() {
			final long recT = 500;
			final long startT = System.currentTimeMillis();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					view.setRecMaxPosInMs(recT);
					while (true){
						long t = System.currentTimeMillis() - startT;
						view.setRecPosInMs(t);
//						System.out.println(t);
						if(t > recT){
							break;
						}
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
//					view.setRecEnabled(false);
//					pview.setPlayEnabled(false);
					view.setBusyState();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					view.setReadyToRecState();
					pview.setPlayMaxLenInMs(recT);
					pview.setReadyToPlayState();
				}
			}).start();
		};

		@Override
		public void reqRecStop() {
			
		}
	}
	
	
	static void setDummyRecorder(AudioRecorderBar bar){
		bar.setRecActionListener(new DummyListener(bar, bar));
	}
	
	
	
	static void setRealRecorder(final AudioRecorderBar bar){
		final AudioPlayer player = new AudioPlayer();
		final OpenAlPlayer playerDevice = new OpenAlPlayer();
		player.setPlayerDevice(playerDevice);
		player.setAudioPlayerView(bar);
		bar.setPlayActionListener(player);
		player.setAfterPlayHook(new Runnable() {
			@Override
			public void run() {
//				bar.setReadyToRecState();
			}
		});

		
		final AudioRecorder recorder = new AudioRecorder();
		recorder.setMaxRecLenInMs(1000);
		OpenAlRecorder recorderDevice =  OpenAlRecorder.getInstance();
		recorderDevice.setRecFormat(1, 8000, 8);
		recorder.setRecorderDevice(recorderDevice);
		recorder.setAudioRecorderView(bar);
		recorder.setAfterRecHook(new Runnable() {
			@Override
			public void run() {
				WavClip wav = recorder.getWavClip();
				playerDevice.initWav(wav);
				bar.setPlayMaxLenInMs(wav.getDurInMs());
				bar.setReadyToPlayState();
			}
		});
		
		bar.setRecActionListener(recorder);
	}
	
	
	public static void main(String [] args) throws Exception{
		PlayerTopPanel.setNimbusLF();

		final AudioRecorderBar bar = new AudioRecorderBar();
		setRealRecorder(bar);
//		setDummyRecorder(bar);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(300, 80);
				
				frame.getContentPane().add(bar);
				frame.setVisible(true);
			}
		});
	}
	
}
