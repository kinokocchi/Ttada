package info.pinlab.ttada.view.swing;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentText;



@SuppressWarnings("serial")
public class EntryTaskPanel extends AbstractTaskPanel
							implements ActionListener{
	private final JTextField txtResp ;
	private final JButton btn;
	private final JPanel respPanel; 

	private ResponseContent responseContent = null;
	
	
	public EntryTaskPanel(){
		super();

		respPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = GbcFactory.getRow();
		gbc.gridx = 0;
		gbc.ipady = 0;
		gbc.ipadx = 10;
		
		txtResp = new JTextField();
		respPanel.add(txtResp, gbc);
		Font origFont = txtResp.getFont();
		txtResp.setFont(origFont.deriveFont(24.0F));
		txtResp.addActionListener(this);
		
		btn = new JButton("Send");
		gbc.gridx = 1;
		gbc.ipady = 18;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		respPanel.add(btn, gbc);
		btn.addActionListener(this);
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				final String usrTxt = EntryTaskPanel.this.txtResp.getText();
				long rt = e.getWhen() - EntryTaskPanel.this.displayT;
				if (EntryTaskPanel.this.taskController != null){
					EntryTaskPanel.this.taskController.enrollResponse(new ResponseContentText(e.getWhen(), rt, usrTxt));
				}
			}
		});
		super.setBottomPanel(respPanel);
		new Thread(new Runnable() {
			@Override
			public void run() {
				txtResp.requestFocusInWindow();
			}
		}).start();

		//-- set focus (not let out!)
		txtResp.setFocusTraversalKeysEnabled(false);
		super.setDefaultFocus(txtResp);
	}

	

	@Override
	public ResponseContent getResponse() {
		long t1 = System.currentTimeMillis();
		return new ResponseContentText(super.displayT-t1, t1, txtResp.getText());
	}

	
	@Override
	public void setState(Response response) {
		ResponseContent content = response.getContent();
		if(content instanceof ResponseContentText){
			String usrTxt = ((ResponseContentText)content).getText();
			txtResp.setText(usrTxt);
			
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e){
		long t1 = e.getWhen();
		
		responseContent = new ResponseContentText(t1, t1-displayT, txtResp.getText(), txtResp.getText());

		if(super.taskController!=null){
			super.taskController.enrollResponse(responseContent);
		}
		System.out.println(responseContent);
	}
	
	
	
}
