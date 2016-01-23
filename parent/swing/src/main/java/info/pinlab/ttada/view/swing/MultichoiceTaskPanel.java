package info.pinlab.ttada.view.swing;

import info.pinlab.ttada.core.model.MultichoiceTask;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.FontProvider;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.task.Task;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MultichoiceTaskPanel extends AbstractTaskPanel implements ActionListener{
	public static Logger logger = Logger.getLogger(MultichoiceTaskPanel.class);
	
	private final List<RespButton> buttons ;
	private static final Color btnGray;
	private static final Color btnActiveCol = new Color(245, 246, 184);

	private ResponseContent responseContent = null;
	

	private final JPanel respPanel ;
	private GridBagConstraints gbc;
	
	static{
		btnGray = new JButton().getBackground();
	}
	
	private static class RespButton extends JButton{
		private final Display disp;
		private RespButton (Display disp){
			this.disp = disp;
		}
	}
	
	public MultichoiceTaskPanel (){
		super();
		buttons = new ArrayList<RespButton>();
		
		respPanel = new JPanel();
		respPanel.setLayout(new GridBagLayout());
		gbc = GbcFactory.getRow();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.ipadx = 25; gbc.ipady = 25;
		gbc.weightx = 1.0;
		super.setBottomPanel(respPanel);
	}
	
	
	static RespButton getRespBtn (Display disp){
		RespButton btn = new RespButton(disp);
		if(disp instanceof TextDisplay){
			String label = ((TextDisplay)disp).getText();
			btn.setText(label);
		}
		if(disp instanceof FontProvider){
			Font font = ((FontProvider) disp).getFont();
			if(font==null){
				logger.error("Can't load font '" +  ((FontProvider) disp).getFontName() + "'");
			}else{
//				Font origFont = btn.getFont();
//				btn.setFont(origFont.deriveFont(origFont.getSize()*1.0f));
//				origFont.getAttributes()
//				System.out.println(font.deriveFont(20.0f));
//				System.out.println(origFont.getSize());
				btn.setFont(font.deriveFont(((FontProvider) disp).getFontSize()));
			}
		}
		return btn;
	}
	
	@Override
	public void setTask(Task task) {
		super.setTask(task);
		final MultichoiceTask multitask = (MultichoiceTask) task;
		int rowN  = multitask.getRowN();
		final int dispN = multitask.getChoiceN();
		int [] dispPerRow = new int [rowN];
		for (int i = 0; i < rowN; i++) {
			dispPerRow[i] = dispN / rowN;
		}
		int danglingN = dispN - rowN* (dispN / rowN); 
		int maxRowN =  dispN / rowN + (danglingN > 0 ? 1 : 0);
		for (int i = 0; i < danglingN; i++) {
			dispPerRow[rowN-(i+1)]++;
		}
		int dispX = -1;
		for(int rowX = 0 ; rowX < rowN ; rowX++){
			for(int colX = 0 ; colX < dispPerRow[rowX] ; colX++){
				if(		colX == (dispPerRow[rowX]-1) /* last button in row*/
						&& dispPerRow[rowX] < maxRowN   /* row has less btns than others */ ){
					gbc.gridwidth  = 2;	
				}else{
					gbc.gridwidth  = 1;	
				}
				dispX++;
				RespButton btn = getRespBtn(multitask.getChoiceX(dispX));
				buttons.add(btn);
				btn.addActionListener(this);

				gbc.gridx = colX;
				gbc.gridy = rowX;
//				System.out.println(gbc.gridy +"." + gbc.gridx + " " + gbc.gridwidth +"\t" + dispPerRow[rowX]);
				respPanel.add(btn, gbc);
			}
		}
	}
	

	@Override
	public void actionPerformed(ActionEvent e){
		RespButton respBtn = (RespButton) e.getSource();
		
		String btnFace = "";
		String btnVal = "";
		if(respBtn.disp instanceof TextDisplay){
			TextDisplay txtDisp = (TextDisplay) respBtn.disp;
			btnFace = txtDisp.getText();
			btnVal = txtDisp.getBrief();
		}
		long t1 = e.getWhen();
		
		responseContent = new ResponseContentText(t1, t1-displayT, btnFace, btnVal);
		for(RespButton button : buttons){
			button.setBackground(btnGray);
		}
		respBtn.setBackground(btnActiveCol);
		
		if(taskController!=null){
			super.taskController.enrollResponse(responseContent);
//-- 20131205- debugging
//			//-- enroll in separate thread
//			final TaskController control = super.taskController;
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					control.enrollResponse(responseContent);
//				}
//			}).start();
		}
	}

	@Override
	public ResponseContent getResponse(){
		if(responseContent==null){
			long t1 = System.currentTimeMillis();
			return new ResponseContentEmpty(t1, t1-displayT);
		}
		return responseContent;
	}

	@Override
	public void setState(Response response){
		responseContent = response.getContent();
		String txt  = "";
		if(responseContent instanceof ResponseContentText){
			txt = ((ResponseContentText)responseContent).getText();
		}
		for(RespButton button : buttons){
			if(txt.equals(button.getText())){
				button.setBackground(btnActiveCol);
			}else{
				button.setBackground(btnGray);
			}
		}
	}
}
