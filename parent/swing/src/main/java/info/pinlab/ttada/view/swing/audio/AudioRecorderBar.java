package info.pinlab.ttada.view.swing.audio;

import info.pinlab.ttada.view.swing.GbcFactory;
import info.pinlab.ttada.view.swing.ResourceLoader;
import info.pinlab.ttada.view.swing.ResourceLoader.IconType;
import info.pinlab.pinsound.app.AudioPlayerListener;
import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.pinsound.app.AudioRecorderListener;
import info.pinlab.pinsound.app.AudioRecorderView;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class AudioRecorderBar extends JPanel implements AudioRecorderView, AudioPlayerView{
	public static Logger LOG = LoggerFactory.getLogger(AudioRecorderBar.class);
	
	private final Icon recIcon;
	private final Icon playIcon;
	private final Icon stopIcon;
	private final Icon pauseIcon;

	private final JToggleButton recBtn;
	private final JToggleButton playBtn;
	
	private final AudioProgressBar playRecBar;
	private Font audioBarFont = null; 
	int iconSize = 16; //-- 16, 32, 48
	int fontSize = 12;

	
	private Color recColor = new Color (249, 219,210); 
	private Color playColor = new Color(215,235,221);
	private Color barTextCol = new Color(120,140,120);

	
	private AudioRecorderListener recListener = null;
	private AudioPlayerListener playerListener = null;
	
	public AudioRecorderBar(){ //??EDT?
		this.setLayout(new GridBagLayout());
		audioBarFont = ResourceLoader.getFont("ubuntu", fontSize);

		playRecBar = new AudioProgressBar();
		playRecBar.setBarColor(playColor, Color.WHITE);
		playRecBar.setTextColor(barTextCol);
		playRecBar.setString("00:00.0");
		playRecBar.setFont(audioBarFont);
		playRecBar.isStringPainted(true);
		
//		playRecBar.setMaximum((int)maxRecTimeInMs);
		
		recIcon =  ResourceLoader.getIcon(IconType.REC, iconSize);
		stopIcon = ResourceLoader.getIcon(IconType.STOP, iconSize);
		playIcon = ResourceLoader.getIcon(IconType.PLAY, iconSize);
		pauseIcon = ResourceLoader.getIcon(IconType.PAUSE, iconSize);
		
		
		GridBagConstraints gbc = GbcFactory.getFillBoth();
//		gbc.gridy = 0;
//		add(playRecBar, gbc);
		Insets origInsets = gbc.insets;
		Insets pBarInsets = new Insets(/*top=*/2, /*left*/ 0, 2, 4);
		
//		gbc.gridx = 0;
//		gbc.weighty = 0.0;
//		gbc.weightx = 1.0;
//		gbc.gridwidth = 1;
		gbc.insets = pBarInsets ;
		gbc.fill = GridBagConstraints.BOTH;
		playRecBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		add(playRecBar, gbc);

		recBtn = new JToggleButton();
		recBtn.setIcon(recIcon);
		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.gridwidth = 1;
		gbc.ipadx = gbc.ipady = 0;
		gbc.insets = origInsets;
		gbc.fill = GridBagConstraints.NONE;
		add(recBtn, gbc);
		
		playBtn = new JToggleButton();
		playBtn.setIcon(playIcon);
		gbc.gridx = 2;
		add(playBtn, gbc);
		
//		playBtn.setEnabled(false);
		
		//-- setting listeners --//
		recBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				boolean reqRecStop = true;
				if(recBtn.isSelected()){
					recBtn.setIcon(stopIcon);
					reqRecStop = false;
//					recBtn.setSelected(false);
				}else{
					reqRecStop = true;
					recBtn.setIcon(recIcon);
				}
				if(AudioRecorderBar.this.recListener == null){
					LOG.warn("REC request - but no device!");
				}else{
					if(reqRecStop){
						recListener.reqRecStop();
					}else{
						recListener.reqRecStart();
					}
				}
			}
		});
		
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(playBtn.isSelected()){
					playBtn.setIcon(pauseIcon);
				}else{
					playBtn.setIcon(playIcon);
				}
				if(AudioRecorderBar.this.playerListener == null){
					LOG.error("Play request - but no device!");
				}else{
					playerListener.reqPauseToggle();
				}
			}
		});
	}
	
	
	@Override
	public void setRecActionListener(AudioRecorderListener l) {
		recListener = l;
	}
	
	
	@Override
	public void setPlayActionListener(AudioPlayerListener l) {
		playerListener = l;
	}
	
	
	@Override
	public void setRecordingState(){
		playRecBar.stopInfinitProgress();
		
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				playRecBar.setBarColor(recColor,Color.WHITE);
				playRecBar.setCursor(0);
				
				recBtn.setSelected(true);
				recBtn.setIcon(stopIcon);
				
				playBtn.setEnabled(false);
				playBtn.setSelected(false);
				playBtn.setIcon(playIcon);
			}
		});
	}

	@Override
	public void setReadyToRecState(){
		playRecBar.stopInfinitProgress();
		
		runOnEdt(
		new Runnable() {
			@Override
			public void run() {
				playRecBar.setCursor(0);
				
				recBtn.setEnabled(true);
				recBtn.setSelected(false);
				recBtn.setIcon(recIcon);
			}
		});
//		playRecBar.repaint();
	}


	@Override
	public void setBusyState() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					recBtn.setIcon(recIcon);
					recBtn.setSelected(false);
					recBtn.setEnabled(false);
					
					playBtn.setIcon(playIcon);
					playBtn.setSelected(false);
					playBtn.setEnabled(false);
					
					playRecBar.isDisplayTime(false);
					playRecBar.setString("collecting audio..");
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playRecBar.startInfinitProgress();
	}


	@Override
	public void setRecEnabled(boolean b){
		runOnEdt(new Runnable() {
			@Override
			public void run(){
				recBtn.setEnabled(false);
			}
		});
	}

	
	@Override
	public void setRecMaxPosInMs(final long ms){
		runOnEdt(new Runnable() {@Override
			public void run() {
				playRecBar.setMaximum(ms);
			}
		});
	}

	@Override
	public void setRecPosInMs(final long ms) {
		final long mins = ms/ (60*1000);
		final long secs = (ms-60*1000*mins) / 1000;
		final long mss = (ms-mins*60*1000-secs*1000)/100;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				playRecBar.setString(String.format("%02d:%02d.%1d", mins, secs, mss));
				playRecBar.setCursor(ms);
				playRecBar.repaint();
			}
		});
	}

	@Override
	public void setPlayingState(){
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				recBtn.setEnabled(false);
			}
		});
	}


	@Override
	public void setReadyToPlayState(){
//		System.out.println("ready2PLAY  " + playRecBar.getMaximum());
		playRecBar.stopInfinitProgress();
		
		runOnEdt(new Runnable() {
			@Override
			public void run() {
				playRecBar.setBarColor(playColor, Color.WHITE);
				playRecBar.setCursor(playRecBar.getMaximum());
//				System.out.println("Play max " + playRecBar.getMaximum());

				playBtn.setSelected(false);
				playBtn.setIcon(playIcon);
				playBtn.setEnabled(true);
				
//				recBtn.setEnabled(true);
//				recBtn.setIcon(recIcon);
//				recBtn.setSelected(false);
			}
		});
		playRecBar.repaint();
	}


	@Override
	public void setPlayMaxLenInMs(final long ms){
		playRecBar.setMaximum(ms);
		playRecBar.setString(String.format("%4.1f", ms/1000.0d));
	}


	@Override
	public void setPlayPosInMs(final long ms) {
		final long mins = ms/ (60*1000);
		final long secs = (ms-60*1000*mins) / 1000;
		final long mss = (ms-mins*60*1000-secs*1000)/100;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				playRecBar.setString(String.format("%02d:%02d.%1d", mins, secs, mss));
				playRecBar.setCursor(ms);
				playRecBar.repaint();
			}
		});
	}
	

	static void runOnEdt(Runnable doRun){
		if(SwingUtilities.isEventDispatchThread()){
			doRun.run();
		}else{
			SwingUtilities.invokeLater(doRun);
		}
	}


	@Override
	public void setPlayEnabled(boolean b) {
		playBtn.setSelected(false);
		playBtn.setEnabled(b);
		playBtn.setIcon(playIcon);
	}
	
}
