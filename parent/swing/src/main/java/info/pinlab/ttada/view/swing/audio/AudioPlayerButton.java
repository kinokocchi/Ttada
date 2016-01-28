package info.pinlab.ttada.view.swing.audio;

import info.pinlab.ttada.view.swing.PlayerTopPanel;
import info.pinlab.ttada.view.swing.ResourceLoader;
import info.pinlab.ttada.view.swing.ResourceLoader.IconType;
import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class AudioPlayerButton extends JToggleButton implements AudioPlayerView{
	private static Icon playIcon;
//	private static Icon stopIcon;
	private static Icon pauseIcon;
	
	static{
		playIcon = ResourceLoader.getIcon(IconType.PLAY, 48);
		pauseIcon = ResourceLoader.getIcon(IconType.PAUSE, 48);
	}
	
	
	private AudioPlayerListener listener = null;
	
	public AudioPlayerButton(){
		setIcon(playIcon);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e){
				System.out.println("PUSHED " + listener);
				if(listener!=null){
					listener.reqPauseToggle();
				}
			}
		});
	}
	
	@Override
	public void setPlayingState() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setSelected(true);
				setIcon(pauseIcon);
			}
		});
	}

	@Override
	public void setReadyToPlayState() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setSelected(false);
				setIcon(playIcon);
			}
		});
	}

	@Override
	public void setPlayMaxLenInMs(long ms) {} //--ignore
	@Override
	public void setPlayPosInMs(long ms) {} //--ignore

	@Override
	public void setEnabled(boolean b) {
		setEnabled(b);
	}

	@Override
	public void setPlayActionListener(AudioPlayerListener l) {
		System.out.println("LISTENER " + l);
		listener = l;
	}

	@Override
	public void setPlayEnabled(boolean b){
		this.setEnabled(b);
	}
	
	public static void main(String[ ]args) throws Exception{
		PlayerTopPanel.setNimbusLF();
		final AudioPlayerButton btn = new AudioPlayerButton();
		AudioPlayer player = new AudioPlayer();
//		player.setPlayerDevice(playerDev);
//		player.setAudio(wav);
		
//		view = new AudioPlayerButton();
		btn.setPlayActionListener(player);
		
		player.setAudioPlayerView(btn);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				panel.add(btn);
				frame.getContentPane().add(panel);
				frame.setSize(500,150);
				frame.setVisible(true);
			}
		});
	}

}
