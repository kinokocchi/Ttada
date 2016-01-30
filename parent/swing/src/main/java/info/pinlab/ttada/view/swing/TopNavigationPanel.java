package info.pinlab.ttada.view.swing;



import info.pinlab.ttada.core.control.StepReqListener;
import info.pinlab.ttada.core.view.NavigatorView;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TopNavigationPanel extends JPanel implements NavigatorView, HasPanel,
	KeyListener, ShortcutConsumer{
	private final JButton leftBtn, rightBtn;
	private final JLabel label ;
	private StepReqListener stepController;
	
	
	{
		shortcuts.add(KeyEvent.ALT_DOWN_MASK|KeyEvent.VK_RIGHT);
		shortcuts.add(KeyEvent.ALT_DOWN_MASK|KeyEvent.VK_LEFT);
		shortcuts.add(KeyEvent.VK_ENTER);
	}
	
	public TopNavigationPanel(){
		super();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		leftBtn = new JButton("<");
		leftBtn.setFocusable(false);
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		add(leftBtn, gbc);
		
		rightBtn = new JButton(">");
		rightBtn.setFocusable(false);

		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		add(rightBtn, gbc);
		
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		label = new JLabel("", JLabel.CENTER);
		add(label, gbc);
	}
	
	@Override
	public void setStepController(StepReqListener controller) {
		this.stepController = controller;
		for(ActionListener l : leftBtn.getActionListeners()){
			leftBtn.removeActionListener(l);
		}
		for(ActionListener l : rightBtn.getActionListeners()){
			rightBtn.removeActionListener(l);
		}
		
		leftBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(stepController!=null)
					stepController.reqPrevByUsr();
			}
		});
		
		rightBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				if(stepController!=null){
					stepController.reqNextByUsr();
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//						}
//					}).start();
				}
			}
		});
	}
	
	@Override
	public void setLabel(String s){
		label.setText(s);
	}
	
	
	@Override 
	public void setEnabled(boolean b){
		rightBtn.setEnabled(b);
		leftBtn.setEnabled(b);
	}
	

	@Override
	public JPanel getPanel() {
		return this;
	}

	
	static final Set<Integer> shortcuts = new HashSet<Integer>(); 
	
	@Override
	public Set<Integer> getShortcutKeys() {
		return shortcuts;
	}

	@Override
	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode() | key.getModifiersEx();
		if(	   keyCode == KeyEvent.VK_ENTER
			|| keyCode == ( KeyEvent.ALT_DOWN_MASK | KeyEvent.VK_RIGHT)){
			rightBtn.doClick();
		}
		if(keyCode == ( KeyEvent.ALT_DOWN_MASK | KeyEvent.VK_LEFT)){
			leftBtn.doClick();
		}
	}

	
	@Override
	public void keyReleased(KeyEvent ignore) {}
	@Override
	public void keyTyped(KeyEvent ignore) {	}
}
