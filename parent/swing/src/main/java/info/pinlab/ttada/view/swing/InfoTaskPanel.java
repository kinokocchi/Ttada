package info.pinlab.ttada.view.swing;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;

import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;

@SuppressWarnings("serial")
public class InfoTaskPanel extends AbstractTaskPanel 
			implements ShortcutConsumer{
	
	JButton okButton;
//	private static final Set<Integer> shortcuts = new HashSet<Integer>();
	protected static Set<Integer> shortcuts = new HashSet<Integer>();

	static{
		shortcuts.add(KeyEvent.VK_ENTER);
	}
	
	
	public InfoTaskPanel(){
		super();
		okButton = new JButton("Ok");
//		System.out.println("EDT ? " + SwingUtilities.isEventDispatchThread());
		
		GridBagConstraints gbc = GbcFactory.getRow();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.ipadx = 25; gbc.ipady = 25;
		gbc.weightx = 1.0;

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(okButton, gbc);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				TaskController controller = ((AbstractTaskPanel)InfoTaskPanel.this).taskController;
				if(controller!=null){
					controller.reqNextByUsr();
				}
			}
		});
		super.setBottomPanel(panel);

		this.setFocusable(false);
		this.setFocusTraversalKeysEnabled(false);
		okButton.setFocusable(false);
		okButton.setFocusTraversalKeysEnabled(false);
		
		//-- set focus (and not let out!)
//		super.setDefaultFocus(okButton);
	}

	@Override
	public ResponseContent getResponse() {
		long t1 = System.currentTimeMillis();
//		System.out.println("RT " + (t1-super.displayT)+"ms  " + super.displayT);
		return new ResponseContentEmpty(t1, t1-super.displayT);
	}

	@Override
	public void setState(Response response) {
		//-- do nothing here --//
	}

	@Override
	public void setEnabled(boolean b) {
		okButton.setEnabled(b);
	}

	@Override
	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode() | key.getModifiersEx();
		if(keyCode == KeyEvent.VK_ENTER){
			okButton.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
	}

	@Override
	public void keyTyped(KeyEvent key){
	}

	@Override
	public Set<Integer> getShortcutKeys() {
		return shortcuts;
	}
}
