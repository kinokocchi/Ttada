package info.pinlab.ttada.view.swing;


import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InfoTaskPanel extends AbstractTaskPanel {
	JButton okButton;
	
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


}
