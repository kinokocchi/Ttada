package info.pinlab.ttada.view.swing;


import info.pinlab.pinsound.app.AudioPlayerView;
import info.pinlab.ttada.core.control.AudioPlayController;
import info.pinlab.ttada.core.control.TaskController;
import info.pinlab.ttada.core.model.ExtendedResource;
import info.pinlab.ttada.core.model.display.AudioDisplay;
import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.FontProvider;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.display.TextInputDisplay;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.view.PlayerTopView;
import info.pinlab.ttada.view.swing.audio.AudioPlayerButton;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

/**
 * 
 *  __________________
 * |                  |
 * |     Display      |
 * |                  |
 * |~~~~~~~~~~~~~~~~~~|
 * |                  |
 * |     Response     |
 * |__________________|
 * 
 * 
 * 
 * @author Gabor Pinter
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractTaskPanel extends JPanel implements TaskViewPanel {
	public static Logger logger = Logger.getLogger(AbstractTaskPanel.class);
	
	private GridBagConstraints gbcTop;
	private GridBagConstraints gbcBottom ;
	TaskController taskController = null;
	private boolean isUnseen = true;
	long displayT = -1;
	AudioPlayerView audioBtn = null;
	AudioPlayController audioPlayerController = null;
	
	PlayerTopView topView = null;
	
	
	//-- to request focus when visible
	JComponent defaultFocusComp = null;

	
	Map<ExtendedResource<?>, Object> modelViewMap = new HashMap<ExtendedResource<?>, Object>();
	
	public AbstractTaskPanel(){
		setLayout(new GridBagLayout());
		gbcTop = GbcFactory.getFillBoth();
		gbcTop.gridy = 0;
		gbcBottom = GbcFactory.getRow();
		gbcBottom.ipady = 0;
		gbcBottom.gridy = 1;
//		addComponentListener(this);
		
		setFocusTraversalKeysEnabled(false);
	}
	
	public void setTopPanel(Component panel){
		this.add(panel, gbcTop);
	}
		

	public void setBottomPanel(Component panel, GridBagConstraints gbc){
		this.add(panel, gbc);
	}
	
	public void setBottomPanel(Component panel){
		setBottomPanel(panel, gbcBottom);
	}
	
	@Override
	public JPanel getPanel() {
		return this;
	}
	
	
	@Override
	public void setTaskController(TaskController controller){
		taskController = controller;
		
		//-- setup audio play controller, if any
//		if(audioBtn !=null){
//			if(controller instanceof AudioPlayerListener){
//				audioBtn.setPlayActionListener((AudioPlayerListener)controller);
//			}
//			if( controller instanceof AudioPlayController){
//				((AudioPlayController)controller).setAudioPlayerView(audioBtn);
//			}
//		}
	}
	
	/**
	 * Setting the top-panel of the window for displays
	 */
	@Override
	public void setTask(Task task){
		modelViewMap.put(task, this);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		int gridy = 0;
		for(Display disp : task.getDisplays()){
			GridBagConstraints gbc = GbcFactory.getFillBoth();
			if(disp instanceof TextDisplay){
				JLabel label = new JLabel(((TextDisplay)disp).getText());
				
				if(disp instanceof FontProvider){
					Font customFont = ((FontProvider)disp).getFont();
					String fontName = ((FontProvider)disp).getFontName();
					if(customFont == null){
						logger.error("Can't set font '" + fontName  + "'");
					}else{
						logger.debug("Setting font '" + fontName  + "'");
						customFont = customFont.deriveFont(((FontProvider)disp).getFontSize());
						label.setFont(customFont);
					}
				}
				label.setOpaque(true);
//				label.setBackground(Color.yellow);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				gbc.gridy = gridy++;
				gbc.weighty = 0.0;
				panel.add(label, gbc);
				modelViewMap.put(disp, label);
			}else if(disp instanceof AudioDisplay){
				gbc.weighty = 1.0;
				JPanel audioBtnContainerPanel = new JPanel();
				audioBtnContainerPanel.setLayout(new GridBagLayout());
				GridBagConstraints gbc2 = new GridBagConstraints();
				audioBtn = new AudioPlayerButton();
				audioBtnContainerPanel.add((AudioPlayerButton)audioBtn, gbc2);
				gbc.gridy = gridy++;
				panel.add(audioBtnContainerPanel, gbc);
				
				modelViewMap.put(disp, audioBtn);
				//-- setup bindings for the button:
				//TODO: cleanup: this should be unecessary, but check!
				if(taskController!=null){
//					setTaskController(taskController);
				}
			}else if(disp instanceof TextInputDisplay){
				TextInputDisplay txtInput = (TextInputDisplay)disp;
				
				JPanel itemPanel =  new JPanel();
				itemPanel.setLayout(new GridBagLayout());
				GridBagConstraints gbcForItem = new GridBagConstraints();
				GridBagConstraints gbcForField = new GridBagConstraints();
				gbcForItem.gridy  = -1;
				gbcForField.gridy = -1;
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
				
				
				for(TextDisplay txtItem : txtInput){
					gbcForItem.gridy++;
					gbcForField.gridy++;
					
					itemPanel.add(new JLabel(txtItem.getText()), gbcForItem);
					itemPanel.add(new JTextField(), gbcForField);
				}
				gbcForItem.gridy++;
				gbcForItem.gridwidth = 2;
				gbcForItem.weighty = 1.0;
				gbcForItem.fill = GridBagConstraints.BOTH;
				JLabel filler = new JLabel("");
				itemPanel.add(filler , gbcForItem);
				
				gbc.gridy = gridy++;
				panel.add(itemPanel, gbc);
				modelViewMap.put(disp, itemPanel);
				
			}else{
				throw new IllegalArgumentException("No gui for display type " + disp.getClass());
			}
			
			
		}
		setTopPanel(panel);
	}

	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(displayT<0){
        	setEnabled(true);
        	displayT = System.currentTimeMillis();
        }

        if(isUnseen && taskController!=null){
        	taskController.onViewVisible();
        	isUnseen=false;
        }

        //-- setting correct focus! --//
        if(defaultFocusComp!=null){
        	defaultFocusComp.requestFocus();
        }
	}
	
	@Override
	public void setDefaultFocus(JComponent comp) {
		defaultFocusComp = comp;
	}
	
	public Map<ExtendedResource<?>, Object> getModelViewMap(){
		return modelViewMap;
	}
	
	
	@Override
	public void setTop(PlayerTopView topView) {
		this.topView = topView;
	}
}





