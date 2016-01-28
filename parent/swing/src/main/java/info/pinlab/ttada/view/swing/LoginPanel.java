package info.pinlab.ttada.view.swing;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel implements ActionListener{

	private final JTextField idField;
	private final JPasswordField pwdField;
	private final JButton btn ;
	
	public LoginPanel(){
		setLayout(new GridBagLayout());	
		GridBagConstraints gbc = GbcFactory.getRow();
		gbc.ipadx = 10;
		gbc.ipady = 10;

		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JLabel msg = new JLabel("You need to login!", JLabel.CENTER); 
		this.add(msg, gbc);
		
		int margin = 5;
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(0, margin, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		JLabel idLab = new JLabel("Id");
		this.add(idLab, gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, margin);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		idField = new JTextField();
		this.add(idField, gbc);
		Font origFont = idField.getFont();
		idField.setFont(origFont.deriveFont(24.0F));
		idField.addActionListener(this);

		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(0, margin, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		JLabel pwdLab = new JLabel("Pwd");
		this.add(pwdLab, gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, margin);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pwdField = new JPasswordField();
		this.add(pwdField, gbc);
		pwdField.setFont(origFont.deriveFont(24.0F));
		pwdField.addActionListener(this);
		

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, margin, margin*10, margin);
		btn = new JButton("Login");
		this.add(btn, gbc);
		btn.setFont(origFont.deriveFont(24.0F));
		btn.addActionListener(this);
	}
	
	
	

	@Override
	public void actionPerformed(ActionEvent arg0){
		System.out.println("Login!");
		
		String id = idField.getText().trim();
		char[] pwd = pwdField.getPassword();
		
		
		
		
		// TODO Auto-generated method stub
	}

	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Login panel");
		frame.getContentPane().add(new LoginPanel());
				

		
		
		
		frame.setSize(300, 400);
		frame.setVisible(true);
		
	}



}
