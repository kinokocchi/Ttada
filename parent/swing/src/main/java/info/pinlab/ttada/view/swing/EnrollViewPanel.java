package info.pinlab.ttada.view.swing;

import info.pinlab.ttada.core.control.EnrollController.State;
import info.pinlab.ttada.core.control.EnrollReqListener;
import info.pinlab.ttada.core.view.EnrollView;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


/**
 * 
 * To be used as a modal window, initiated from the main frame.
 * 
 * @author Gabor Pinter
 *
 */
@SuppressWarnings("serial")
public class EnrollViewPanel extends JPanel implements EnrollView, HasPanel {

	private JLabel msgLabel, statusLabel;
	private JProgressBar bar;
	private JButton alterSaveBtn, stopBtn;
//	private boolean isActive = true;
	private EnrollReqListener reqListener = null;
	
//	private volatile State state;

	
	
	public EnrollViewPanel(){
		this.setLayout(new GridBagLayout());

		//-- set label for messages
		msgLabel = new JLabel("Idle...");
		msgLabel.setOpaque(true);

		statusLabel = new JLabel("");
		statusLabel.setOpaque(true);
		
		GridBagConstraints gbc = GbcFactory.getFillBoth();
		gbc.insets = new Insets(5, 10, 0, 5);
		gbc.ipadx = 15;
		gbc.gridwidth = 2;
		gbc.gridx = gbc.gridy =  0;
		this.add(this.msgLabel, gbc);

		//-- set progress bar --//
		bar = new JProgressBar();
		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		gbc.ipady = 12;
		gbc.insets = new Insets(20, 5, 15, 5);
		
		
		bar.setStringPainted(true);
		bar.setString("Hello");
		bar.setMaximum(100);
		bar.setValue(23);
		this.add(bar, gbc);

		//-- set buttons
		alterSaveBtn = new JButton("Save as");
		stopBtn = new JButton("Stop");
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.ipady = 25;
		gbc.insets = new Insets(0, 5, 5, 2);
		this.add(alterSaveBtn, gbc);
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 2, 5, 5);
		this.add(stopBtn, gbc);
		
		alterSaveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(reqListener!=null) reqListener.reqFallbackEnroll();
			}
		});
		
//		gbc.gridx = 1;
//		gbc.insets = new Insets(0, 2, 5, 5);
//		this.add(stopBtn, gbc);

//		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
//		statusLabel.setBorder(border);
		statusLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		gbc.gridwidth = 2;
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.ipady = 2;
//		gbc.ipadx = 20;
		gbc.insets = new Insets(0, 10, 0, 10);
		this.add(statusLabel, gbc);
		statusLabel.setFont(
				statusLabel.getFont().deriveFont(10.0f));
		statusLabel.setText("#");
		
		
	}
	
