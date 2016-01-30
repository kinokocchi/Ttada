package info.pinlab.ttada.view.swing.audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import info.pinlab.pinsound.app.AudioPlayer;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.ttada.view.swing.TopPanel;
import info.pinlab.ttada.view.swing.ResourceLoader;
import info.pinlab.ttada.view.swing.ResourceLoader.IconType;
import info.pinlab.ttada.view.swing.ShortcutConsumer;


@SuppressWarnings("serial")
public class AudioPlayerButton extends JToggleButton 
					implements AudioPlayerView, ShortcutConsumer {
	private static Icon playIcon;
//	private static Icon stopIcon;
	private static Icon pauseIcon;
	
	static final Set<Integer> shortcuts = new HashSet<Integer>();
	
	static{
		playIcon = ResourceLoader.getIcon(IconType.PLAY, 48);
		pauseIcon = ResourceLoader.getIcon(IconType.PAUSE, 48);
		shortcuts.add(KeyEvent.VK_SPACE);
		shortcuts.add(KeyEvent.CTRL_DOWN_MASK|KeyEvent.VK_SPACE);
		shortcuts.add(KeyEvent.META_DOWN_MASK|KeyEvent.VK_SPACE);
	}
	
	
	private AudioPlayerListener listener = null;
	
	public AudioPlayerButton(){
		setFocusable(false);
		setIcon(playIcon);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e){
//				System.out.println("PUSHED " + listener);
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
		listener = l;
	}

	@Override
	public void setPlayEnabled(boolean b){
		this.setEnabled(b);
	}
	
	public static void main(String[ ]args) throws Exception{
		TopPanel.setNimbusLF();
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

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode()|e.getModifiersEx(); 
		if(keyCode==KeyEvent.VK_SPACE
			|| keyCode==(KeyEvent.CTRL_DOWN_MASK|KeyEvent.VK_SPACE)
			|| keyCode==(KeyEvent.META_DOWN_MASK|KeyEvent.VK_SPACE)
			){
			this.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public Set<Integer> getShortcutKeys() {
		return shortcuts;
	}

}
