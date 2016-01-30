package info.pinlab.ttada.view.swing;

import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.FontProvider;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.response.ResponseContentMulti;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.task.SurveyTask;
import info.pinlab.ttada.core.model.task.SurveyTaskEntry;
import info.pinlab.ttada.core.model.task.Task;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class SurveyTaskPanel extends AbstractTaskPanel {
	private final JButton okButton;
	private SurveyTask surveyTask = null;
	
	Map<SurveyTaskEntry, JTextField> entryMap = new HashMap<SurveyTaskEntry, JTextField>();
	
	
	public SurveyTaskPanel (){
		super();
		okButton =  new JButton("Ok!");
		
		GridBagConstraints gbc = GbcFactory.getRow();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.ipadx = 25; gbc.ipady = 25;
		gbc.weightx = 1.0;

		JPanel okPanel = new JPanel();
		okPanel.setLayout(new GridBagLayout());
		okPanel.add(okButton, gbc);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				
				if(surveyTask!=null){
					for(SurveyTaskEntry entry : surveyTask){
						if (entry.isObligatory()){
							String value  = entryMap.get(entry).getText().trim();
							if(value.isEmpty()){
								String msg = "'" + entry.getLabel() + "' can't be empty!";
								if(topView!=null){
									topView.showWarning("Input error!", msg);
								}
								LOG.warn(msg);
								return;
							}
						}
					}
				}
				
				
				TaskController controller = ((AbstractTaskPanel)SurveyTaskPanel.this).taskController;
				if(controller!=null){
					//-- check if 
					controller.reqNextByUsr();
				}
			}
		});
		super.setBottomPanel(okPanel);
	}
	
	
	@Override
	public void setTask(Task task){
		if(!(task instanceof SurveyTask)){
			throw new IllegalArgumentException("Task must be a '" + SurveyTask.class.getName() + "' class! Not '" + task.getClass().getName() + "'!");
		}
		surveyTask = (SurveyTask) task;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = GbcFactory.getFillBoth();
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;

		int gridy = 0;
		for(Display disp : surveyTask.getDisplays()){
			JLabel label = new JLabel(((TextDisplay)disp).getText());
			
			if(disp instanceof FontProvider){
				Font customFont = ((FontProvider)disp).getFont();
				String fontName = ((FontProvider)disp).getFontName();
				if(customFont == null){
					LOG.error("Can't set font '" + fontName  + "'");
				}else{
					LOG.debug("Setting font '" + fontName  + "'");
					customFont = customFont.deriveFont(((FontProvider)disp).getFontSize());
					label.setFont(customFont);
				}
			}
			label.setOpaque(true);
//			label.setBackground(Color.yellow);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			gbc.gridy = gridy++;
//			gbc.weighty = 0.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(label, gbc);
			
			modelViewMap.put(disp, label);
		}
		
		JPanel itemPanel =  new JPanel();
		itemPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcForItem = new GridBagConstraints();
		GridBagConstraints gbcForField = new GridBagConstraints();
		gbcForItem.gridy  = gbc.gridy+1;
		gbcForField.gridy = gbc.gridy+1;
		gbcForItem.gridx = 0;
		gbcForField.gridx = 1;
		
		gbcForItem.anchor = GridBagConstraints.LINE_START;
		gbcForItem.insets = new Insets(0, 10, 0, 10);
		gbcForItem.fill = GridBagConstraints.NONE;
		gbcForItem.weighty = 0.0;
		
		gbcForField.fill = GridBagConstraints.HORIZONTAL;
		gbcForField.weightx = 1.0;
		gbcForField.weighty = 0.0;
		gbcForField.ipadx = 10;
		gbcForField.insets = new Insets(0, 10, 0, 10);

		
		for(SurveyTaskEntry entry : surveyTask){
			gbcForItem.gridy++;
			gbcForField.gridy++;
			
			itemPanel.add(new JLabel(entry.getLabel()), gbcForItem);
			JTextField inputField = new JTextField();
			itemPanel.add(inputField, gbcForField);
			
			entryMap.put(entry, inputField);
			
		}
		
		gbc.gridx = 0; 
		gbc.gridy = gbcForItem.gridy+1;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JLabel filler = new JLabel("");
		itemPanel.add(filler , gbc);
		
		
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(itemPanel, gbc);
		
		super.modelViewMap.put(task, this);
		super.setTopPanel(panel);
	}
	
	
	
	
	
	@Override
	public ResponseContent getResponse(){
		long t1 = System.currentTimeMillis();
		long responseTime = t1-displayT;
		if(surveyTask!=null){
			List<ResponseContent> responseContents = new ArrayList<ResponseContent>();
			for(SurveyTaskEntry entry : surveyTask){
				String value  = entryMap.get(entry).getText().trim();
				ResponseContent responseContent = new ResponseContentText(t1, responseTime, value, entry.getBrief());
				responseContents.add(responseContent);
			}
			return new ResponseContentMulti(t1, responseTime, responseContents);
		}
		
		return new ResponseContentEmpty(t1, t1-super.displayT);
	}

	@Override
	public void setState(Response response) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