//	private String log = "";
	
	@Override
	public void setLogMsg(final String msg) {
//		log = msg;
		TopPanel.runOnEdt( new Runnable() {
			@Override
			public void run(){
				statusLabel.setText("# "+msg);
			}
		});
	}

	@Override
	public void setStoreItemMax(final int n){
		TopPanel.runOnEdt( new Runnable() {
			@Override
			public void run() {
				bar.setMaximum(n);
				bar.setString( bar.getValue()  + " / " + n );
			}
		});
	}

	@Override
	public void setStoredItemN(final int n) {
		TopPanel.runOnEdt( new Runnable() {
			@Override
			public void run() {
				bar.setValue(n);
				bar.setString( n + " / " +  bar.getMaximum());
			}
		});
	}
	

	@Override
	public void showMessage(final String msg) {
		TopPanel.runOnEdt(new Runnable(){
			@Override
			public void run() {
				msgLabel.setText(msg);
				msgLabel.setForeground(Color.BLACK);
			}
		});
	}

	@Override
	public void showWarning(final String warn) {
		TopPanel.runOnEdt(new Runnable() {
			@Override
			public void run() {
				msgLabel.setText(warn);
				msgLabel.setForeground(Color.BLACK);
			}
		});

//		msgLabel.setBackground(Color.YELLOW);
	}

	@Override
	public void showError(final String err) {
		TopPanel.runOnEdt(new Runnable() {
			@Override
			public void run() {
				msgLabel.setText(err);
				msgLabel.setForeground(Color.WHITE);
			}
		});
//		msgLabel.setBackground(Color.RED);
	}

	@Override
	public void setEnrollReqListener(EnrollReqListener l) {
		reqListener = l;
	}

	

	
	
	
	

	@Override
	public JPanel getPanel() {
		return this;
	}


	
	@Override
	public void dispose(){
		reqListener = null;
		
		for(ActionListener l : stopBtn.getActionListeners()){
			stopBtn.removeActionListener(l);
		}
		for(ActionListener l : alterSaveBtn.getActionListeners()){
			alterSaveBtn.removeActionListener(l);
		}
		
		//-- dispose of parent frame
		JDialog dialog = null;
		JFrame frame = null;
		Container container = this;
		while(dialog==null && frame == null){
			container = container.getParent();
			if(container instanceof JFrame){
				((JFrame)container).dispose();
				return;
			}
			if(container instanceof JDialog){
				((JDialog)container).dispose();
				return;
			}
		}
	}
	
	
	
	
	private final ActionListener reqStopAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(reqListener!=null){
				reqListener.reqStopEnroll();
			}
		}
	};
	private final ActionListener reqStartAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(reqListener!=null)
				reqListener.reqStartEnroll();
		}
	};
	private final ActionListener reqExitAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(reqListener != null){
				reqListener.reqExitEnroll();
			}
		}
	};
	
	
	
	ActionListener stopBtnAction = null;
	
	
	@Override
	synchronized public void setState(final State state) {
		TopPanel.runOnEdt(new Runnable() {
			@Override
			public void run() {
				switch (state) {
				case NOT_STARTED:
					msgLabel.setText("Waiting to start..");
					
					alterSaveBtn.setEnabled(false);
					alterSaveBtn.setText("");
					
					stopBtn.setEnabled(false);
					stopBtn.setText("");
					
					break;
				case BUSY:
					msgLabel.setText("Busy uploading..");
					
					alterSaveBtn.setEnabled(true);
					
					stopBtn.setText("Cancel");
					stopBtn.setEnabled(true);
					if(reqStopAction != stopBtnAction){
						for(ActionListener l : stopBtn.getActionListeners())
							stopBtn.removeActionListener(l);
						stopBtn.addActionListener(reqStopAction);
					}
					break;
				case COMPLETED:
					msgLabel.setText("All your data is saved!");

					alterSaveBtn.setEnabled(true);
					alterSaveBtn.setText("Save elsewhere");

					stopBtn.setText("Exit");
					stopBtn.setEnabled(true);
					if(reqExitAction != stopBtnAction){
						for(ActionListener l : stopBtn.getActionListeners())
							stopBtn.removeActionListener(l);
						stopBtn.addActionListener(reqExitAction);
					}

					break;
				case INTERRUPTED:
					msgLabel.setText("Interrupted!");
					
					alterSaveBtn.setText("Save elsewhere");
					alterSaveBtn.setEnabled(true);
					
					stopBtn.setText("Restart");
					stopBtn.setEnabled(true);
					if(reqStartAction != stopBtnAction){
						for(ActionListener l : stopBtn.getActionListeners())
							stopBtn.removeActionListener(l);
						stopBtn.addActionListener(reqStartAction);
					}
					break;
				case IDLE:
					msgLabel.setText("Idle...");
					
					alterSaveBtn.setText("Save elsewhere");
					alterSaveBtn.setEnabled(true);
					
					stopBtn.setText("Cancel");
					stopBtn.setEnabled(true);
					if(reqStopAction != stopBtnAction){
						for(ActionListener l : stopBtn.getActionListeners())
							stopBtn.removeActionListener(l);
						stopBtn.addActionListener(reqStopAction);
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
	
	/**
	 * For manual testing 
	 */
	public static void main(String[] args) throws Exception{
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	        if ("Nimbus".equals(info.getName())) {
	            UIManager.setLookAndFeel(info.getClassName());
	            break;
	        }
	    }
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Local test");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				EnrollViewPanel panel = new EnrollViewPanel();
				panel.setLogMsg("Saved " + new File(".").getAbsolutePath() );
				panel.setState(State.COMPLETED);
				panel.setStoreItemMax(100);
				panel.setStoredItemN(23);
				
				frame.getContentPane().setLayout(new GridLayout(1, 1));
				frame.getContentPane().add(panel);
				
				frame.setSize(400,200);
				frame.setVisible(true);
			}
		});
	}






	
}
